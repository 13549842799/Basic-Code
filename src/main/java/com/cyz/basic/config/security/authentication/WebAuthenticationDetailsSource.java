package com.cyz.basic.config.security.authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of {@link AuthenticationDetailsSource} which builds the details object
 * from an <tt>HttpServletRequest</tt> object, creating a {@code WebAuthenticationDetails}
 * .
 *
 * @author Ben Alex
 */
public class WebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

	/**
	 * @param context the {@code HttpServletRequest} object.
	 * @return the {@code WebAuthenticationDetails} containing information about the
	 * current request
	 */
	@Override
	public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
		return new WebAuthenticationDetails(context);
	}

}
