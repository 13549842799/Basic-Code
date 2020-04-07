package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.config.annotation.web.AbstractRequestMatcherRegistry;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public abstract class AbstractConfigAttributeRequestMatcherRegistry<C> extends AbstractRequestMatcherRegistry<C> {
	
	private List<UrlMapping> urlMappings = new ArrayList<>();
	
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
	
	/**
	 * A mapping of {@link RequestMatcher} to {@link Collection} of
	 * {@link ConfigAttribute} instances
	 */
	static final class UrlMapping {
		private RequestMatcher requestMatcher;
		private Collection<ConfigAttribute> configAttrs;

		UrlMapping(RequestMatcher requestMatcher, Collection<ConfigAttribute> configAttrs) {
			this.requestMatcher = requestMatcher;
			this.configAttrs = configAttrs;
		}

		public RequestMatcher getRequestMatcher() {
			return requestMatcher;
		}

		public Collection<ConfigAttribute> getConfigAttrs() {
			return configAttrs;
		}
	}
	
	/**
	 * Adds a {@link UrlMapping} added by subclasses in
	 * {@link #chainRequestMatchers(java.util.List)} at a particular index.
	 *
	 * @param index the index to add a {@link UrlMapping}
	 * @param urlMapping {@link UrlMapping} the mapping to add
	 */
	final void addMapping(int index, UrlMapping urlMapping) {
		this.urlMappings.add(index, urlMapping);
	}
	
	/**
	 * Adds a {@link UrlMapping} added by subclasses in
	 * {@link #chainRequestMatchers(java.util.List)} and resets the unmapped
	 * {@link RequestMatcher}'s.
	 *
	 * @param urlMapping {@link UrlMapping} the mapping to add
	 */
	final void addMapping(UrlMapping urlMapping) {
		this.unmappedMatchers = null;
		this.urlMappings.add(urlMapping);
	}

}
