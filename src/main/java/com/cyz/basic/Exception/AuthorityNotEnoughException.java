package com.cyz.basic.Exception;

public class AuthorityNotEnoughException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4966222013562701739L;

	public AuthorityNotEnoughException() {
        super("权限不足");
	}
}
