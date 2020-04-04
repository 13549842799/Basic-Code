package com.cyz.basic.config.security.exception;

/**
 * <p>
 * Thrown if an authentication request could not be processed due to a system problem that
 * occurred internally. It differs from {@link AuthenticationServiceException} in that it
 * would not be thrown if an external system has an internal error or failure. This
 * ensures that we can handle errors that are within our control distinctly from errors of
 * other systems. The advantage to this distinction is that the untrusted external system
 * should not be able to fill up logs and cause excessive IO. However, an internal system
 * should report errors.
 * </p>
 * <p>
 * This might be thrown if a backend authentication repository is unavailable, for
 * example. However, it would not be thrown in the event that an error occurred when
 * validating an OpenID response with an OpenID Provider.
 * </p>
 *
 * @author Rob Winch
 *
 */
@SuppressWarnings("serial")
public class InternalAuthenticationServiceException extends
		AuthenticationServiceException {

	public InternalAuthenticationServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalAuthenticationServiceException(String message) {
		super(message);
	}
}
