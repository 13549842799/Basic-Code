package com.cyz.basic.config.security.config.annotation.authentication.configurers.userdetails;

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
	
}
