package com.cyz.basic.config.security.config.annotation.authentication.configurers.userdetails;

import com.cyz.basic.config.security.authentication.dao.DaoAuthenticationProvider;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.authentication.ProviderManagerBuilder;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;
import com.cyz.basic.config.security.crypto.password.PasswordEncoder;

/**
 * Allows configuring a {@link DaoAuthenticationProvider}
 *
 * @author Rob Winch
 * @since 3.2
 *
 * @param <B> the type of the {@link SecurityBuilder}
 * @param <C> the type of {@link AbstractDaoAuthenticationConfigurer} this is
 * @param <U> The type of {@link UserDetailsService} that is being used
 *
 */
abstract class AbstractDaoAuthenticationConfigurer<B extends ProviderManagerBuilder<B>, C extends AbstractDaoAuthenticationConfigurer<B, C, U>, U extends UserDetailsService>
		extends UserDetailsAwareConfigurer<B, U> {
	private DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	private final U userDetailsService;
	
	/**
	 * Creates a new instance
	 *
	 * @param userDetailsService
	 */
	protected AbstractDaoAuthenticationConfigurer(U userDetailsService) {
		this.userDetailsService = userDetailsService;
		provider.setUserDetailsService(userDetailsService);
	}
	
	/**
	 * Adds an {@link ObjectPostProcessor} for this class.
	 *
	 * @param objectPostProcessor
	 * @return the {@link AbstractDaoAuthenticationConfigurer} for further customizations
	 */
	@SuppressWarnings("unchecked")
	public C withObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
		addObjectPostProcessor(objectPostProcessor);
		return (C) this;
	}

	/**
	 * Allows specifying the {@link PasswordEncoder} to use with the
	 * {@link DaoAuthenticationProvider}. The default is to use plain text.
	 *
	 * @param passwordEncoder The {@link PasswordEncoder} to use.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public C passwordEncoder(PasswordEncoder passwordEncoder) {
		provider.setPasswordEncoder(passwordEncoder);
		return (C) this;
	}

	@Override
	public void configure(B builder) throws Exception {
		provider = postProcess(provider);
		builder.authenticationProvider(provider);
	}

	/**
	 * Gets the {@link UserDetailsService} that is used with the
	 * {@link DaoAuthenticationProvider}
	 *
	 * @return the {@link UserDetailsService} that is used with the
	 * {@link DaoAuthenticationProvider}
	 */
	public U getUserDetailsService() {
		return userDetailsService;
	}
}
