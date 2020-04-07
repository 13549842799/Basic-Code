package com.cyz.basic.config.security.manager;

import java.util.Collection;
import java.util.List;

import com.cyz.basic.config.security.access.AccessDecisionVoter;
import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.access.vote.AbstractAccessDecisionManager;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AccessDeniedException;
import com.cyz.basic.config.security.exception.InsufficientAuthenticationException;

public class MyAccessDecisionManager extends AbstractAccessDecisionManager {

	protected MyAccessDecisionManager(List<AccessDecisionVoter<? extends Object>> decisionVoters) {
		super(decisionVoters);
	}

	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		
	}


}
