package com.cyz.basic.config.security.provisioning;

import java.util.List;

import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.config.security.detail.SecurityAuthority;
import com.cyz.basic.config.security.detail.SecurityUser;

public class MyBatisUserDetailsManager extends UserDetailServiceSupport {
	
	

	@Override
	public UserDetails createUserDetail(SecurityUser user, List<SecurityAuthority> auths) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SecurityUser getUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SecurityAuthority> getAuthsByUsername(SecurityUser user) {
		// TODO Auto-generated method stub
		return null;
	}

}
