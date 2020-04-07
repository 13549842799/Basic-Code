package com.cyz.basic.config.security.web.authentication.logout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyz.basic.config.security.core.Authentication;

public interface LogoutHandler {
	/**
	 * Causes a logout to be completed. The method must complete successfully.
	 *
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @param authentication the current principal details
	 */
	void logout(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication);
}
