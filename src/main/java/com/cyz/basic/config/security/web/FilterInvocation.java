package com.cyz.basic.config.security.web;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Holds objects associated with a HTTP filter.
 * <P>
 * Guarantees the request and response are instances of <code>HttpServletRequest</code>
 * and <code>HttpServletResponse</code>, and that there are no <code>null</code> objects.
 * <p>
 * Required so that security system classes can obtain access to the filter environment,
 * as well as the request and response.
 *
 * @author Ben Alex
 * @author colin sampaleanu
 * @author Luke Taylor
 * @author Rob Winch
 */
public class FilterInvocation {
	// ~ Static fields
	// ==================================================================================================
	static final FilterChain DUMMY_CHAIN = new FilterChain() {
		public void doFilter(ServletRequest req, ServletResponse res)
				throws IOException, ServletException {
			throw new UnsupportedOperationException("Dummy filter chain");
		}
	};
	
	private FilterChain chain;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public FilterInvocation(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		if ((request == null) || (response == null) || (chain == null)) {
			throw new IllegalArgumentException("Cannot pass null values to constructor");
		}

		this.request = (HttpServletRequest) request;
		this.response = (HttpServletResponse) response;
		this.chain = chain;
	}

	public FilterChain getChain() {
		return chain;
	}
	
	public String getRequestUrl() {
		return UrlUtils.buildRequestUrl(this.request);
	}
	
	public String getFullRequestUrl() {
		return UrlUtils.buildFullRequestUrl(this.request);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}
	
	@Override
	public String toString() {
		return "FilterInvocation: URL: " + getRequestUrl();
	}
	
}

class DummyRequest extends HttpServletRequestWrapper {
	private static final HttpServletRequest UNSUPPORTED_REQUEST = (HttpServletRequest) Proxy
			.newProxyInstance(DummyRequest.class.getClassLoader(),
					new Class[] { HttpServletRequest.class },
					new UnsupportedOperationExceptionInvocationHandler());

	private String requestURI;
	private String contextPath = "";
	private String servletPath;
	private String pathInfo;
	private String queryString;
	private String method;

	public DummyRequest() {
		super(UNSUPPORTED_REQUEST);
	}

	public String getCharacterEncoding() {
		return "UTF-8";
	}

	public Object getAttribute(String attributeName) {
		return null;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public String getContextPath() {
		return this.contextPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	@Override
	public String getServletPath() {
		return this.servletPath;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getPathInfo() {
		return this.pathInfo;
	}

	@Override
	public String getQueryString() {
		return this.queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}

final class UnsupportedOperationExceptionInvocationHandler implements InvocationHandler {
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		throw new UnsupportedOperationException(method + " is not supported");
	}
}
