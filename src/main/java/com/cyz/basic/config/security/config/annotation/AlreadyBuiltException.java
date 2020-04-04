package com.cyz.basic.config.security.config.annotation;

public class AlreadyBuiltException extends IllegalStateException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 340255455687906544L;

	public AlreadyBuiltException(String message) {
		super(message);
	}
}
