package com.cyz.basic.config.security.core.context;

import java.io.Serializable;

import com.cyz.basic.config.security.core.Authentication;

/**
 * 
 * @author cyz
 *
 */
public interface CyzSecurityContext extends Serializable {
	/**
	 * Obtains the currently authenticated principal, or an authentication request token.
	 *
	 * @return the <code>Authentication</code> or <code>null</code> if no authentication
	 * information is available
	 */
	Authentication getAuthentication();

	/**
	 * Changes the currently authenticated principal, or removes the authentication
	 * information.
	 *
	 * @param authentication the new <code>Authentication</code> token, or
	 * <code>null</code> if no further authentication information should be stored
	 */
	void setAuthentication(Authentication authentication);
	
	void clearAuthentication();
}
