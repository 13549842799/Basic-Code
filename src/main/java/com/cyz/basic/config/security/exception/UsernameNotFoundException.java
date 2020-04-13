package com.cyz.basic.config.security.exception;

public class UsernameNotFoundException extends AuthenticationException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 254991862299169133L;

	public UsernameNotFoundException() {
		super("用户名有问题");
	}

	public UsernameNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public UsernameNotFoundException(String msg, Throwable t) {
		super(msg, t);
	}

}
