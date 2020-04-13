package com.cyz.basic.config.security.config.annotation.authentication.configurers.userdetails;

import com.cyz.basic.config.security.config.annotation.authentication.ProviderManagerBuilder;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;

/**
 * Allows configuring a {@link DaoAuthenticationProvider}
 *
 * @author Rob Winch
 * @since 3.2
 *
 * @param <B> The type of {@link ProviderManagerBuilder} this is
 * @param <U> The type of {@link UserDetailsService} that is being used
 *
 */
public class DaoAuthenticationConfigurer<B extends ProviderManagerBuilder<B>, U extends UserDetailsService>
		extends
		AbstractDaoAuthenticationConfigurer<B, DaoAuthenticationConfigurer<B, U>, U> {

	/**
	 * Creates a new instance
	 * @param userDetailsService
	 */
	public DaoAuthenticationConfigurer(U userDetailsService) {
		super(userDetailsService);
	}

}
