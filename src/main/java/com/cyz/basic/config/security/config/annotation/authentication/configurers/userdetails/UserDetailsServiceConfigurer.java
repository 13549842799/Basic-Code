package com.cyz.basic.config.security.config.annotation.authentication.configurers.userdetails;

import com.cyz.basic.config.security.config.annotation.authentication.ProviderManagerBuilder;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;

/**
 * Allows configuring a {@link UserDetailsService} within a
 * {@link AuthenticationManagerBuilder}.
 *
 * @author Rob Winch
 * @since 3.2
 *
 * @param <B> the type of the {@link ProviderManagerBuilder}
 * @param <C> the {@link UserDetailsServiceConfigurer} (or this)
 * @param <U> the type of UserDetailsService being used to allow for returning the
 * concrete UserDetailsService.
 */
public class UserDetailsServiceConfigurer<B extends ProviderManagerBuilder<B>, C extends UserDetailsServiceConfigurer<B, C, U>, U extends UserDetailsService>
		extends AbstractDaoAuthenticationConfigurer<B, C, U> {

	@Override
	public U getUserDetailsService() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

