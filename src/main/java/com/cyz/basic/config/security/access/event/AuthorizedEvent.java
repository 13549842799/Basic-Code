package com.cyz.basic.config.security.access.event;

import java.util.Collection;

import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.core.Authentication;


@SuppressWarnings("serial")
public class AuthorizedEvent extends AbstractAuthorizationEvent {
	// ================================================================================================

	private Authentication authentication;
	private Collection<ConfigAttribute> configAttributes;

	// ~ Constructors
	// ===================================================================================================

	/**
	 * Construct the event.
	 *
	 * @param secureObject the secure object
	 * @param attributes that apply to the secure object
	 * @param authentication that successfully called the secure object
	 *
	 */
	public AuthorizedEvent(Object secureObject, Collection<ConfigAttribute> attributes,
			Authentication authentication) {
		super(secureObject);

		if ((attributes == null) || (authentication == null)) {
			throw new IllegalArgumentException(
					"All parameters are required and cannot be null");
		}

		this.configAttributes = attributes;
		this.authentication = authentication;
	}

	// ~ Methods
	// ========================================================================================================

	public Authentication getAuthentication() {
		return authentication;
	}

	public Collection<ConfigAttribute> getConfigAttributes() {
		return configAttributes;
	}
}
