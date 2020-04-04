package com.cyz.basic.config.security.exception;

/**
 * Thrown if an authentication request is rejected because there is no
 * {@link Authentication} object in the
 * {@link org.springframework.security.core.context.SecurityContext SecurityContext}.
 *
 * @author Ben Alex
 */
@SuppressWarnings("serial")
public class AuthenticationCredentialsNotFoundException extends AuthenticationException {

	/**
	 * Constructs an <code>AuthenticationCredentialsNotFoundException</code> with the
	 * specified message.
	 *
	 * @param msg the detail message
	 */
	public AuthenticationCredentialsNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * Constructs an <code>AuthenticationCredentialsNotFoundException</code> with the
	 * specified message and root cause.
	 *
	 * @param msg the detail message
	 * @param t root cause
	 */
	public AuthenticationCredentialsNotFoundException(String msg, Throwable t) {
		super(msg, t);
	}
}
