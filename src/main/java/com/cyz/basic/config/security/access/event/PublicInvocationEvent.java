package com.cyz.basic.config.security.access.event;
/**
 * Event that is generated whenever a public secure object is invoked.
 * <p>
 * A public secure object is a secure object that has no <code>ConfigAttribute</code>s
 * defined. A public secure object will not cause the <code>SecurityContextHolder</code>
 * to be inspected or authenticated, and no authorization will take place.
 * </p>
 * <p>
 * Published just before the secure object attempts to proceed.
 * </p>
 *
 * @author Ben Alex
 */
@SuppressWarnings("serial")
public class PublicInvocationEvent extends AbstractAuthorizationEvent {
	// ~ Constructors
	// ===================================================================================================

	/**
	 * Construct the event, passing in the public secure object.
	 *
	 * @param secureObject the public secure object
	 */
	public PublicInvocationEvent(Object secureObject) {
		super(secureObject);
	}
}
