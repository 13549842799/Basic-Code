package com.cyz.basic.config.security.web.authentication.logout;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;

/**
 * 参照类SimpleUrlLogoutSuccessHandler
 * @author cyz
 *
 */
public class SimpleUrlLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
		implements LogoutSuccessHandler {

	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		super.handle(request, response, authentication);
	}

}
