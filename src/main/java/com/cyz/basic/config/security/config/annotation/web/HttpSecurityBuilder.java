package com.cyz.basic.config.security.config.annotation.web;

import javax.servlet.Filter;

import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.config.annotation.SecurityBuilder;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurer;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;
import com.cyz.basic.config.security.web.DefaultSecurityFilterChain;

public interface HttpSecurityBuilder<H extends HttpSecurityBuilder<H>> extends
    SecurityBuilder<DefaultSecurityFilterChain> {
	
	/**
	 * Gets the {@link SecurityConfigurer} by its class name or <code>null</code> if not
	 * found. Note that object hierarchies are not considered.
	 *
	 * @param clazz the Class of the {@link SecurityConfigurer} to attempt to get.
	 */
	<C extends SecurityConfigurer<DefaultSecurityFilterChain, H>> C getConfigurer(
			Class<C> clazz);

	/**
	 * Removes the {@link SecurityConfigurer} by its class name or <code>null</code> if
	 * not found. Note that object hierarchies are not considered.
	 *
	 * @param clazz the Class of the {@link SecurityConfigurer} to attempt to remove.
	 * @return the {@link SecurityConfigurer} that was removed or null if not found
	 */
	<C extends SecurityConfigurer<DefaultSecurityFilterChain, H>> C removeConfigurer(
			Class<C> clazz);

	/**
	 * Sets an object that is shared by multiple {@link SecurityConfigurer}.
	 *
	 * @param sharedType the Class to key the shared object by.
	 * @param object the Object to store
	 */
	<C> void setSharedObject(Class<C> sharedType, C object);

	/**
	 * Gets a shared Object. Note that object heirarchies are not considered.
	 *
	 * @param sharedType the type of the shared Object
	 * @return the shared Object or null if it is not found
	 */
	<C> C getSharedObject(Class<C> sharedType);

	/**
	 * Allows adding an additional {@link AuthenticationProvider} to be used
	 *
	 * @param authenticationProvider the {@link AuthenticationProvider} to be added
	 * @return the {@link HttpSecurity} for further customizations
	 */
	H authenticationProvider(AuthenticationProvider authenticationProvider);

	/**
	 * Allows adding an additional {@link UserDetailsService} to be used
	 *
	 * @param userDetailsService the {@link UserDetailsService} to be added
	 * @return the {@link HttpSecurity} for further customizations
	 */
	H userDetailsService(UserDetailsService userDetailsService) throws Exception;

	/**
	 * Allows adding a {@link Filter} after one of the known {@link Filter} classes. The
	 * known {@link Filter} instances are either a {@link Filter} listed in
	 * {@link #addFilter(Filter)} or a {@link Filter} that has already been added using
	 * {@link #addFilterAfter(Filter, Class)} or {@link #addFilterBefore(Filter, Class)}.
	 *
	 * @param filter the {@link Filter} to register after the type {@code afterFilter}
	 * @param afterFilter the Class of the known {@link Filter}.
	 * @return the {@link HttpSecurity} for further customizations
	 */
	H addFilterAfter(Filter filter, Class<? extends Filter> afterFilter);

	/**
	 * Allows adding a {@link Filter} before one of the known {@link Filter} classes. The
	 * known {@link Filter} instances are either a {@link Filter} listed in
	 * {@link #addFilter(Filter)} or a {@link Filter} that has already been added using
	 * {@link #addFilterAfter(Filter, Class)} or {@link #addFilterBefore(Filter, Class)}.
	 *
	 * @param filter the {@link Filter} to register before the type {@code beforeFilter}
	 * @param beforeFilter the Class of the known {@link Filter}.
	 * @return the {@link HttpSecurity} for further customizations
	 */
	H addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter);

	/**
	 * Adds a {@link Filter} that must be an instance of or extend one of the Filters
	 * provided within the Security framework. The method ensures that the ordering of the
	 * Filters is automatically taken care of.
	 *
	 * The ordering of the Filters is:
	 *
	 * <ul>
	 * <li>{@link ChannelProcessingFilter}</li>
	 * <li>{@link ConcurrentSessionFilter}</li>
	 * <li>{@link SecurityContextPersistenceFilter}</li>
	 * <li>{@link LogoutFilter}</li>
	 * <li>{@link X509AuthenticationFilter}</li>
	 * <li>{@link AbstractPreAuthenticatedProcessingFilter}</li>
	 * <li><a href="{@docRoot}/org/springframework/security/cas/web/CasAuthenticationFilter.html">CasAuthenticationFilter</a></li>
	 * <li>{@link UsernamePasswordAuthenticationFilter}</li>
	 * <li>{@link ConcurrentSessionFilter}</li>
	 * <li>{@link OpenIDAuthenticationFilter}</li>
	 * <li>{@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter}</li>
	 * <li>{@link ConcurrentSessionFilter}</li>
	 * <li>{@link DigestAuthenticationFilter}</li>
	 * <li>{@link BasicAuthenticationFilter}</li>
	 * <li>{@link RequestCacheAwareFilter}</li>
	 * <li>{@link SecurityContextHolderAwareRequestFilter}</li>
	 * <li>{@link JaasApiIntegrationFilter}</li>
	 * <li>{@link RememberMeAuthenticationFilter}</li>
	 * <li>{@link AnonymousAuthenticationFilter}</li>
	 * <li>{@link SessionManagementFilter}</li>
	 * <li>{@link ExceptionTranslationFilter}</li>
	 * <li>{@link FilterSecurityInterceptor}</li>
	 * <li>{@link SwitchUserFilter}</li>
	 * </ul>
	 *
	 * @param filter the {@link Filter} to add
	 * @return the {@link HttpSecurity} for further customizations
	 */
	H addFilter(Filter filter);

}
