package com.cyz.basic.config.security.config.annotation.web;

import org.springframework.context.ApplicationContext;

import com.cyz.basic.config.security.web.util.matcher.AnyRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public abstract class AbstractRequestMatcherRegistry<C> {
	
	private static final String HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME = "mvcHandlerMappingIntrospector";
	
	private static final RequestMatcher ANY_REQUEST = AnyRequestMatcher.INSTANCE;
	
	private ApplicationContext context;

	protected final void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

}
