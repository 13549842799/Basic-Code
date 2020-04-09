package com.cyz.basic.config.security.authentication.event;

import com.cyz.basic.config.security.core.Authentication;

public class AuthenticationSuccessEvent extends AbstractAuthenticationEvent {

	public AuthenticationSuccessEvent(Authentication authentication) {
		super(authentication);
	}
}
