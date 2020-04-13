package com.cyz.basic.config.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.util.HttpUtil.RespParams;


public class SimpleAuthenticationSuccessHandler extends AbstractAuthenticationRestfulHandler
		implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp,
			Authentication authentication) throws IOException, ServletException {
			
		handle(req, resp, RespParams.create(req, resp).success(authentication.result()));
		
	}

}
