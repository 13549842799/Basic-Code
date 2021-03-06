package com.cyz.basic.config.security.config.annotation.web.configurers;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import com.cyz.basic.config.security.authentication.AuthenticationDetailsSource;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.AuthenticationEntryPoint;
import com.cyz.basic.config.security.web.authentication.DelegatingAuthenticationEntryPoint;
import com.cyz.basic.config.security.web.authentication.HttpStatusEntryPoint;
import com.cyz.basic.config.security.web.util.matcher.RequestHeaderRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;
import com.cyz.basic.config.security.web.www.BasicAuthenticationEntryPoint;

/**
 * Adds HTTP basic based authentication. All attributes have reasonable defaults making
 * all parameters are optional.
 *
 * <h2>Security Filters</h2>
 *
 * The following Filters are populated
 *
 * <ul>
 * <li>
 * {@link BasicAuthenticationFilter}</li>
 * </ul>
 *
 * <h2>Shared Objects Created</h2>
 *
 * <ul>
 * <li>AuthenticationEntryPoint - populated with the
 * {@link #authenticationEntryPoint(AuthenticationEntryPoint)} (default
 * {@link BasicAuthenticationEntryPoint})</li>
 * </ul>
 *
 * <h2>Shared Objects Used</h2>
 *
 * The following shared objects are used:
 *
 * <ul>
 * <li>{@link AuthenticationManager}</li>
 * <li>{@link RememberMeServices}</li>
 * </ul>
 *
 * @author Rob Winch
 * @since 3.2
 */
public class HttpBasicConfigurer<B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<HttpBasicConfigurer<B>, B> {
	
	private static final RequestHeaderRequestMatcher X_REQUESTED_WITH = new RequestHeaderRequestMatcher("X-Requested-With",
			"XMLHttpRequest");

	private static final String DEFAULT_REALM = "Realm";

	private AuthenticationEntryPoint authenticationEntryPoint;
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
	private BasicAuthenticationEntryPoint basicAuthEntryPoint = new BasicAuthenticationEntryPoint();

	/**
	 * Creates a new instance
	 * @throws Exception
	 * @see HttpSecurity#httpBasic()
	 */
	public HttpBasicConfigurer() throws Exception {
		realmName(DEFAULT_REALM);

		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
		entryPoints.put(X_REQUESTED_WITH, new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

		DelegatingAuthenticationEntryPoint defaultEntryPoint = new DelegatingAuthenticationEntryPoint(
				entryPoints);
		defaultEntryPoint.setDefaultEntryPoint(this.basicAuthEntryPoint);
		this.authenticationEntryPoint = defaultEntryPoint;
	}

	/**
	 * Allows easily changing the realm, but leaving the remaining defaults in place. If
	 * {@link #authenticationEntryPoint(AuthenticationEntryPoint)} has been invoked,
	 * invoking this method will result in an error.
	 *
	 * @param realmName the HTTP Basic realm to use
	 * @return {@link HttpBasicConfigurer} for additional customization
	 * @throws Exception
	 */
	public HttpBasicConfigurer<B> realmName(String realmName) throws Exception {
		this.basicAuthEntryPoint.setRealmName(realmName);
		this.basicAuthEntryPoint.afterPropertiesSet();
		return this;
	}

	/**
	 * The {@link AuthenticationEntryPoint} to be populated on
	 * {@link BasicAuthenticationFilter} in the event that authentication fails. The
	 * default to use {@link BasicAuthenticationEntryPoint} with the realm
	 * "Realm".
	 *
	 * @param authenticationEntryPoint the {@link AuthenticationEntryPoint} to use
	 * @return {@link HttpBasicConfigurer} for additional customization
	 */
	public HttpBasicConfigurer<B> authenticationEntryPoint(
			AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
		return this;
	}

	/**
	 * Specifies a custom {@link AuthenticationDetailsSource} to use for basic
	 * authentication. The default is {@link WebAuthenticationDetailsSource}.
	 *
	 * @param authenticationDetailsSource the custom {@link AuthenticationDetailsSource}
	 * to use
	 * @return {@link HttpBasicConfigurer} for additional customization
	 */
	public HttpBasicConfigurer<B> authenticationDetailsSource(
			AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
		return this;
	}

	@Override
	public void init(B http) throws Exception {
		registerDefaults(http);
	}

	private void registerDefaults(B http) {
		ContentNegotiationStrategy contentNegotiationStrategy = http
				.getSharedObject(ContentNegotiationStrategy.class);
		if (contentNegotiationStrategy == null) {
			contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
		}

		/*MediaTypeRequestMatcher restMatcher = new MediaTypeRequestMatcher(
				contentNegotiationStrategy, MediaType.APPLICATION_ATOM_XML,
				MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML,
				MediaType.MULTIPART_FORM_DATA, MediaType.TEXT_XML);
		restMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));

		MediaTypeRequestMatcher allMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy, MediaType.ALL);
		allMatcher.setUseEquals(true);

		RequestMatcher notHtmlMatcher = new NegatedRequestMatcher(
				new MediaTypeRequestMatcher(contentNegotiationStrategy,
						MediaType.TEXT_HTML));
		RequestMatcher restNotHtmlMatcher = new AndRequestMatcher(
				Arrays.<RequestMatcher>asList(notHtmlMatcher, restMatcher));

		RequestMatcher preferredMatcher = new OrRequestMatcher(Arrays.asList(X_REQUESTED_WITH, restNotHtmlMatcher, allMatcher));

		registerDefaultEntryPoint(http, preferredMatcher);
		registerDefaultLogoutSuccessHandler(http, preferredMatcher);*/
	}

	private void registerDefaultEntryPoint(B http, RequestMatcher preferredMatcher) {
		ExceptionHandlingConfigurer<B> exceptionHandling = http
				.getConfigurer(ExceptionHandlingConfigurer.class);
		if (exceptionHandling == null) {
			return;
		}
		/*exceptionHandling.defaultAuthenticationEntryPointFor(
				postProcess(this.authenticationEntryPoint), preferredMatcher);*/
	}

	private void registerDefaultLogoutSuccessHandler(B http, RequestMatcher preferredMatcher) {
		LogoutConfigurer<B> logout = http
				.getConfigurer(LogoutConfigurer.class);
		if (logout == null) {
			return;
		}
		/*LogoutConfigurer<B> handler = logout.defaultLogoutSuccessHandlerFor(
				postProcess(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)), preferredMatcher);*/
	}

	@Override
	public void configure(B http) throws Exception {
		/*AuthenticationManager authenticationManager = http
				.getSharedObject(AuthenticationManager.class);
		BasicAuthenticationFilter basicAuthenticationFilter = new BasicAuthenticationFilter(
				authenticationManager, this.authenticationEntryPoint);
		if (this.authenticationDetailsSource != null) {
			basicAuthenticationFilter
					.setAuthenticationDetailsSource(this.authenticationDetailsSource);
		}
		RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
		if(rememberMeServices != null) {
			basicAuthenticationFilter.setRememberMeServices(rememberMeServices);
		}
		basicAuthenticationFilter = postProcess(basicAuthenticationFilter);
		http.addFilter(basicAuthenticationFilter);*/
	}

}
