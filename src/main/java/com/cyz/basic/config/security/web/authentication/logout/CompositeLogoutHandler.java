package com.cyz.basic.config.security.web.authentication.logout;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.core.Authentication;

public final class CompositeLogoutHandler implements LogoutHandler {

	private final List<LogoutHandler> logoutHandlers;

	public CompositeLogoutHandler(LogoutHandler... logoutHandlers) {
		Assert.notEmpty(logoutHandlers, "LogoutHandlers are required");
		this.logoutHandlers = Arrays.asList(logoutHandlers);
	}

	public CompositeLogoutHandler(List<LogoutHandler> logoutHandlers) {
		Assert.notEmpty(logoutHandlers, "LogoutHandlers are required");
		this.logoutHandlers = logoutHandlers;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		for (LogoutHandler handler : this.logoutHandlers) {
			handler.logout(request, response, authentication);
		}
	}

}
