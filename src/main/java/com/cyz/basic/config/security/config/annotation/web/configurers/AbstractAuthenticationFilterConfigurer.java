package com.cyz.basic.config.security.config.annotation.web.configurers;


import javax.servlet.http.HttpServletRequest;

import com.cyz.basic.config.security.authentication.AuthenticationDetailsSource;
import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.authentication.AuthenticationSuccessHandler;
import com.cyz.basic.config.security.authentication.SimpleAuthenticationSuccessHandler;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.authentication.AbstractAuthenticationProcessingFilter;
import com.cyz.basic.config.security.web.authentication.AuthenticationFailureHandler;
import com.cyz.basic.config.security.web.authentication.RememberMeServices;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public abstract class AbstractAuthenticationFilterConfigurer<B extends HttpSecurityBuilder<B>, T extends AbstractAuthenticationFilterConfigurer<B, T, F>, F extends AbstractAuthenticationProcessingFilter> 
    extends AbstractHttpConfigurer<T, B>{

	private F authFilter;
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
	private boolean enableLoginPage; //是否启用登录页
	private String loginPage;
	private String loginProcessingUrl;
	
	private SimpleAuthenticationSuccessHandler defaultSuccessHandler = new SimpleAuthenticationSuccessHandler();
	private AuthenticationSuccessHandler successHandler = this.defaultSuccessHandler;
	
	private AuthenticationFailureHandler failureHandler;
	
	private boolean permitAll;

	private String failureUrl;
	
	/**
	 * Creates a new instance with minimal defaults
	 */
	protected AbstractAuthenticationFilterConfigurer() {
		this.enableLoginPage = false;
	}
	
	/**
	 * Creates a new instance
	 * @param authenticationFilter the {@link AbstractAuthenticationProcessingFilter} to
	 * use
	 * @param defaultLoginProcessingUrl the default URL to use for
	 * {@link #loginProcessingUrl(String)}
	 */
	protected AbstractAuthenticationFilterConfigurer(F authenticationFilter,
			String defaultLoginProcessingUrl) {
		this();
		this.authFilter = authenticationFilter;
		if (defaultLoginProcessingUrl != null) {
			loginProcessingUrl(defaultLoginProcessingUrl);
		}
	}
	
	/**
	 * Specifies where users will be redirected after authenticating successfully if
	 * they have not visited a secured page prior to authenticating. This is a shortcut
	 * for calling {@link #defaultSuccessUrl(String, boolean)}.
	 *
	 * @param defaultSuccessUrl the default success url
	 * @return the {@link FormLoginConfigurer} for additional customization
	 */
	public final T defaultSuccessUrl(String defaultSuccessUrl) {
		return defaultSuccessUrl(defaultSuccessUrl, false);
	}

	/**
	 * Specifies where users will be redirected after authenticating successfully if
	 * they have not visited a secured page prior to authenticating or {@code alwaysUse}
	 * is true. This is a shortcut for calling
	 * {@link #successHandler(AuthenticationSuccessHandler)}.
	 *
	 * @param defaultSuccessUrl the default success url
	 * @param alwaysUse true if the {@code defaultSuccesUrl} should be used after
	 * authentication despite if a protected page had been previously visited
	 * @return the {@link FormLoginConfigurer} for additional customization
	 */
	public final T defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse) {
		SimpleAuthenticationSuccessHandler handler = new SimpleAuthenticationSuccessHandler();
		/*handler.setDefaultTargetUrl(defaultSuccessUrl);
		handler.setAlwaysUseDefaultTargetUrl(alwaysUse);*/
		this.defaultSuccessHandler = handler;
		return successHandler(handler);
	}
	
	/**
	 * Specifies the URL to validate the credentials.
	 *
	 * @param loginProcessingUrl the URL to validate username and password
	 * @return the {@link FormLoginConfigurer} for additional customization
	 */
	public T loginProcessingUrl(String loginProcessingUrl) {
		this.loginProcessingUrl = loginProcessingUrl;
		authFilter
				.setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
		return getSelf();
	}
	
	/**
	 * Specifies the {@link AuthenticationSuccessHandler} to be used. The default is
	 * {@link SavedRequestAwareAuthenticationSuccessHandler} with no additional properites
	 * set.
	 *
	 * @param successHandler the {@link AuthenticationSuccessHandler}.
	 * @return the {@link FormLoginConfigurer} for additional customization
	 */
	public final T successHandler(AuthenticationSuccessHandler successHandler) {
		this.successHandler = successHandler;
		return getSelf();
	}
	
	/**
	 * Create the {@link RequestMatcher} given a loginProcessingUrl
	 * @param loginProcessingUrl creates the {@link RequestMatcher} based upon the
	 * loginProcessingUrl
	 * @return the {@link RequestMatcher} to use based upon the loginProcessingUrl
	 */
	protected abstract RequestMatcher createLoginProcessingUrlMatcher(
			String loginProcessingUrl);
	
	
	/**
	 * Equivalent of invoking permitAll(true)
	 * @return the {@link FormLoginConfigurer} for additional customization
	 */
	public final T permitAll() {
		return permitAll(true);
	}

	/**
	 * Ensures the urls for {@link #failureUrl(String)} as well as for the {@link HttpSecurityBuilder}, the
	 * {@link #getLoginPage} and {@link #getLoginProcessingUrl} are granted access to any user.
	 *
	 * @param permitAll true to grant access to the URLs false to skip this step
	 * @return the {@link FormLoginConfigurer} for additional customization
	 */
	public final T permitAll(boolean permitAll) {
		this.permitAll = permitAll;
		return getSelf();
	}
	
	@Override
	public void configure(B http) throws Exception {

		authFilter.setAuthenticationManager(http
				.getSharedObject(AuthenticationManager.class));
		authFilter.setAuthenticationSuccessHandler(successHandler);
		authFilter.setAuthenticationFailureHandler(failureHandler);
		if (authenticationDetailsSource != null) {
			authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
		}
		/*SessionAuthenticationStrategy sessionAuthenticationStrategy = http
				.getSharedObject(SessionAuthenticationStrategy.class);
		if (sessionAuthenticationStrategy != null) {
			authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		}*/
		RememberMeServices rememberMeServices = http
				.getSharedObject(RememberMeServices.class);
		if (rememberMeServices != null) {
			authFilter.setRememberMeServices(rememberMeServices);
		}
		F filter = postProcess(authFilter);
		http.addFilter(filter);
	}
	
	/**
	 * Gets the Authentication Filter
	 *
	 * @return the Authentication Filter
	 */
	protected final F getAuthenticationFilter() {
		return authFilter;
	}

	/**
	 * Sets the Authentication Filter
	 *
	 * @param authFilter the Authentication Filter
	 */
	protected final void setAuthenticationFilter(F authFilter) {
		this.authFilter = authFilter;
	}
	
	/**
	 * Updates the default values for access.
	 */
	protected final void updateAccessDefaults(B http) {
		if (permitAll) {
			PermitAllSupport.permitAll(http, loginPage, loginProcessingUrl, failureUrl);
		}
	}
	
	@SuppressWarnings("unchecked")
	private T getSelf() {
		return (T) this;
	}
}
