package com.cyz.basic.config.security.authentication.event;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AuthenticationException;

public abstract class AbstractAuthenticationFailureEvent extends AbstractAuthenticationEvent {
	// ~ Instance fields
	// ================================================================================================

	private final AuthenticationException exception;

	// ~ Constructors
	// ===================================================================================================

	public AbstractAuthenticationFailureEvent(Authentication authentication,
			AuthenticationException exception) {
		super(authentication);
		Assert.notNull(exception, "AuthenticationException is required");
		this.exception = exception;
	}

	// ~ Methods
	// ========================================================================================================

	public AuthenticationException getException() {
		return exception;
	}
}
