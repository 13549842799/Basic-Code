package com.cyz.basic.config.security.access.event;

import java.util.Collection;

import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AccessDeniedException;



/**
 * Indicates a secure object invocation failed because the principal could not be
 * authorized for the request.
 *
 * <p>
 * This event might be thrown as a result of either an
 * {@link org.springframework.security.access.AccessDecisionManager AccessDecisionManager}
 * or an {@link org.springframework.security.access.intercept.AfterInvocationManager
 * AfterInvocationManager}.
 *
 * @author Ben Alex
 */
@SuppressWarnings("serial")
public class AuthorizationFailureEvent extends AbstractAuthorizationEvent {
	// ~ Instance fields
	// ================================================================================================

	private AccessDeniedException accessDeniedException;
	private Authentication authentication;
	private Collection<ConfigAttribute> configAttributes;

	// ~ Constructors
	// ===================================================================================================

	/**
	 * Construct the event.
	 *
	 * @param secureObject the secure object
	 * @param attributes that apply to the secure object
	 * @param authentication that was found in the <code>SecurityContextHolder</code>
	 * @param accessDeniedException that was returned by the
	 * <code>AccessDecisionManager</code>
	 *
	 * @throws IllegalArgumentException if any null arguments are presented.
	 */
	public AuthorizationFailureEvent(Object secureObject,
			Collection<ConfigAttribute> attributes, Authentication authentication,
			AccessDeniedException accessDeniedException) {
		super(secureObject);

		if ((attributes == null) || (authentication == null)
				|| (accessDeniedException == null)) {
			throw new IllegalArgumentException(
					"All parameters are required and cannot be null");
		}

		this.configAttributes = attributes;
		this.authentication = authentication;
		this.accessDeniedException = accessDeniedException;
	}

	// ~ Methods
	// ========================================================================================================

	public AccessDeniedException getAccessDeniedException() {
		return accessDeniedException;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public Collection<ConfigAttribute> getConfigAttributes() {
		return configAttributes;
	}
}
