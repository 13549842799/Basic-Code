package com.cyz.basic.config.security.config.annotation.authentication;

import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.config.annotation.SecurityBuilder;

public interface ProviderManagerBuilder<B extends ProviderManagerBuilder<B>> extends SecurityBuilder<AuthenticationManager> {

	/**
	 * Add authentication based upon the custom {@link AuthenticationProvider} that is
	 * passed in. Since the {@link AuthenticationProvider} implementation is unknown, all
	 * customizations must be done externally and the {@link ProviderManagerBuilder} is
	 * returned immediately.
	 *
	 * Note that an Exception is thrown if an error occurs when adding the {@link AuthenticationProvider}.
	 *
	 * @return a {@link ProviderManagerBuilder} to allow further authentication to be
	 * provided to the {@link ProviderManagerBuilder}
	 */
	B authenticationProvider(AuthenticationProvider authenticationProvider);
}
