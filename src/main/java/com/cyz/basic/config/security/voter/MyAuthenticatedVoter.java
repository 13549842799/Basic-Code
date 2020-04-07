package com.cyz.basic.config.security.voter;

import java.util.Collection;

import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.access.vote.AuthenticatedVoter;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.detail.GrantedAuthority;


public class MyAuthenticatedVoter extends AuthenticatedVoter {

	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		int result = ACCESS_ABSTAIN;
		
		Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
        System.out.println(auths == null ? "auths长度为0" : "auths的长度为:" + auths.size());
		if (auths == null || auths.size() == 0) {
			return ACCESS_DENIED;
		}
		System.out.println(attributes == null);
		for (ConfigAttribute attr : attributes) {
			/*if (!super.supports(attr)) {
				continue;
			}*/			
			System.out.println("支持");
			System.out.println(attr.getAttribute());
			for (GrantedAuthority auth : auths) {
				if (auth.getAuthority().equals(attr.getAttribute())) {
					return ACCESS_GRANTED;
				}
			}
		}
		
		return result;
	}

}
