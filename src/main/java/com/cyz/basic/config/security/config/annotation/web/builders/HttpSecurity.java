package com.cyz.basic.config.security.config.annotation.web.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.config.annotation.AbstractConfiguredSecurityBuilder;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.SecurityBuilder;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;
import com.cyz.basic.config.security.web.DefaultSecurityFilterChain;
import com.cyz.basic.config.security.web.util.matcher.AnyRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public final class HttpSecurity extends
    AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity> implements 
    SecurityBuilder<DefaultSecurityFilterChain>, HttpSecurityBuilder<HttpSecurity>{
	
	//private final RequestMatcherConfigurer requestMatcherConfigurer;
	private List<Filter> filters = new ArrayList<>();
	private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
	private FilterComparator comparator = new FilterComparator();
	
	/**
	 * Creates a new instance
	 * @param objectPostProcessor the {@link ObjectPostProcessor} that should be used
	 * @param authenticationBuilder the {@link AuthenticationManagerBuilder} to use for
	 * additional updates
	 * @param sharedObjects the shared Objects to initialize the {@link HttpSecurity} with
	 * @see WebSecurityConfiguration
	 */
	@SuppressWarnings("unchecked")
	public HttpSecurity(ObjectPostProcessor<Object> objectPostProcessor,
			AuthenticationManagerBuilder authenticationBuilder,
			Map<Class<? extends Object>, Object> sharedObjects) {
		super(objectPostProcessor);
		Assert.notNull(authenticationBuilder, "authenticationBuilder cannot be null");
		setSharedObject(AuthenticationManagerBuilder.class, authenticationBuilder);
		for (Map.Entry<Class<? extends Object>, Object> entry : sharedObjects
				.entrySet()) {
			setSharedObject((Class<Object>) entry.getKey(), entry.getValue());
		}
		ApplicationContext context = (ApplicationContext) sharedObjects
				.get(ApplicationContext.class);
		//this.requestMatcherConfigurer = new RequestMatcherConfigurer(context);
	}
	
	private ApplicationContext getContext() {
		return getSharedObject(ApplicationContext.class);
	}

	@Override
	public HttpSecurity authenticationProvider(AuthenticationProvider authenticationProvider) {
		getAuthenticationRegistry().authenticationProvider(authenticationProvider);
		return this;
	}

	@Override
	public HttpSecurity userDetailsService(UserDetailsService userDetailsService) throws Exception {
		getAuthenticationRegistry().userDetailsService(userDetailsService);
		return this;
	}
	
	private AuthenticationManagerBuilder getAuthenticationRegistry() {
		return getSharedObject(AuthenticationManagerBuilder.class);
	}

	@Override
	public HttpSecurity addFilterAfter(Filter filter, Class<? extends Filter> afterFilter) {
		comparator.registerAfter(filter.getClass(), afterFilter);
		return addFilter(filter);
	}

	@Override
	public HttpSecurity addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter) {
		comparator.registerBefore(filter.getClass(), beforeFilter);
		return addFilter(filter);
	}

	@Override
	public HttpSecurity addFilter(Filter filter) {
		Class<? extends Filter> filterClass = filter.getClass();
		if (!comparator.isRegistered(filterClass)) {
			throw new IllegalArgumentException(
					"The Filter class "
							+ filterClass.getName()
							+ " does not have a registered order and cannot be added without a specified order. Consider using addFilterBefore or addFilterAfter instead.");
		}
		this.filters.add(filter);
		return this;
	}

	@Override
	protected DefaultSecurityFilterChain performBuild() throws Exception {
		Collections.sort(filters, comparator);
		return new DefaultSecurityFilterChain(requestMatcher, filters);
	}
	
	/**
	 * Allows restricting access based upon the {@link HttpServletRequest} using
	 *
	 * <h2>Example Configurations</h2>
	 *
	 * The most basic example is to configure all URLs to require the role "ROLE_USER".
	 * The configuration below requires authentication to every URL and will grant access
	 * to both the user "admin" and "user".
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin();
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;)
	 * 				.and().withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;ADMIN&quot;, &quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * We can also configure multiple URLs. The configuration below requires
	 * authentication to every URL and will grant access to URLs starting with /admin/ to
	 * only the "admin" user. All other URLs either user can access.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/admin/**&quot;).hasRole(&quot;ADMIN&quot;)
	 * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin();
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;)
	 * 				.and().withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;ADMIN&quot;, &quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * Note that the matchers are considered in order. Therefore, the following is invalid
	 * because the first matcher matches every request and will never get to the second
	 * mapping:
	 *
	 * <pre>
	 * http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).antMatchers(&quot;/admin/**&quot;)
	 * 		.hasRole(&quot;ADMIN&quot;)
	 * </pre>
	 *
	 * @see #requestMatcher(RequestMatcher)
	 *
	 * @return
	 * @throws Exception
	 */
	public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests()
			throws Exception {
		ApplicationContext context = getContext();
		return getOrApply(new ExpressionUrlAuthorizationConfigurer<>(context))
				.getRegistry();
	}
	
	/**
	 * If the {@link SecurityConfigurer} has already been specified get the original,
	 * otherwise apply the new {@link SecurityConfigurerAdapter}.
	 *
	 * @param configurer the {@link SecurityConfigurer} to apply if one is not found for
	 * this {@link SecurityConfigurer} class.
	 * @return the current {@link SecurityConfigurer} for the configurer passed in
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private <C extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> C getOrApply(
			C configurer) throws Exception {
		C existingConfig = (C) getConfigurer(configurer.getClass());
		if (existingConfig != null) {
			return existingConfig;
		}
		return apply(configurer);
	}

}
