package com.cyz.basic.config.security.exception;

/**
 * Thrown if an authentication request is rejected because the account is locked. Makes no
 * assertion as to whether or not the credentials were valid.
 *
 * @author Ben Alex
 */
@SuppressWarnings("serial")
public class LockedException extends AccountStatusException {
	/**
	 * Constructs a <code>LockedException</code> with the specified message.
	 *
	 * @param msg the detail message.
	 */
	public LockedException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>LockedException</code> with the specified message and root
	 * cause.
	 *
	 * @param msg the detail message.
	 * @param t root cause
	 */
	public LockedException(String msg, Throwable t) {
		super(msg, t);
	}
}
