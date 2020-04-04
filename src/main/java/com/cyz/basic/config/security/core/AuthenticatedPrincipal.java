package com.cyz.basic.config.security.core;


/**
 * Representation of an authenticated <code>Principal</code> once an
 * {@link Authentication} request has been successfully authenticated
 * by the {@link AuthenticationManager#authenticate(Authentication)} method.
 *
 * Implementors typically provide their own representation of a <code>Principal</code>,
 * which usually contains information describing the <code>Principal</code> entity,
 * such as, first/middle/last name, address, email, phone, id, etc.
 *
 * This interface allows implementors to expose specific attributes
 * of their custom representation of <code>Principal</code> in a generic way.
 *
 * @author Joe Grandja
 * @since 5.0
 * @see Authentication#getPrincipal()
 * @see org.springframework.security.core.userdetails.UserDetails
 */
public interface AuthenticatedPrincipal {

	/**
	 * Returns the name of the authenticated <code>Principal</code>. Never <code>null</code>.
	 *
	 * @return the name of the authenticated <code>Principal</code>
	 */
	String getName();

}