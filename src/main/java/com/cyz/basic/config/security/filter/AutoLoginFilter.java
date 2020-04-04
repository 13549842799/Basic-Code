package com.cyz.basic.config.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class AutoLoginFilter extends GenericFilterBean implements MessageSourceAware{
	
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	
	private static final String FILTER_APPLIED = "__spring_security_auto_login_filter_applied";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (request.getAttribute(FILTER_APPLIED) != null) {
			chain.doFilter(request, response);
			return;
		}
		final boolean debug = logger.isDebugEnabled();
		
		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
		
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			if (debug) {
				logger.debug("context is null,so it not run continue!");
			}
			chain.doFilter(req, res);
			return;
		}
		
		Authentication auth = context.getAuthentication();
		if (auth != null) {
			if (debug) {
				logger.debug("it has auth now!");
			}
			chain.doFilter(req, res);
			return;
		}
		
		
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

}
