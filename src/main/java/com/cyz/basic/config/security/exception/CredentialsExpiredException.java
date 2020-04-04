package com.cyz.basic.config.security.exception;

/**
 * Thrown if an authentication request is rejected because the account's credentials have
 * expired. Makes no assertion as to whether or not the credentials were valid.
 *
 * @author Ben Alex
 */
public class CredentialsExpiredException extends AccountStatusException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7616276415841165470L;

	/**
	 * Constructs a <code>CredentialsExpiredException</code> with the specified message.
	 *
	 * @param msg the detail message
	 */
	public CredentialsExpiredException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>CredentialsExpiredException</code> with the specified message
	 * and root cause.
	 *
	 * @param msg the detail message
	 * @param t root cause
	 */
	public CredentialsExpiredException(String msg, Throwable t) {
		super(msg, t);
	}
}
