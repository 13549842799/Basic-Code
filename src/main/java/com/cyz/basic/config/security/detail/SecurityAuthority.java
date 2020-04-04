package com.cyz.basic.config.security.detail;

public interface SecurityAuthority extends GrantedAuthority {

	default String getAuthority() {
		
		return null;
	}

}
