package com.cyz.basic.config.security.config.annotation.authentication.configurers.provisioning;

import java.util.ArrayList;

import com.cyz.basic.config.security.config.annotation.authentication.ProviderManagerBuilder;
import com.cyz.basic.config.security.provisioning.InMemoryUserDetailsManager;

/**
 * Configures an
 * {@link org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder}
 * to have in memory authentication. It also allows easily adding users to the in memory
 * authentication.
 *
 * @param <B> the type of the {@link ProviderManagerBuilder} that is being configured
 *
 * @author Rob Winch
 * @since 3.2
 */
public class InMemoryUserDetailsManagerConfigurer<B extends ProviderManagerBuilder<B>>
		extends UserDetailsManagerConfigurer<B, InMemoryUserDetailsManagerConfigurer<B>> {

	/**
	 * Creates a new instance
	 */
	public InMemoryUserDetailsManagerConfigurer() {
		//super(new InMemoryUserDetailsManager(new ArrayList<>()));
	}
}
