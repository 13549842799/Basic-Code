package com.cyz.basic.config.security.service;

import java.util.List;

import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;
import com.cyz.basic.config.security.detail.SecurityAuthority;
import com.cyz.basic.config.security.detail.SecurityUser;
import com.cyz.basic.config.security.exception.UsernameNotFoundException;
/**
 * 默认的，用户既与角色关联，也与权限关联，我们可以通过赋予用户角色来为用户批量添加权限，也可以单独为用户添加某个权限。
 * @author cyz
 *
 */
public abstract class UserDetailServiceSupport implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		SecurityUser user = getUserByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException(username + " is not fund");
		}
		
		List<SecurityAuthority> auths = getAuthsByUsername(user);
		if (auths == null || auths.size() == 0) {
			throw new UsernameNotFoundException(username + " has no authorities");
		}	

		return createUserDetail(user, auths);
	}
	
	public abstract UserDetails createUserDetail(SecurityUser user, List<SecurityAuthority> auths);
	
	public abstract SecurityUser getUserByUsername(String username);
	
	public abstract List<SecurityAuthority> getAuthsByUsername(SecurityUser user);

}
