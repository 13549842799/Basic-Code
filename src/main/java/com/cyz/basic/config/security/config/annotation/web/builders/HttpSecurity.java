package com.cyz.basic.config.security.config.annotation.web.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.config.annotation.AbstractConfiguredSecurityBuilder;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.SecurityBuilder;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import com.cyz.basic.config.security.config.annotation.web.AbstractRequestMatcherRegistry;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.config.annotation.web.configurers.AnonymousConfigurer;
import com.cyz.basic.config.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import com.cyz.basic.config.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import com.cyz.basic.config.security.config.annotation.web.configurers.FormLoginConfigurer;
import com.cyz.basic.config.security.config.annotation.web.configurers.HeadersConfigurer;
import com.cyz.basic.config.security.config.annotation.web.configurers.LogoutConfigurer;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;
import com.cyz.basic.config.security.web.DefaultSecurityFilterChain;
import com.cyz.basic.config.security.web.servlet.util.matcher.MvcRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.AnyRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.OrRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

public final class HttpSecurity extends
    AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity> implements 
    SecurityBuilder<DefaultSecurityFilterChain>, HttpSecurityBuilder<HttpSecurity>{
	
	private final RequestMatcherConfigurer requestMatcherConfigurer;
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
		this.requestMatcherConfigurer = new RequestMatcherConfigurer(context);
	}
	
	private ApplicationContext getContext() {
		return getSharedObject(ApplicationContext.class);
	}
	
	/**
	 * Adds the Security headers to the response. This is activated by default when using
	 * {@link WebSecurityConfigurerAdapter}'s default constructor. Accepting the
	 * default provided by {@link WebSecurityConfigurerAdapter} or only invoking
	 * {@link #headers()} without invoking additional methods on it, is the equivalent of:
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 *     protected void configure(HttpSecurity http) throws Exception {
	 *         http
	 *             .headers()
	 *                 .contentTypeOptions()
	 *                 .and()
	 *                 .xssProtection()
	 *                 .and()
	 *                 .cacheControl()
	 *                 .and()
	 *                 .httpStrictTransportSecurity()
	 *                 .and()
	 *                 .frameOptions()
	 *                 .and()
	 *             ...;
	 *     }
	 * }
	 * </pre>
	 *
	 * You can disable the headers using the following:
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 *     protected void configure(HttpSecurity http) throws Exception {
	 *         http
	 *             .headers().disable()
	 *             ...;
	 *     }
	 * }
	 * </pre>
	 *
	 * You can enable only a few of the headers by first invoking
	 * {@link HeadersConfigurer#defaultsDisabled()}
	 * and then invoking the appropriate methods on the {@link #headers()} result.
	 * For example, the following will enable {@link HeadersConfigurer#cacheControl()} and
	 * {@link HeadersConfigurer#frameOptions()} only.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 *     protected void configure(HttpSecurity http) throws Exception {
	 *         http
	 *             .headers()
	 *                  .defaultsDisabled()
	 *                  .cacheControl()
	 *                  .and()
	 *                  .frameOptions()
	 *                  .and()
	 *             ...;
	 *     }
	 * }
	 * </pre>
	 *
	 * You can also choose to keep the defaults but explicitly disable a subset of headers.
	 * For example, the following will enable all the default headers except
	 * {@link HeadersConfigurer#frameOptions()}.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 *     protected void configure(HttpSecurity http) throws Exception {
	 *         http
	 *             .headers()
	 *                  .frameOptions()
	 *                  	.disable()
	 *                  .and()
	 *             ...;
	 *     }
	 * }
	 * </pre>
	 *
	 * @return
	 * @throws Exception
	 * @see HeadersConfigurer
	 */
	public HeadersConfigurer<HttpSecurity> headers() throws Exception {
		return getOrApply(new HeadersConfigurer<>());
	}
	
	public <C> void setSharedObject(Class<C> sharedType, C object) {
		super.setSharedObject(sharedType, object);
	}
	
	@Override
	protected void beforeConfigure() throws Exception {
		setSharedObject(AuthenticationManager.class, getAuthenticationRegistry().build());
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
	
	/**
	 * Adds the Filter at the location of the specified Filter class. For example, if you
	 * want the filter CustomFilter to be registered in the same position as
	 * {@link UsernamePasswordAuthenticationFilter}, you can invoke:
	 *
	 * <pre>
	 * addFilterAt(new CustomFilter(), UsernamePasswordAuthenticationFilter.class)
	 * </pre>
	 *
	 * Registration of multiple Filters in the same location means their ordering is not
	 * deterministic. More concretely, registering multiple Filters in the same location
	 * does not override existing Filters. Instead, do not register Filters you do not
	 * want to use.
	 *
	 * @param filter the Filter to register
	 * @param atFilter the location of another {@link Filter} that is already registered
	 * (i.e. known) with Spring Security.
	 * @return the {@link HttpSecurity} for further customizations
	 */
	public HttpSecurity addFilterAt(Filter filter, Class<? extends Filter> atFilter) {
		this.comparator.registerAt(filter.getClass(), atFilter);
		return addFilter(filter);
	}
	
	/**
	 * Allows specifying which {@link HttpServletRequest} instances this
	 * {@link HttpSecurity} will be invoked on. This method allows for easily invoking the
	 * {@link HttpSecurity} for multiple different {@link RequestMatcher} instances. If
	 * only a single {@link RequestMatcher} is necessary consider using {@link #mvcMatcher(String)},
	 * {@link #antMatcher(String)}, {@link #regexMatcher(String)}, or
	 * {@link #requestMatcher(RequestMatcher)}.
	 *
	 * <p>
	 * Invoking {@link #requestMatchers()} will not override previous invocations of {@link #mvcMatcher(String)}},
	 * {@link #requestMatchers()}, {@link #antMatcher(String)},
	 * {@link #regexMatcher(String)}, and {@link #requestMatcher(RequestMatcher)}.
	 * </p>
	 *
	 * <h3>Example Configurations</h3>
	 *
	 * The following configuration enables the {@link HttpSecurity} for URLs that begin
	 * with "/api/" or "/oauth/".
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http
	 * 			.requestMatchers()
	 * 				.antMatchers(&quot;/api/**&quot;, &quot;/oauth/**&quot;)
	 * 				.and()
	 * 			.authorizeRequests()
	 * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
	 * 				.and()
	 * 			.httpBasic();
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth
	 * 			.inMemoryAuthentication()
	 * 				.withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * The configuration below is the same as the previous configuration.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http
	 * 			.requestMatchers()
	 * 				.antMatchers(&quot;/api/**&quot;)
	 * 				.antMatchers(&quot;/oauth/**&quot;)
	 * 				.and()
	 * 			.authorizeRequests()
	 * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
	 * 				.and()
	 * 			.httpBasic();
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth
	 * 			.inMemoryAuthentication()
	 * 				.withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * The configuration below is also the same as the above configuration.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http
	 * 			.requestMatchers()
	 * 				.antMatchers(&quot;/api/**&quot;)
	 * 				.and()
	 *			 .requestMatchers()
	 * 				.antMatchers(&quot;/oauth/**&quot;)
	 * 				.and()
	 * 			.authorizeRequests()
	 * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
	 * 				.and()
	 * 			.httpBasic();
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth
	 * 			.inMemoryAuthentication()
	 * 				.withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the {@link RequestMatcherConfigurer} for further customizations
	 */
	public RequestMatcherConfigurer requestMatchers() {
		return requestMatcherConfigurer;
	}
	
	/**
	 * Allows configuring the {@link HttpSecurity} to only be invoked when matching the
	 * provided {@link RequestMatcher}. If more advanced configuration is necessary,
	 * consider using {@link #requestMatchers()}.
	 *
	 * <p>
	 * Invoking {@link #requestMatcher(RequestMatcher)} will override previous invocations
	 * of {@link #requestMatchers()}, {@link #mvcMatcher(String)}, {@link #antMatcher(String)},
	 * {@link #regexMatcher(String)}, and {@link #requestMatcher(RequestMatcher)}.
	 * </p>
	 *
	 * @param requestMatcher the {@link RequestMatcher} to use (i.e. new
	 * AntPathRequestMatcher("/admin/**","GET") )
	 * @return the {@link HttpSecurity} for further customizations
	 * @see #requestMatchers()
	 * @see #antMatcher(String)
	 * @see #regexMatcher(String)
	 */
	public HttpSecurity requestMatcher(RequestMatcher requestMatcher) {
		this.requestMatcher = requestMatcher;
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
	
	/**
	 * Specifies to support form based authentication. If
	 * {@link FormLoginConfigurer#loginPage(String)} is not specified a default login page
	 * will be generated.
	 *
	 * <h2>Example Configurations</h2>
	 *
	 * The most basic configuration defaults to automatically generating a login page at
	 * the URL "/login", redirecting to "/login?error" for authentication failure. The
	 * details of the login page can be found on
	 * {@link FormLoginConfigurer#loginPage(String)}
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class FormLoginSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin();
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * The configuration below demonstrates customizing the defaults.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class FormLoginSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin()
	 * 				.usernameParameter(&quot;username&quot;) // default is username
	 * 				.passwordParameter(&quot;password&quot;) // default is password
	 * 				.loginPage(&quot;/authentication/login&quot;) // default is /login with an HTTP get
	 * 				.failureUrl(&quot;/authentication/login?failed&quot;) // default is /login?error
	 * 				.loginProcessingUrl(&quot;/authentication/login/process&quot;); // default is /login
	 * 																		// with an HTTP
	 * 																		// post
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @see FormLoginConfigurer#loginPage(String)
	 *
	 * @return
	 * @throws Exception
	 */
	public FormLoginConfigurer<HttpSecurity> formLogin() throws Exception {
		return getOrApply(new FormLoginConfigurer<>());
	}
	
	/**
	 * Allows configuring exception handling. This is automatically applied when using
	 * {@link WebSecurityConfigurerAdapter}.
	 *
	 * @return the {@link ExceptionHandlingConfigurer} for further customizations
	 * @throws Exception
	 */
	public ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling() throws Exception {
		return getOrApply(new ExceptionHandlingConfigurer<>());
	}

	/**
	 * Provides logout support. This is automatically applied when using
	 * {@link WebSecurityConfigurerAdapter}. The default is that accessing the URL
	 * "/logout" will log the user out by invalidating the HTTP Session, cleaning up any
	 * {@link #rememberMe()} authentication that was configured, clearing the
	 * {@link SecurityContextHolder}, and then redirect to "/login?success".
	 *
	 * <h2>Example Custom Configuration</h2>
	 *
	 * The following customization to log out when the URL "/custom-logout" is invoked.
	 * Log out will remove the cookie named "remove", not invalidate the HttpSession,
	 * clear the SecurityContextHolder, and upon completion redirect to "/logout-success".
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class LogoutSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin()
	 * 				.and()
	 * 				// sample logout customization
	 * 				.logout().deleteCookies(&quot;remove&quot;).invalidateHttpSession(false)
	 * 				.logoutUrl(&quot;/custom-logout&quot;).logoutSuccessUrl(&quot;/logout-success&quot;);
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the {@link LogoutConfigurer} for further customizations
	 * @throws Exception
	 */
	public LogoutConfigurer<HttpSecurity> logout() throws Exception {
		return getOrApply(new LogoutConfigurer<>());
	}
	
	/**
	 * Allows configuring how an anonymous user is represented. This is automatically
	 * applied when used in conjunction with {@link WebSecurityConfigurerAdapter}. By
	 * default anonymous users will be represented with an
	 * {@link org.springframework.security.authentication.AnonymousAuthenticationToken}
	 * and contain the role "ROLE_ANONYMOUS".
	 *
	 * <h2>Example Configuration</h2>
	 *
	 * The following configuration demonstrates how to specify that anonymous users should
	 * contain the role "ROLE_ANON" instead.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class AnononymousSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin()
	 * 				.and()
	 * 				// sample anonymous customization
	 * 				.anonymous().authorities(&quot;ROLE_ANON&quot;);
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * The following demonstrates how to represent anonymous users as null. Note that this
	 * can cause {@link NullPointerException} in code that assumes anonymous
	 * authentication is enabled.
	 *
	 * <pre>
	 * &#064;Configuration
	 * &#064;EnableWebSecurity
	 * public class AnononymousSecurityConfig extends WebSecurityConfigurerAdapter {
	 *
	 * 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin()
	 * 				.and()
	 * 				// sample anonymous customization
	 * 				.anonymous().disabled();
	 * 	}
	 *
	 * 	&#064;Override
	 * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	 * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return
	 * @throws Exception
	 */
	public AnonymousConfigurer<HttpSecurity> anonymous() throws Exception {
		return getOrApply(new AnonymousConfigurer<>());
	}
	
	/**
	 * An extension to {@link RequestMatcherConfigurer} that allows optionally configuring
	 * the servlet path.
	 *
	 * @author Rob Winch
	 */
	public final class MvcMatchersRequestMatcherConfigurer extends RequestMatcherConfigurer {

		/**
		 * Creates a new instance
		 * @param context the {@link ApplicationContext} to use
		 * @param matchers the {@link MvcRequestMatcher} instances to set the servlet path
		 * on if {@link #servletPath(String)} is set.
		 */
		private MvcMatchersRequestMatcherConfigurer(ApplicationContext context,
				List<MvcRequestMatcher> matchers) {
			super(context);
			this.matchers = new ArrayList<>(matchers);
		}

		public RequestMatcherConfigurer servletPath(String servletPath) {
			for (RequestMatcher matcher : this.matchers) {
				((MvcRequestMatcher) matcher).setServletPath(servletPath);
			}
			return this;
		}

	}
	
	/**
	 * Allows mapping HTTP requests that this {@link HttpSecurity} will be used for
	 *
	 * @author Rob Winch
	 * @since 3.2
	 */
	public class RequestMatcherConfigurer
			extends AbstractRequestMatcherRegistry<RequestMatcherConfigurer> {

		protected List<RequestMatcher> matchers = new ArrayList<>();

		/**
		 * @param context
		 */
		private RequestMatcherConfigurer(ApplicationContext context) {
			setApplicationContext(context);
		}

		@Override
		public MvcMatchersRequestMatcherConfigurer mvcMatchers(HttpMethod method,
				String... mvcPatterns) {
			List<MvcRequestMatcher> mvcMatchers = createMvcMatchers(method, mvcPatterns);
			setMatchers(mvcMatchers);
			return new MvcMatchersRequestMatcherConfigurer(getContext(), mvcMatchers);
		}

		@Override
		public MvcMatchersRequestMatcherConfigurer mvcMatchers(String... patterns) {
			return mvcMatchers(null, patterns);
		}

		@Override
		protected RequestMatcherConfigurer chainRequestMatchers(
				List<RequestMatcher> requestMatchers) {
			setMatchers(requestMatchers);
			return this;
		}

		private void setMatchers(List<? extends RequestMatcher> requestMatchers) {
			this.matchers.addAll(requestMatchers);
			requestMatcher(new OrRequestMatcher(this.matchers));
		}

		/**
		 * Return the {@link HttpSecurity} for further customizations
		 *
		 * @return the {@link HttpSecurity} for further customizations
		 */
		public HttpSecurity and() {
			return HttpSecurity.this;
		}

	}
}
