package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.authentication.logout.LogoutHandler;
import com.cyz.basic.config.security.web.authentication.logout.SecurityContextLogoutHandler;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public final class LogoutConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<LogoutConfigurer<H>, H> {
	
	private List<LogoutHandler> logoutHandlers = new ArrayList<>();
	private SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
	private String logoutUrl = "/logout";
	private RequestMatcher logoutRequestMatcher;
	private boolean permitAll;
	private boolean customLogoutSuccess;
	
	/**
	 * Creates a new instance
	 * @see HttpSecurity#logout()
	 */
	public LogoutConfigurer() {
	}
	
	/**
	 * Adds a {@link LogoutHandler}. The {@link SecurityContextLogoutHandler} is added as
	 * the last {@link LogoutHandler} by default.
	 *
	 * @param logoutHandler the {@link LogoutHandler} to add
	 * @return the {@link LogoutConfigurer} for further customization
	 */
	public LogoutConfigurer<H> addLogoutHandler(LogoutHandler logoutHandler) {
		Assert.notNull(logoutHandler, "logoutHandler cannot be null");
		this.logoutHandlers.add(logoutHandler);
		return this;
	}
	
	/**
	 * Specifies if {@link SecurityContextLogoutHandler} should clear the {@link Authentication} at the time of logout.
	 * @param clearAuthentication true {@link SecurityContextLogoutHandler} should clear the {@link Authentication} (default), or false otherwise.
	 * @return the {@link LogoutConfigurer} for further customization
	 */
	public LogoutConfigurer<H> clearAuthentication(boolean clearAuthentication) {
		contextLogoutHandler.setClearAuthentication(clearAuthentication);
		return this;
	}
	
	/**
	 * The URL that triggers log out to occur (default is "/logout"). If CSRF protection
	 * is enabled (default), then the request must also be a POST. This means that by
	 * default POST "/logout" is required to trigger a log out. If CSRF protection is
	 * disabled, then any HTTP method is allowed.
	 *
	 * <p>
	 * It is considered best practice to use an HTTP POST on any action that changes state
	 * (i.e. log out) to protect against <a
	 * href="https://en.wikipedia.org/wiki/Cross-site_request_forgery">CSRF attacks</a>. If
	 * you really want to use an HTTP GET, you can use
	 * <code>logoutRequestMatcher(new AntPathRequestMatcher(logoutUrl, "GET"));</code>
	 * </p>
	 *
	 * @see #logoutRequestMatcher(RequestMatcher)
	 * @see HttpSecurity#csrf()
	 *
	 * @param logoutUrl the URL that will invoke logout.
	 * @return the {@link LogoutConfigurer} for further customization
	 */
	public LogoutConfigurer<H> logoutUrl(String logoutUrl) {
		this.logoutRequestMatcher = null;
		this.logoutUrl = logoutUrl;
		return this;
	}
	
	/**
	 * The RequestMatcher that triggers log out to occur. In most circumstances users will
	 * use {@link #logoutUrl(String)} which helps enforce good practices.
	 *
	 * @see #logoutUrl(String)
	 *
	 * @param logoutRequestMatcher the RequestMatcher used to determine if logout should
	 * occur.
	 * @return the {@link LogoutConfigurer} for further customization
	 */
	public LogoutConfigurer<H> logoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
		this.logoutRequestMatcher = logoutRequestMatcher;
		return this;
	}
	
	/**
	 * Gets the {@link LogoutHandler} instances that will be used.
	 * @return the {@link LogoutHandler} instances. Cannot be null.
	 */
	List<LogoutHandler> getLogoutHandlers() {
		return logoutHandlers;
	}

}
