package com.cyz.basic.config.security.service;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.access.intercept.FilterInvocationSecurityMetadataSource;
import com.cyz.basic.config.security.web.FilterInvocation;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public abstract class SecurityMetadataSourceSupport implements FilterInvocationSecurityMetadataSource {
	
	private Map<RequestMatcher, Collection<ConfigAttribute>> requestMap;
	
	public SecurityMetadataSourceSupport() {
		
	}
	
	protected abstract Map<RequestMatcher, Collection<ConfigAttribute>> createMap();

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		final HttpServletRequest request = ((FilterInvocation) object).getRequest();
		for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : createMap()
				.entrySet()) {
			System.out.println(entry.getKey());
			if (entry.getKey().matches(request)) {
				return entry.getValue();
			}
		}
		return null;
	}
	

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return false;
	}

}
