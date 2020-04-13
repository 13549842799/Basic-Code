package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.authentication.logout.DelegatingLogoutSuccessHandler;
import com.cyz.basic.config.security.web.authentication.logout.LogoutFilter;
import com.cyz.basic.config.security.web.authentication.logout.LogoutHandler;
import com.cyz.basic.config.security.web.authentication.logout.LogoutSuccessHandler;
import com.cyz.basic.config.security.web.authentication.logout.SecurityContextLogoutHandler;
import com.cyz.basic.config.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import com.cyz.basic.config.security.web.util.matcher.AntPathRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.OrRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public final class LogoutConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<LogoutConfigurer<H>, H> {
	
	private List<LogoutHandler> logoutHandlers = new ArrayList<>();
	private SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
	private LogoutSuccessHandler logoutSuccessHandler;
	private String logoutUrl = "/logout";
	private RequestMatcher logoutRequestMatcher;
	private boolean permitAll;
	private boolean customLogoutSuccess;
	
	private LinkedHashMap<RequestMatcher, LogoutSuccessHandler> defaultLogoutSuccessHandlerMappings =
			new LinkedHashMap<>();
	
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
	 * A shortcut for {@link #permitAll(boolean)} with <code>true</code> as an argument.
	 * @return the {@link LogoutConfigurer} for further customizations
	 */
	public LogoutConfigurer<H> permitAll() {
		return permitAll(true);
	}
	
	/**
	 * Sets the {@link LogoutSuccessHandler} to use. If this is specified,
	 * {@link #logoutSuccessUrl(String)} is ignored.
	 *
	 * @param logoutSuccessHandler the {@link LogoutSuccessHandler} to use after a user
	 * has been logged out.
	 * @return the {@link LogoutConfigurer} for further customizations
	 */
	public LogoutConfigurer<H> logoutSuccessHandler(
			LogoutSuccessHandler logoutSuccessHandler) {
		this.customLogoutSuccess = true;
		this.logoutSuccessHandler = logoutSuccessHandler;
		return this;
	}
	
	/**
	 * Sets a default {@link LogoutSuccessHandler} to be used which prefers being invoked
	 * for the provided {@link RequestMatcher}. If no {@link LogoutSuccessHandler} is
	 * specified a {@link SimpleUrlLogoutSuccessHandler} will be used.
	 * If any default {@link LogoutSuccessHandler} instances are configured, then a
	 * {@link DelegatingLogoutSuccessHandler} will be used that defaults to a
	 * {@link SimpleUrlLogoutSuccessHandler}.
	 *
	 * @param handler the {@link LogoutSuccessHandler} to use
	 * @param preferredMatcher the {@link RequestMatcher} for this default
	 * {@link LogoutSuccessHandler}
	 * @return the {@link LogoutConfigurer} for further customizations
	 */
	public LogoutConfigurer<H> defaultLogoutSuccessHandlerFor(
			LogoutSuccessHandler handler, RequestMatcher preferredMatcher) {
		Assert.notNull(handler, "handler cannot be null");
		Assert.notNull(preferredMatcher, "preferredMatcher cannot be null");
		this.defaultLogoutSuccessHandlerMappings.put(preferredMatcher, handler);
		return this;
	}
	
	/**
	 * Grants access to the {@link #logoutSuccessUrl(String)} and the
	 * {@link #logoutUrl(String)} for every user.
	 *
	 * @param permitAll if true grants access, else nothing is done
	 * @return the {@link LogoutConfigurer} for further customization.
	 */
	public LogoutConfigurer<H> permitAll(boolean permitAll) {
		this.permitAll = permitAll;
		return this;
	}
	
	/**
	 * Gets the {@link LogoutSuccessHandler} if not null, otherwise creates a new
	 * {@link SimpleUrlLogoutSuccessHandler} using the {@link #logoutSuccessUrl(String)}.
	 *
	 * @return the {@link LogoutSuccessHandler} to use
	 */
	private LogoutSuccessHandler getLogoutSuccessHandler() {
		LogoutSuccessHandler handler = this.logoutSuccessHandler;
		if (handler == null) {
			handler = createDefaultSuccessHandler();
		}
		return handler;
	}
	
	private LogoutSuccessHandler createDefaultSuccessHandler() {
		SimpleUrlLogoutSuccessHandler urlLogoutHandler = new SimpleUrlLogoutSuccessHandler();

		if(defaultLogoutSuccessHandlerMappings.isEmpty()) {
			return urlLogoutHandler;
		}
		DelegatingLogoutSuccessHandler successHandler = new DelegatingLogoutSuccessHandler(defaultLogoutSuccessHandlerMappings);
		successHandler.setDefaultLogoutSuccessHandler(urlLogoutHandler);
		return successHandler;
	}
	
	@Override
	public void init(H http) throws Exception {
		if (permitAll) {
			//PermitAllSupport.permitAll(http, this.logoutSuccessUrl);
			PermitAllSupport.permitAll(http, this.getLogoutRequestMatcher(http));
		}

		/*DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
				.getSharedObject(DefaultLoginPageGeneratingFilter.class);
		if (loginPageGeneratingFilter != null && !isCustomLogoutSuccess()) {
			loginPageGeneratingFilter.setLogoutSuccessUrl(getLogoutSuccessUrl());
		}*/
	}

	@Override
	public void configure(H http) throws Exception {
		LogoutFilter logoutFilter = createLogoutFilter(http);
		http.addFilter(logoutFilter);
	}
	
	/**
	 * Returns true if the logout success has been customized via
	 * {@link #logoutSuccessUrl(String)} or
	 * {@link #logoutSuccessHandler(LogoutSuccessHandler)}.
	 *
	 * @return true if logout success handling has been customized, else false
	 */
	boolean isCustomLogoutSuccess() {
		return customLogoutSuccess;
	}
	
	/**
	 * Gets the {@link LogoutHandler} instances that will be used.
	 * @return the {@link LogoutHandler} instances. Cannot be null.
	 */
	List<LogoutHandler> getLogoutHandlers() {
		return logoutHandlers;
	}
	
	/**
	 * Creates the {@link LogoutFilter} using the {@link LogoutHandler} instances, the
	 * {@link #logoutSuccessHandler(LogoutSuccessHandler)} and the
	 * {@link #logoutUrl(String)}.
	 *
	 * @param http the builder to use
	 * @return the {@link LogoutFilter} to use.
	 * @throws Exception
	 */
	private LogoutFilter createLogoutFilter(H http) throws Exception {
		logoutHandlers.add(contextLogoutHandler);
		LogoutHandler[] handlers = logoutHandlers
				.toArray(new LogoutHandler[logoutHandlers.size()]);
		LogoutFilter result = new LogoutFilter(getLogoutSuccessHandler(), handlers);
		result.setLogoutRequestMatcher(getLogoutRequestMatcher(http));
		result = postProcess(result);
		return result;
	}
	
	//@SuppressWarnings("unchecked")
	private RequestMatcher getLogoutRequestMatcher(H http) {
		if (logoutRequestMatcher != null) {
			return logoutRequestMatcher;
		}
		//待改进 csrf
		//if (http.getConfigurer(CsrfConfigurer.class) != null) {
			//this.logoutRequestMatcher = new AntPathRequestMatcher(this.logoutUrl, "POST");
		//}
		//else {
			/*this.logoutRequestMatcher = new OrRequestMatcher(
				new AntPathRequestMatcher(this.logoutUrl, "GET"),
				new AntPathRequestMatcher(this.logoutUrl, "POST"),
				new AntPathRequestMatcher(this.logoutUrl, "PUT"),
				new AntPathRequestMatcher(this.logoutUrl, "DELETE")
			);*/
		    this.logoutRequestMatcher = new OrRequestMatcher(
				new AntPathRequestMatcher(this.logoutUrl, "POST")
			);
		//}
		return this.logoutRequestMatcher;
	}

}
