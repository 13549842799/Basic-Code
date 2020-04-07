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
import org.springframework.web.filter.GenericFilterBean;

public class AutoLoginFilter /*extends GenericFilterBean implements MessageSourceAware*/{
	
	
	private static final String FILTER_APPLIED = "__spring_security_auto_login_filter_applied";

	/*@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		
		
	}*/


}
