package com.cyz.basic.config.security.access.event;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract superclass for all security interception related events.
 *
 * @author Ben Alex
 */
public abstract class AbstractAuthorizationEvent extends ApplicationEvent {
	// ~ Constructors
		// ===================================================================================================

		/**
	 * 
	 */
	private static final long serialVersionUID = 6809145724792720696L;

		/**
		 * Construct the event, passing in the secure object being intercepted.
		 *
		 * @param secureObject the secure object
		 */
		public AbstractAuthorizationEvent(Object secureObject) {
			super(secureObject);
		}
}
