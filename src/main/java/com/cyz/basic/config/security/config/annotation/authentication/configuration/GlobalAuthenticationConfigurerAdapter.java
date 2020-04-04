package com.cyz.basic.config.security.config.annotation.authentication.configuration;

import org.springframework.core.annotation.Order;

import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurer;
import com.cyz.basic.config.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * A {@link SecurityConfigurer} that can be exposed as a bean to configure the global
 * {@link AuthenticationManagerBuilder}. Beans of this type are automatically used by
 * {@link AuthenticationConfiguration} to configure the global
 * {@link AuthenticationManagerBuilder}.
 *
 * @since 5.0
 * @author Rob Winch
 */
@Order(100)
public abstract class GlobalAuthenticationConfigurerAdapter implements
		SecurityConfigurer<AuthenticationManager, AuthenticationManagerBuilder> {

	public void init(AuthenticationManagerBuilder auth) throws Exception {
	}

	public void configure(AuthenticationManagerBuilder auth) throws Exception {
	}
}