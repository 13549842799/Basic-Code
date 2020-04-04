package com.cyz.basic.config.security.authentication.event;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.access.event.AbstractAuthorizationEvent;
import com.cyz.basic.config.security.core.Authentication;

/**
 * Indicates an interactive authentication was successful.
 * <P>
 * The <code>ApplicationEvent</code>'s <code>source</code> will be the
 * <code>Authentication</code> object.
 * </p>
 * <p>
 * This does not extend from <code>AuthenticationSuccessEvent</code> to avoid duplicate
 * <code>AuthenticationSuccessEvent</code>s being sent to any listeners.
 * </p>
 *
 * @author Ben Alex
 */
@SuppressWarnings("serial")
public class InteractiveAuthenticationSuccessEvent extends AbstractAuthorizationEvent {
	// ~ Instance fields
	// ================================================================================================

	private final Class<?> generatedBy;

	// ~ Constructors
	// ===================================================================================================

	public InteractiveAuthenticationSuccessEvent(Authentication authentication,
			Class<?> generatedBy) {
		super(authentication);
		Assert.notNull(generatedBy, "generatedBy cannot be null");
		this.generatedBy = generatedBy;
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * Getter for the <code>Class</code> that generated this event. This can be useful for
	 * generating additional logging information.
	 *
	 * @return the class
	 */
	public Class<?> getGeneratedBy() {
		return generatedBy;
	}
}
