package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.AuthenticationEntryPoint;
import com.cyz.basic.config.security.web.access.AccessDeniedHandler;
import com.cyz.basic.config.security.web.access.ExceptionTranslationFilter;
import com.cyz.basic.config.security.web.authentication.DelegatingAuthenticationEntryPoint;
import com.cyz.basic.config.security.web.authentication.Http403ForbiddenEntryPoint;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

/**
 * Adds exception handling for Spring Security related exceptions to an application. All
 * properties have reasonable defaults, so no additional configuration is required other
 * than applying this
 * {@link org.springframework.security.config.annotation.SecurityConfigurer}.
 *
 * <h2>Security Filters</h2>
 *
 * The following Filters are populated
 *
 * <ul>
 * <li>{@link ExceptionTranslationFilter}</li>
 * </ul>
 *
 * <h2>Shared Objects Created</h2>
 *
 * No shared objects are created.
 *
 * <h2>Shared Objects Used</h2>
 *
 * The following shared objects are used:
 *
 * <ul>
 * <li>If no explicit {@link RequestCache}, is provided a {@link RequestCache} shared
 * object is used to replay the request after authentication is successful</li>
 * <li>{@link AuthenticationEntryPoint} - see
 * {@link #authenticationEntryPoint(AuthenticationEntryPoint)}</li>
 * </ul>
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class ExceptionHandlingConfigurer<H extends HttpSecurityBuilder<H>> extends
    AbstractHttpConfigurer<ExceptionHandlingConfigurer<H>, H> {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	
	private AuthenticationEntryPoint authenticationEntryPoint;
	
	private AccessDeniedHandler accessDeniedHandler;
	
	private LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> defaultEntryPointMappings = new LinkedHashMap<>();
	
	/**
	 * Creates a new instance
	 * @see HttpSecurity#exceptionHandling()
	 */
	public ExceptionHandlingConfigurer() {
	}
	
	/**
	 * Specifies the {@link AccessDeniedHandler} to be used
	 *
	 * @param accessDeniedHandler the {@link AccessDeniedHandler} to be used
	 * @return the {@link ExceptionHandlingConfigurer} for further customization
	 */
	public ExceptionHandlingConfigurer<H> accessDeniedHandler(
			AccessDeniedHandler accessDeniedHandler) {
		this.accessDeniedHandler = accessDeniedHandler;
		return this;
	}
	
	/**
	 * Gets the {@link AccessDeniedHandler} that is configured.
	 *
	 * @return the {@link AccessDeniedHandler}
	 */
	AccessDeniedHandler getAccessDeniedHandler() {
		return this.accessDeniedHandler;
	}
	
	/**
	 * Gets any explicitly configured {@link AuthenticationEntryPoint}
	 * @return
	 */
	AuthenticationEntryPoint getAuthenticationEntryPoint() {
		return this.authenticationEntryPoint;
	}
	
	/**
	 * Sets a default {@link AuthenticationEntryPoint} to be used which prefers being
	 * invoked for the provided {@link RequestMatcher}. If only a single default
	 * {@link AuthenticationEntryPoint} is specified, it will be what is used for the
	 * default {@link AuthenticationEntryPoint}. If multiple default
	 * {@link AuthenticationEntryPoint} instances are configured, then a
	 * {@link DelegatingAuthenticationEntryPoint} will be used.
	 *
	 * @param entryPoint the {@link AuthenticationEntryPoint} to use
	 * @param preferredMatcher the {@link RequestMatcher} for this default
	 * {@link AuthenticationEntryPoint}
	 * @return the {@link ExceptionHandlingConfigurer} for further customizations
	 */
	public ExceptionHandlingConfigurer<H> defaultAuthenticationEntryPointFor(
			AuthenticationEntryPoint entryPoint, RequestMatcher preferredMatcher) {
		this.defaultEntryPointMappings.put(preferredMatcher, entryPoint);
		return this;
	}
	
	@Override
	public void configure(H http) throws Exception {
		AuthenticationEntryPoint entryPoint = getAuthenticationEntryPoint(http);
		ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(entryPoint);
		if (accessDeniedHandler != null) {
			exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler);
		}
		exceptionTranslationFilter = postProcess(exceptionTranslationFilter);
		http.addFilter(exceptionTranslationFilter);
	}
	
	/**
	 * Gets the {@link AuthenticationEntryPoint} according to the rules specified by
	 * {@link #authenticationEntryPoint(AuthenticationEntryPoint)}
	 * @param http the {@link HttpSecurity} used to look up shared
	 * {@link AuthenticationEntryPoint}
	 * @return the {@link AuthenticationEntryPoint} to use
	 */
	AuthenticationEntryPoint getAuthenticationEntryPoint(H http) {
		AuthenticationEntryPoint entryPoint = this.authenticationEntryPoint;
		if (entryPoint == null) {
			entryPoint = createDefaultEntryPoint(http);
		}
		return entryPoint;
	}

	private AuthenticationEntryPoint createDefaultEntryPoint(H http) {
		if (defaultEntryPointMappings.isEmpty()) {
			logger.info("defaultEntryPointMappings is empty , so it is http 403");
			return new Http403ForbiddenEntryPoint();
		}
		if (defaultEntryPointMappings.size() == 1) {
			return defaultEntryPointMappings.values().iterator().next();
		}
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(
				defaultEntryPointMappings);
		entryPoint.setDefaultEntryPoint(defaultEntryPointMappings.values().iterator()
				.next());
		return entryPoint;
	}
}
