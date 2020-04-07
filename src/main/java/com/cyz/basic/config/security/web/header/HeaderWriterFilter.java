package com.cyz.basic.config.security.web.header;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cyz.basic.config.security.web.header.writers.CompositeHeaderWriter;
import com.cyz.basic.config.security.web.util.OnCommittedResponseWrapper;


public class HeaderWriterFilter extends OncePerRequestFilter {

	/**
	 * The {@link HeaderWriter} to write headers to the response.
	 * {@see CompositeHeaderWriter}
	 */
	private final HeaderWriter headerWriter;

	/**
	 * Indicates whether to write the headers at the beginning of the request.
	 */
	private boolean shouldWriteHeadersEagerly = false;

	/**
	 * Creates a new instance.
	 *
	 * @param headerWriters the {@link HeaderWriter} instances to write out headers to the
	 * {@link HttpServletResponse}.
	 */
	public HeaderWriterFilter(List<HeaderWriter> headerWriters) {
		Assert.notEmpty(headerWriters, "headerWriters cannot be null or empty");
		this.headerWriter = new CompositeHeaderWriter(headerWriters);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (this.shouldWriteHeadersEagerly) {
			doHeadersBefore(request, response, filterChain);
		} else {
			doHeadersAfter(request, response, filterChain);
		}
	}

	private void doHeadersBefore(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		this.headerWriter.writeHeaders(request, response);
		filterChain.doFilter(request, response);
	}

	private void doHeadersAfter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HeaderWriterResponse headerWriterResponse = new HeaderWriterResponse(request,
				response, this.headerWriter);
		HeaderWriterRequest headerWriterRequest = new HeaderWriterRequest(request,
				headerWriterResponse);
		try {
			filterChain.doFilter(headerWriterRequest, headerWriterResponse);
		} finally {
			headerWriterResponse.writeHeaders();
		}
	}

	/**
	 * Allow writing headers at the beginning of the request.
	 *
	 * @param shouldWriteHeadersEagerly boolean to allow writing headers at the beginning of the request.
	 * @author Ankur Pathak
	 * @since 5.2
	 */
	public void setShouldWriteHeadersEagerly(boolean shouldWriteHeadersEagerly) {
		this.shouldWriteHeadersEagerly = shouldWriteHeadersEagerly;
	}

	static class HeaderWriterResponse extends OnCommittedResponseWrapper {
		private final HttpServletRequest request;
		private final HeaderWriter headerWriter;

		HeaderWriterResponse(HttpServletRequest request, HttpServletResponse response,
				HeaderWriter headerWriter) {
			super(response);
			this.request = request;
			this.headerWriter = headerWriter;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.springframework.security.web.util.OnCommittedResponseWrapper#
		 * onResponseCommitted()
		 */
		@Override
		protected void onResponseCommitted() {
			writeHeaders();
			this.disableOnResponseCommitted();
		}

		protected void writeHeaders() {
			if (isDisableOnResponseCommitted()) {
				return;
			}
			this.headerWriter.writeHeaders(this.request, getHttpResponse());
		}

		private HttpServletResponse getHttpResponse() {
			return (HttpServletResponse) getResponse();
		}
	}

	static class HeaderWriterRequest extends HttpServletRequestWrapper {
		private final HeaderWriterResponse response;

		HeaderWriterRequest(HttpServletRequest request, HeaderWriterResponse response) {
			super(request);
			this.response = response;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			return new HeaderWriterRequestDispatcher(super.getRequestDispatcher(path), this.response);
		}
	}

	static class HeaderWriterRequestDispatcher implements RequestDispatcher {
		private final RequestDispatcher delegate;
		private final HeaderWriterResponse response;

		HeaderWriterRequestDispatcher(RequestDispatcher delegate, HeaderWriterResponse response) {
			this.delegate = delegate;
			this.response = response;
		}

		@Override
		public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
			this.delegate.forward(request, response);
		}

		@Override
		public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
			this.response.onResponseCommitted();
			this.delegate.include(request, response);
		}
	}

}
