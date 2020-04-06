package com.cyz.basic.config.security.config.annotation.web.configurers;

import com.cyz.basic.config.security.access.AccessDecisionManager;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;

/**
 * A base class for configuring the {@link FilterSecurityInterceptor}.
 *
 * <h2>Security Filters</h2>
 *
 * The following Filters are populated
 *
 * <ul>
 * <li>{@link FilterSecurityInterceptor}</li>
 * </ul>
 *
 * <h2>Shared Objects Created</h2>
 *
 * The following shared objects are populated to allow other {@link SecurityConfigurer}'s
 * to customize:
 * <ul>
 * <li>{@link FilterSecurityInterceptor}</li>
 * </ul>
 *
 * <h2>Shared Objects Used</h2>
 *
 * The following shared objects are used:
 *
 * <ul>
 * <li>
 * {@link AuthenticationManager}
 * </li>
 * </ul>
 *
 *
 * @param <C> the AbstractInterceptUrlConfigurer
 * @param <H> the type of {@link HttpSecurityBuilder} that is being configured
 *
 * @author Rob Winch
 * @since 3.2
 * @see ExpressionUrlAuthorizationConfigurer
 * @see UrlAuthorizationConfigurer
 */
public abstract class AbstractInterceptUrlConfigurer<C extends AbstractInterceptUrlConfigurer<C, H>, H extends HttpSecurityBuilder<H>>
    extends AbstractHttpConfigurer<C, H> {
	private Boolean filterSecurityInterceptorOncePerRequest;
	
	private AccessDecisionManager accessDecisionManager;
	
	abstract class AbstractInterceptUrlRegistry<R extends AbstractInterceptUrlRegistry<R, T>, T> extends AbstractConfigAttributeRequestMatcherRegistry<T> {

		/**
		 * Allows setting the {@link AccessDecisionManager}. If none is provided, a
		 * default {@link AccessDecisionManager} is created.
		 *
		 * @param accessDecisionManager the {@link AccessDecisionManager} to use
		 * @return the {@link AbstractInterceptUrlConfigurer} for further customization
		 */
		public R accessDecisionManager(AccessDecisionManager accessDecisionManager) {
			AbstractInterceptUrlConfigurer.this.accessDecisionManager = accessDecisionManager;
			return getSelf();
		}
		
		/**
		 * Allows setting if the {@link FilterSecurityInterceptor} should be only applied
		 * once per request (i.e. if the filter intercepts on a forward, should it be
		 * applied again).
		 *
		 * @param filterSecurityInterceptorOncePerRequest if the
		 * {@link FilterSecurityInterceptor} should be only applied once per request
		 * @return the {@link AbstractInterceptUrlConfigurer} for further customization
		 */
		public R filterSecurityInterceptorOncePerRequest(
				boolean filterSecurityInterceptorOncePerRequest) {
			AbstractInterceptUrlConfigurer.this.filterSecurityInterceptorOncePerRequest = filterSecurityInterceptorOncePerRequest;
			return getSelf();
		}
		
		/**
		 * Returns a reference to the current object with a single suppression of the type
		 *
		 * @return a reference to the current object
		 */
		@SuppressWarnings("unchecked")
		private R getSelf() {
			return (R) this;
		}
	}

}
