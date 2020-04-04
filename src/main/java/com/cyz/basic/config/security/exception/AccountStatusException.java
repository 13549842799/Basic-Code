package com.cyz.basic.config.security.exception;

@SuppressWarnings("serial")
public abstract class AccountStatusException extends AuthenticationException {
	public AccountStatusException(String msg) {
		super(msg);
	}

	public AccountStatusException(String msg, Throwable t) {
		super(msg, t);
	}
}
