package com.cyz.basic.config.security.exception;

/**
 * Abstract superclass for all exceptions related to an {@link Authentication} object
 * being invalid for whatever reason.
 *
 * @author Ben Alex
 */
@SuppressWarnings("serial")
public abstract class AuthenticationException extends RuntimeException {

	// ~ Constructors
	// ===================================================================================================

	/**
	 * Constructs an {@code AuthenticationException} with the specified message and root
	 * cause.
	 *
	 * @param msg the detail message
	 * @param t the root cause
	 */
	public AuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * Constructs an {@code AuthenticationException} with the specified message and no
	 * root cause.
	 *
	 * @param msg the detail message
	 */
	public AuthenticationException(String msg) {
		super(msg);
	}

}
