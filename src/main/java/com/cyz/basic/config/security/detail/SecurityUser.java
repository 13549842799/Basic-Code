package com.cyz.basic.config.security.detail;

import java.util.Collection;
import java.util.List;

import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.pojo.CreatorEntity;

@SuppressWarnings("serial")
public abstract class SecurityUser extends CreatorEntity<Integer> implements  UserDetails{
	
	protected List<SecurityAuthority> auths;
	
	

	public List<SecurityAuthority> getAuths() {
		return auths;
	}

	public void setAuths(List<SecurityAuthority> auths) {
		this.auths = auths;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {		
		return auths;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	
	
	

}
