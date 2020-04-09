package com.cyz.basic.config.security.authentication.event;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AuthenticationException;

public class AuthenticationFailureBadCredentialsEvent extends AbstractAuthenticationFailureEvent {
	public AuthenticationFailureBadCredentialsEvent(Authentication authentication,
			AuthenticationException exception) {
		super(authentication, exception);
	}
}
