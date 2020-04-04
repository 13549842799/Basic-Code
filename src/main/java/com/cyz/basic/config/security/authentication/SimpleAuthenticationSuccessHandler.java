package com.cyz.basic.config.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyz.basic.config.security.core.Authentication;


public class SimpleAuthenticationSuccessHandler extends AbstractAuthenticationRestfulHandler
		implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		handle(request, response, authentication);
	}

}
