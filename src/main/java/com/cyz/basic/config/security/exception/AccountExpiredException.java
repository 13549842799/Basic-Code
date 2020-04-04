package com.cyz.basic.config.security.exception;

public class AccountExpiredException extends AccountStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1985649581853541939L;

	/**
	 * Constructs a <code>AccountExpiredException</code> with the specified message.
	 *
	 * @param msg the detail message
	 */
	public AccountExpiredException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>AccountExpiredException</code> with the specified message and
	 * root cause.
	 *
	 * @param msg the detail message
	 * @param t root cause
	 */
	public AccountExpiredException(String msg, Throwable t) {
		super(msg, t);
	}
}
