package com.cyz.basic.config.security.web.authentication.logout;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.core.context.SecurityContextHolder;
import com.cyz.basic.config.security.web.UrlUtils;
import com.cyz.basic.config.security.web.util.matcher.AntPathRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public class LogoutFilter extends GenericFilterBean {
	
	// ~ Instance fields
	// ================================================================================================

	private RequestMatcher logoutRequestMatcher;

	private final LogoutHandler handler;
	private final LogoutSuccessHandler logoutSuccessHandler;

	// ~ Constructors
	// ===================================================================================================
	/**
	 * Constructor which takes a <tt>LogoutSuccessHandler</tt> instance to determine the
	 * target destination after logging out. The list of <tt>LogoutHandler</tt>s are
	 * intended to perform the actual logout functionality (such as clearing the security
	 * context, invalidating the session, etc.).
	 */
	public LogoutFilter(LogoutSuccessHandler logoutSuccessHandler,
			LogoutHandler... handlers) {
		this.handler = new CompositeLogoutHandler(handlers);
		Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler cannot be null");
		this.logoutSuccessHandler = logoutSuccessHandler;
		setFilterProcessesUrl("/logout");
	}

	public LogoutFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
		this.handler = new CompositeLogoutHandler(handlers);
		Assert.isTrue(
				!StringUtils.hasLength(logoutSuccessUrl)
						|| UrlUtils.isValidRedirectUrl(logoutSuccessUrl),
				() -> logoutSuccessUrl + " isn't a valid redirect URL");
		SimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
		if (StringUtils.hasText(logoutSuccessUrl)) {
			urlLogoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
		}
		logoutSuccessHandler = urlLogoutSuccessHandler;
		setFilterProcessesUrl("/logout");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		if (requiresLogout(request, response)) {
			logger.info("getAuthentication in logoutFilter");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			
			if (logger.isDebugEnabled()) {
				logger.debug("Logging out user '" + auth
						+ "' and transferring to logout destination");
			}
			
			handler.logout(request, response, auth);
			
			logoutSuccessHandler.onLogoutSuccess(request, response, auth);
		}
		
		chain.doFilter(request, response);
	}
	
	/**
	 * Allow subclasses to modify when a logout should take place.
	 *
	 * @param request the request
	 * @param response the response
	 *
	 * @return <code>true</code> if logout should occur, <code>false</code> otherwise
	 */
	protected boolean requiresLogout(HttpServletRequest request,
			HttpServletResponse response) {
		return logoutRequestMatcher.matches(request);
	}

	public void setLogoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
		Assert.notNull(logoutRequestMatcher, "logoutRequestMatcher cannot be null");
		this.logoutRequestMatcher = logoutRequestMatcher;
	}

	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.logoutRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
	}

}
