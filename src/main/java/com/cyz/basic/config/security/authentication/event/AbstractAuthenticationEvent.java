package com.cyz.basic.config.security.authentication.event;

import org.springframework.context.ApplicationEvent;

import com.cyz.basic.config.security.core.Authentication;

@SuppressWarnings("serial")
public abstract class AbstractAuthenticationEvent extends ApplicationEvent {
	// ===================================================================================================

	public AbstractAuthenticationEvent(Authentication authentication) {
		super(authentication);
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * Getters for the <code>Authentication</code> request that caused the event. Also
	 * available from <code>super.getSource()</code>.
	 *
	 * @return the authentication request
	 */
	public Authentication getAuthentication() {
		return (Authentication) super.getSource();
	}
}
