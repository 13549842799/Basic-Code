package com.cyz.basic.config.security.web.header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Contract for writing headers to a {@link HttpServletResponse}
 *
 * @see HeaderWriterFilter
 *
 * @author Marten Deinum
 * @author Rob Winch
 * @since 3.2
 */
public interface HeaderWriter {

	/**
	 * Create a {@code Header} instance.
	 *
	 * @param request the request
	 * @param response the response
	 */
	void writeHeaders(HttpServletRequest request, HttpServletResponse response);
}