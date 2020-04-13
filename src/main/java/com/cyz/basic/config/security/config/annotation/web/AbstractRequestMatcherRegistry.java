package com.cyz.basic.config.security.config.annotation.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.web.servlet.util.matcher.MvcRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.AnyRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public abstract class AbstractRequestMatcherRegistry<C> {
	
	private static final String HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME = "mvcHandlerMappingIntrospector";
	
	private static final RequestMatcher ANY_REQUEST = AnyRequestMatcher.INSTANCE;
	
	private ApplicationContext context;
	
	private boolean anyRequestConfigured = false;

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
		Assert.state(!this.anyRequestConfigured, "Can't configure requestMatchers after anyRequest");
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
	
	/**
	 * <p>
	 * Maps an {@link MvcRequestMatcher} that does not care which {@link HttpMethod} is
	 * used. This matcher will use the same rules that Spring MVC uses for matching. For
	 * example, often times a mapping of the path "/path" will match on "/path", "/path/",
	 * "/path.html", etc.
	 * </p>
	 * <p>
	 * If the current request will not be processed by Spring MVC, a reasonable default
	 * using the pattern as a ant pattern will be used.
	 * </p>
	 *
	 * @param mvcPatterns the patterns to match on. The rules for matching are defined by
	 * Spring MVC
	 * @return the object that is chained after creating the {@link RequestMatcher}.
	 */
	public abstract C mvcMatchers(String... mvcPatterns);
	
	/**
	 * <p>
	 * Maps an {@link MvcRequestMatcher} that also specifies a specific {@link HttpMethod}
	 * to match on. This matcher will use the same rules that Spring MVC uses for
	 * matching. For example, often times a mapping of the path "/path" will match on
	 * "/path", "/path/", "/path.html", etc.
	 * </p>
	 * <p>
	 * If the current request will not be processed by Spring MVC, a reasonable default
	 * using the pattern as a ant pattern will be used.
	 * </p>
	 *
	 * @param method the HTTP method to match on
	 * @param mvcPatterns the patterns to match on. The rules for matching are defined by
	 * Spring MVC
	 * @return the object that is chained after creating the {@link RequestMatcher}.
	 */
	public abstract C mvcMatchers(HttpMethod method, String... mvcPatterns);
	
	/**
	 * Creates {@link MvcRequestMatcher} instances for the method and patterns passed in
	 *
	 * @param method the HTTP method to use or null if any should be used
	 * @param mvcPatterns the Spring MVC patterns to match on
	 * @return a List of {@link MvcRequestMatcher} instances
	 */
	protected final List<MvcRequestMatcher> createMvcMatchers(HttpMethod method,
			String... mvcPatterns) {
		Assert.state(!this.anyRequestConfigured, "Can't configure mvcMatchers after anyRequest");
		ObjectPostProcessor<Object> opp = this.context.getBean(ObjectPostProcessor.class);
		if (!this.context.containsBean(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME)) {
			throw new NoSuchBeanDefinitionException("A Bean named " + HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME +" of type " + HandlerMappingIntrospector.class.getName()
				+ " is required to use MvcRequestMatcher. Please ensure Spring Security & Spring MVC are configured in a shared ApplicationContext.");
		}
		HandlerMappingIntrospector introspector = this.context.getBean(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME,
			HandlerMappingIntrospector.class);
		List<MvcRequestMatcher> matchers = new ArrayList<>(
				mvcPatterns.length);
		for (String mvcPattern : mvcPatterns) {
			MvcRequestMatcher matcher = new MvcRequestMatcher(introspector, mvcPattern);
			opp.postProcess(matcher);

			if (method != null) {
				matcher.setMethod(method);
			}
			matchers.add(matcher);
		}
		return matchers;
	}
}
