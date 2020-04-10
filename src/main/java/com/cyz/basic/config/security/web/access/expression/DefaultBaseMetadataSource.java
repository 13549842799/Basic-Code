package com.cyz.basic.config.security.web.access.expression;

import java.util.Collection;
import java.util.Map;

import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public class DefaultBaseMetadataSource implements FilterInvocationSecurityMetadataSource {
	
	private Map<RequestMatcher, Collection<ConfigAttribute>> requestMap;
	
	public DefaultBaseMetadataSource() {
		
	}
	
    public DefaultBaseMetadataSource(Map<RequestMatcher, Collection<ConfigAttribute>> requestMap) {
		
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		// TODO Auto-generated method stub
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
