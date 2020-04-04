package com.cyz.basic.config.security.exception;

public class ProviderNotFoundException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2347283002076355220L;

	/**
     * Constructs an instance of this class.
     */
    public ProviderNotFoundException() {
    }

    /**
     * Constructs an instance of this class.
     *
     * @param   msg
     *          the detail message
     */
    public ProviderNotFoundException(String msg) {
        super(msg);
    }
}
