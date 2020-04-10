package com.cyz.basic.config.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.exception.AuthenticationException;
import com.cyz.basic.config.security.web.AuthenticationEntryPoint;

/**
 * An {@link AuthenticationEntryPoint} that sends a generic {@link HttpStatus} as a
 * response. Useful for JavaScript clients which cannot use Basic authentication since the
 * browser intercepts the response.
 *
 * @author Rob Winch
 * @since 4.0
 */
public final class HttpStatusEntryPoint implements AuthenticationEntryPoint {
	private final HttpStatus httpStatus;

	/**
	 * Creates a new instance.
	 *
	 * @param httpStatus the HttpStatus to set
	 */
	public HttpStatusEntryPoint(HttpStatus httpStatus) {
		Assert.notNull(httpStatus, "httpStatus cannot be null");
		this.httpStatus = httpStatus;
	}

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(httpStatus.value());
	}
}
