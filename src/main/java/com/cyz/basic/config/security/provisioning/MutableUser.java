package com.cyz.basic.config.security.provisioning;

import java.util.Collection;

import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.config.security.detail.GrantedAuthority;

public class MutableUser implements MutableUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6878236227640857677L;
	private String password;
	private final UserDetails delegate;

	public MutableUser(UserDetails user) {
		this.delegate = user;
		this.password = user.getPassword();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return delegate.getAuthorities();
	}

	public String getUsername() {
		return delegate.getUsername();
	}

	public boolean isAccountNonExpired() {
		return delegate.isAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
		return delegate.isAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		return delegate.isCredentialsNonExpired();
	}

	public boolean isEnabled() {
		return delegate.isEnabled();
	}

}
