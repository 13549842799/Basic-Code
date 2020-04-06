package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.List;

import com.cyz.basic.config.security.config.annotation.web.AbstractRequestMatcherRegistry;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public abstract class AbstractConfigAttributeRequestMatcherRegistry<C> extends AbstractRequestMatcherRegistry<C> {
	
	private List<RequestMatcher> unmappedMatchers;
	
	/**
	 * Marks the {@link RequestMatcher}'s as unmapped and then calls
	 * {@link #chainRequestMatchersInternal(List)}.
	 *
	 * @param requestMatchers the {@link RequestMatcher} instances that were created
	 * @return the chained Object for the subclass which allows association of something
	 * else to the {@link RequestMatcher}
	 */
	protected final C chainRequestMatchers(List<RequestMatcher> requestMatchers) {
		this.unmappedMatchers = requestMatchers;
		return chainRequestMatchersInternal(requestMatchers);
	}
	
	/**
	 * Subclasses should implement this method for returning the object that is chained to
	 * the creation of the {@link RequestMatcher} instances.
	 *
	 * @param requestMatchers the {@link RequestMatcher} instances that were created
	 * @return the chained Object for the subclass which allows association of something
	 * else to the {@link RequestMatcher}
	 */
	protected abstract C chainRequestMatchersInternal(List<RequestMatcher> requestMatchers);

}
