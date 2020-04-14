package com.cyz.basic.config.security.authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.cyz.basic.config.security.detail.GrantedAuthority;


/**
 * An {@link org.springframework.security.core.Authentication} implementation that is
 * designed for simple presentation of a username and password.
 * <p>
 * The <code>principal</code> and <code>credentials</code> should be set with an
 * <code>Object</code> that provides the respective property via its
 * <code>Object.toString()</code> method. The simplest such <code>Object</code> to use is
 * <code>String</code>.
 *
 * @author Ben Alex
 */
public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {

	// ~ Instance fields
		// ================================================================================================

		/**
	 * 
	 */
	private static final long serialVersionUID = 154844373771184373L;
	private Object principal; //一般表示用户名
	private Object credentials; //一般表示密码

	// ~ Constructors
	// ===================================================================================================

	public UsernamePasswordAuthenticationToken() {
	}
	
	/**
	 * This constructor can be safely used by any code that wishes to create a
	 * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
	 * will return <code>false</code>.
	 *
	 */
	public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(false);
	}

	/**
	 * This constructor should only be used by <code>AuthenticationManager</code> or
	 * <code>AuthenticationProvider</code> implementations that are satisfied with
	 * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
	 * authentication token.
	 *
	 * @param principal
	 * @param credentials
	 * @param authorities
	 */
	public UsernamePasswordAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		super.setAuthenticated(true); // must use super, as we override
	}

	// ~ Methods
	// ========================================================================================================

	public Object getCredentials() {
		return this.credentials;
	}

	public Object getPrincipal() {
		return this.principal;
	}
	

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		/*if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}*/

		super.setAuthenticated(isAuthenticated);
	}

	@Override
	public Object result() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("username", this.principal);
		result.put("details", this.getDetails());
		return result;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		credentials = null;
	}

}
