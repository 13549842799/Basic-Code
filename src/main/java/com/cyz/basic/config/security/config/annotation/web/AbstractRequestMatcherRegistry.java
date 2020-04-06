package com.cyz.basic.config.security.config.annotation.web;

import java.util.Arrays;
import java.util.List;

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

	/**
	 * Maps any request.
	 *
	 * @return the object that is chained after creating the {@link RequestMatcher}
	 */
	public C anyRequest() {
		return requestMatchers(ANY_REQUEST);
	}
	
	/**
	 * Associates a list of {@link RequestMatcher} instances with the
	 * {@link AbstractConfigAttributeRequestMatcherRegistry}
	 *
	 * @param requestMatchers the {@link RequestMatcher} instances
	 *
	 * @return the object that is chained after creating the {@link RequestMatcher}
	 */
	public C requestMatchers(RequestMatcher... requestMatchers) {
		return chainRequestMatchers(Arrays.asList(requestMatchers));
	}
	
	/**
	 * Subclasses should implement this method for returning the object that is chained to
	 * the creation of the {@link RequestMatcher} instances.
	 *
	 * @param requestMatchers the {@link RequestMatcher} instances that were created
	 * @return the chained Object for the subclass which allows association of something
	 * else to the {@link RequestMatcher}
	 */
	protected abstract C chainRequestMatchers(List<RequestMatcher> requestMatchers);
}
