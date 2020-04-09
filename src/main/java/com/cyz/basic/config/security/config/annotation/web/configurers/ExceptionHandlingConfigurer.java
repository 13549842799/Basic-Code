package com.cyz.basic.config.security.config.annotation.web.configurers;

import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.access.AccessDeniedHandler;
import com.cyz.basic.config.security.web.access.ExceptionTranslationFilter;

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
	
	private AccessDeniedHandler accessDeniedHandler;
	
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
	
	@Override
	public void configure(H http) throws Exception {
		//AuthenticationEntryPoint entryPoint = getAuthenticationEntryPoint(http);
		ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter();
		if (accessDeniedHandler != null) {
			exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler);
		}
		exceptionTranslationFilter = postProcess(exceptionTranslationFilter);
		http.addFilter(exceptionTranslationFilter);
	}
}
