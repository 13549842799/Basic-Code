package com.cyz.basic.config.security.exception;

/**
 * Thrown if an authentication request is rejected because the account is disabled. Makes
 * no assertion as to whether or not the credentials were valid.
 *
 * @author Ben Alex
 */
@SuppressWarnings("serial")
public class DisabledException extends AccountStatusException {
	/**
	 * Constructs a <code>DisabledException</code> with the specified message.
	 *
	 * @param msg the detail message
	 */
	public DisabledException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>DisabledException</code> with the specified message and root
	 * cause.
	 *
	 * @param msg the detail message
	 * @param t root cause
	 */
	public DisabledException(String msg, Throwable t) {
		super(msg, t);
	}
}
