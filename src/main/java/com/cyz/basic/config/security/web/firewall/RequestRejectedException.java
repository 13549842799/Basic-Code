package com.cyz.basic.config.security.web.firewall;

/**
 * @author Luke Taylor
 */
public class RequestRejectedException extends RuntimeException {
	public RequestRejectedException(String message) {
		super(message);
	}
}
