package com.cyz.basic.config.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import com.cyz.basic.config.SpringConfiguration.SpringMessageSource;
import com.cyz.basic.config.security.authentication.AuthenticationTrustResolver;
import com.cyz.basic.config.security.authentication.AuthenticationTrustResolverImpl;
import com.cyz.basic.config.security.authentication.WebAuthenticationDetails;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.core.context.SecurityContextHolder;
import com.cyz.basic.util.HttpUtil;
import com.cyz.basic.util.HttpUtil.RespParams;

public class AuthenticationTokenFilter implements Filter {
	
	protected final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
	protected final MessageSourceAccessor messages = SpringMessageSource.getAccess();
	protected final Log logger = LogFactory.getLog(getClass());
	
	private static final String DEFAULT_TOKEN = "x-token";
	
	private final String tokenName;
	
	public AuthenticationTokenFilter() {
		this.tokenName = DEFAULT_TOKEN;
	}
	
	public AuthenticationTokenFilter(String tokenName) {
		this.tokenName = tokenName;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		logger.info("begin to valid token");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || authenticationTrustResolver.isAnonymous(authentication)) {
			logger.info("if now is Anonymous,it don't need to valid token");
			chain.doFilter(request, response);
			return;
		}
		
		WebAuthenticationDetails obj = (WebAuthenticationDetails)authentication.getDetails();
		
		String token = req.getHeader(tokenName);
		
		if (StringUtils.isEmpty(obj.getToken())) {
			throw new IllegalArgumentException("token检验中: authentication为null:参数异常");
		}
 		if (StringUtils.isEmpty(token) || !token.equals(obj.getToken())) {
 			logger.info("token校验异常，旧的token:" + obj.getToken() + ", 新的token:" + token);
 			HttpUtil.responseResult(RespParams.create(req, resp).tokenError());
 			return;
 		}
 		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
