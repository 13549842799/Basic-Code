package com.cyz.basic.config.security.config.annotation.authentication.configurers.userdetails;

import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.authentication.ProviderManagerBuilder;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;

public abstract class UserDetailsAwareConfigurer<B extends ProviderManagerBuilder<B>
    , U extends UserDetailsService> extends SecurityConfigurerAdapter<AuthenticationManager, B> {
	
	/**
	 * Gets the {@link UserDetailsService} or null if it is not available
	 * @return the {@link UserDetailsService} or null if it is not available
	 */
	public abstract U getUserDetailsService();

}
