package com.cyz.basic.config.security.exception;

@SuppressWarnings("serial")
public class InsufficientAuthenticationException extends AuthenticationException {
	// ~ Constructors
		// ===================================================================================================

		/**
		 * Constructs an <code>InsufficientAuthenticationException</code> with the specified
		 * message.
		 *
		 * @param msg the detail message
		 */
		public InsufficientAuthenticationException(String msg) {
			super(msg);
		}

		/**
		 * Constructs an <code>InsufficientAuthenticationException</code> with the specified
		 * message and root cause.
		 *
		 * @param msg the detail message
		 * @param t root cause
		 */
		public InsufficientAuthenticationException(String msg, Throwable t) {
			super(msg, t);
		}
}
