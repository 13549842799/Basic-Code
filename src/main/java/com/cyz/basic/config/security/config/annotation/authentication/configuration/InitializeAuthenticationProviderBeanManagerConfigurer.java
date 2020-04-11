package com.cyz.basic.config.security.config.annotation.authentication.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;

import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * Lazily initializes the global authentication with a {@link UserDetailsService} if it is
 * not yet configured and there is only a single Bean of that type. Optionally, if a
 * {@link PasswordEncoder} is defined will wire this up too.
 *
 * @author Rob Winch
 * @since 4.1
 */
@Order(InitializeAuthenticationProviderBeanManagerConfigurer.DEFAULT_ORDER)
public class InitializeAuthenticationProviderBeanManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {
	static final int DEFAULT_ORDER = InitializeUserDetailsBeanManagerConfigurer.DEFAULT_ORDER
			- 100;

	private final ApplicationContext context;
	
	/**
	 * @param context the ApplicationContext to look up beans.
	 */
	public InitializeAuthenticationProviderBeanManagerConfigurer(
			ApplicationContext context) {
		this.context = context;
	}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.apply(new InitializeUserDetailsManagerConfigurer());
	}

	class InitializeUserDetailsManagerConfigurer
			extends GlobalAuthenticationConfigurerAdapter {
		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			if (auth.isConfigured()) {
				return;
			}
			AuthenticationProvider authenticationProvider = getBeanOrNull(
					AuthenticationProvider.class);
			if (authenticationProvider == null) {
				return;
			}


			auth.authenticationProvider(authenticationProvider);
		}

		/**
		 * @return
		 */
		private <T> T getBeanOrNull(Class<T> type) {
			String[] userDetailsBeanNames = InitializeAuthenticationProviderBeanManagerConfigurer.this.context
					.getBeanNamesForType(type);
			if (userDetailsBeanNames.length != 1) {
				return null;
			}

			return InitializeAuthenticationProviderBeanManagerConfigurer.this.context
					.getBean(userDetailsBeanNames[0], type);
		}
	}
}
