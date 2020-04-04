package com.cyz.basic.config.security.authentication;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AuthenticationException;

public interface AuthenticationEventPublisher {

	void publishAuthenticationSuccess(Authentication authentication);

	void publishAuthenticationFailure(AuthenticationException exception,
			Authentication authentication);
}
