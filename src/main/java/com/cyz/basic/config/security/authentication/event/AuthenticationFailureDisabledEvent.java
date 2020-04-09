package com.cyz.basic.config.security.authentication.event;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AuthenticationException;

public class AuthenticationFailureDisabledEvent extends AbstractAuthenticationFailureEvent {

	public AuthenticationFailureDisabledEvent(Authentication authentication,
			AuthenticationException exception) {
		super(authentication, exception);
	}
}
