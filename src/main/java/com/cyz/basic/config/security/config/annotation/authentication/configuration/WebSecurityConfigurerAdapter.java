package com.cyz.basic.config.security.config.annotation.authentication.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import com.cyz.basic.config.security.access.intercept.FilterSecurityInterceptor;
import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.authentication.AuthenticationTrustResolver;
import com.cyz.basic.config.security.authentication.AuthenticationTrustResolverImpl;
import com.cyz.basic.config.security.authentication.DefaultAuthenticationEventPublisher;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import com.cyz.basic.config.security.config.annotation.web.WebSecurityConfigurer;
import com.cyz.basic.config.security.config.annotation.web.builders.HttpSecurity;
import com.cyz.basic.config.security.config.annotation.web.builders.WebSecurity;
import com.cyz.basic.config.security.crypto.factory.PasswordEncoderFactories;
import com.cyz.basic.config.security.crypto.password.PasswordEncoder;



/**
 * Provides a convenient base class for creating a {@link WebSecurityConfigurer}
 * instance. The implementation allows customization by overriding methods.
 *
 * <p>
 * Will automatically apply the result of looking up
 * {@link AbstractHttpConfigurer} from {@link SpringFactoriesLoader} to allow
 * developers to extend the defaults.
 * To do this, you must create a class that extends AbstractHttpConfigurer and then create a file in the classpath at "META-INF/spring.factories" that looks something like:
 * </p>
 * <pre>
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer
 * </pre>
 * If you have multiple classes that should be added you can use "," to separate the values. For example:
 *
 * <pre>
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer, sample.OtherThatExtendsAbstractHttpConfigurer
 * </pre>
 *
 *webSecurityConfigurer适配类可以创建或者返回HttpSecurity实例
 *
 * @see EnableWebSecurity
 *
 * @author Rob Winch
 */
@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {
	
	private final Log logger = LogFactory.getLog(WebSecurityConfigurerAdapter.class);
	
	private ApplicationContext context;
	
	private ContentNegotiationStrategy contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
	
	private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
		public <T> T postProcess(T object) {
			throw new IllegalStateException(
					ObjectPostProcessor.class.getName()
							+ " is a required bean. Ensure you have used @EnableWebSecurity and @Configuration");
		}
	};
	
	private AuthenticationConfiguration authenticationConfiguration;
	private AuthenticationManagerBuilder authenticationBuilder;
	private AuthenticationManagerBuilder localConfigureAuthenticationBldr;
	private boolean disableLocalConfigureAuthenticationBldr;
	private boolean authenticationManagerInitialized;
	private AuthenticationManager authenticationManager;
	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	private HttpSecurity http;
	private boolean disableDefaults; //是否禁用默认的配置configuration
	
	/**
	 * Creates an instance with the default configuration enabled.
	 */
	protected WebSecurityConfigurerAdapter() {
		this(false);
	}

	/**
	 * Creates an instance which allows specifying if the default configuration should be
	 * enabled. Disabling the default configuration should be considered more advanced
	 * usage as it requires more understanding of how the framework is implemented.
	 *
	 * @param disableDefaults true if the default configuration should be disabled, else
	 * false
	 */
	protected WebSecurityConfigurerAdapter(boolean disableDefaults) {
		this.disableDefaults = disableDefaults;
	}
	
	/**
	 * Creates the {@link HttpSecurity} or returns the current instance
	 *
	 * ] * @return the {@link HttpSecurity}
	 * @throws Exception
	 */
	protected final HttpSecurity getHttp() throws Exception {
		if (http != null) {
			return http;
		}
		
		DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
				.postProcess(new DefaultAuthenticationEventPublisher());
		localConfigureAuthenticationBldr.authenticationEventPublisher(eventPublisher);
		
		AuthenticationManager authenticationManager = authenticationManager();
		authenticationBuilder.parentAuthenticationManager(authenticationManager);
		authenticationBuilder.authenticationEventPublisher(eventPublisher);		
		Map<Class<? extends Object>, Object> sharedObjects = createSharedObjects();
		
		http = new HttpSecurity(objectPostProcessor, authenticationBuilder, sharedObjects);
		if (!disableDefaults) {
			http
			.exceptionHandling().and()
			.headers().and()
			.anonymous().and()
			.logout();
		}
		System.out.println("getHttp");
		configure(http);
		return http;
	}
	
	/**
	 * Override this method to configure {@link WebSecurity}. For example, if you wish to
	 * ignore certain requests.
	 */
	public void configure(WebSecurity web) throws Exception {
	}
	
	/**
	 * Override this method to configure the {@link HttpSecurity}. Typically subclasses
	 * should not invoke this method by calling super as it may override their
	 * configuration. The default configuration is:
	 *
	 * <pre>
	 * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
	 * </pre>
	 *
	 * @param http the {@link HttpSecurity} to modify
	 * @throws Exception if an error occurs
	 */
	// @formatter:off
	protected void configure(HttpSecurity http) throws Exception {
		logger.debug("Using default configure(HttpSecurity). If subclassed this will potentially override subclass configure(HttpSecurity).");

		http.authorizeRequests().anyRequest().authenticated()
			.and().formLogin();/*.and()
			.httpBasic();*/
	}
	
	/**
	 * Gets the ApplicationContext
	 * @return the context
	 */
	protected final ApplicationContext getApplicationContext() {
		return this.context;
	}

	@Autowired
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;

		@SuppressWarnings("unchecked")
		ObjectPostProcessor<Object> objectPostProcessor = context.getBean(ObjectPostProcessor.class);
		LazyPasswordEncoder passwordEncoder = new LazyPasswordEncoder(context);

		authenticationBuilder = new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, passwordEncoder);
		localConfigureAuthenticationBldr = new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, passwordEncoder) {
			@Override
			public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
				authenticationBuilder.eraseCredentials(eraseCredentials);
				return super.eraseCredentials(eraseCredentials);
			}

		};
	}
	
	@Autowired(required = false)
	public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
		this.trustResolver = trustResolver;
	}

	@Autowired(required = false)
	public void setContentNegotationStrategy(
			ContentNegotiationStrategy contentNegotiationStrategy) {
		this.contentNegotiationStrategy = contentNegotiationStrategy;
	}
	
	@Autowired
	public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
		this.objectPostProcessor = objectPostProcessor;
	}

	@Autowired
	public void setAuthenticationConfiguration(
			AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
		
	}
	
	
	public void init(final WebSecurity web) throws Exception {
		final HttpSecurity http = getHttp();
		web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
			public void run() {
				FilterSecurityInterceptor securityInterceptor = http
						.getSharedObject(FilterSecurityInterceptor.class);
				web.securityInterceptor(securityInterceptor);
			}
		});
		logger.info("结束");
	}
	
	/**
	 * Creates the shared objects
	 *
	 * @return the shared Objects
	 */
	private Map<Class<? extends Object>, Object> createSharedObjects() {
		Map<Class<? extends Object>, Object> sharedObjects = new HashMap<Class<? extends Object>, Object>();
		sharedObjects.putAll(localConfigureAuthenticationBldr.getSharedObjects());
		//sharedObjects.put(UserDetailsService.class, userDetailsService());
		sharedObjects.put(ApplicationContext.class, context);
		sharedObjects.put(ContentNegotiationStrategy.class, contentNegotiationStrategy);
		sharedObjects.put(AuthenticationTrustResolver.class, trustResolver);
		return sharedObjects;
	}
	
	static class DefaultPasswordEncoderAuthenticationManagerBuilder extends AuthenticationManagerBuilder {
		private PasswordEncoder defaultPasswordEncoder;

		/**
		 * Creates a new instance
		 *
		 * @param objectPostProcessor the {@link ObjectPostProcessor} instance to use.
		 */
		DefaultPasswordEncoderAuthenticationManagerBuilder(
			ObjectPostProcessor<Object> objectPostProcessor, PasswordEncoder defaultPasswordEncoder) {
			super(objectPostProcessor);
			this.defaultPasswordEncoder = defaultPasswordEncoder;
		}

		/*@Override
		public <T extends UserDetailsService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> userDetailsService(
			T userDetailsService) throws Exception {
			return super.userDetailsService(userDetailsService)
				.passwordEncoder(this.defaultPasswordEncoder);
		}*/
	}
	
	/**
	 * Gets the {@link AuthenticationManager} to use. The default strategy is if
	 * {@link #configure(AuthenticationManagerBuilder)} method is overridden to use the
	 * {@link AuthenticationManagerBuilder} that was passed in. Otherwise, autowire the
	 * {@link AuthenticationManager} by type.
	 *
	 * @return the {@link AuthenticationManager} to use
	 * @throws Exception
	 */
	protected AuthenticationManager authenticationManager() throws Exception {
		if (!authenticationManagerInitialized) {
			configure(localConfigureAuthenticationBldr);
			if (disableLocalConfigureAuthenticationBldr) {
				authenticationManager = authenticationConfiguration
						.getAuthenticationManager();
			}
			else {
				authenticationManager = localConfigureAuthenticationBldr.build();
			}
			authenticationManagerInitialized = true;
		}
		return authenticationManager;
	}
	
	/**
	 * Used by the default implementation of {@link #authenticationManager()} to attempt
	 * to obtain an {@link AuthenticationManager}. If overridden, the
	 * {@link AuthenticationManagerBuilder} should be used to specify the
	 * {@link AuthenticationManager}.
	 *
	 * <p>
	 * The {@link #authenticationManagerBean()} method can be used to expose the resulting
	 * {@link AuthenticationManager} as a Bean. The {@link #userDetailsServiceBean()} can
	 * be used to expose the last populated {@link UserDetailsService} that is created
	 * with the {@link AuthenticationManagerBuilder} as a Bean. The
	 * {@link UserDetailsService} will also automatically be populated on
	 * {@link HttpSecurity#getSharedObject(Class)} for use with other
	 * {@link SecurityContextConfigurer} (i.e. RememberMeConfigurer )
	 * </p>
	 *
	 * <p>
	 * For example, the following configuration could be used to register in memory
	 * authentication that exposes an in memory {@link UserDetailsService}:
	 * </p>
	 *
	 * <pre>
	 * &#064;Override
	 * protected void configure(AuthenticationManagerBuilder auth) {
	 * 	auth
	 * 	// enable in memory based authentication with a user named
	 * 	// &quot;user&quot; and &quot;admin&quot;
	 * 	.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;).and()
	 * 			.withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
	 * }
	 *
	 * // Expose the UserDetailsService as a Bean
	 * &#064;Bean
	 * &#064;Override
	 * public UserDetailsService userDetailsServiceBean() throws Exception {
	 * 	return super.userDetailsServiceBean();
	 * }
	 *
	 * </pre>
	 *
	 * @param auth the {@link AuthenticationManagerBuilder} to use
	 * @throws Exception
	 */
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		this.disableLocalConfigureAuthenticationBldr = true;
	}
	
	static class LazyPasswordEncoder implements PasswordEncoder {
		private ApplicationContext applicationContext;
		private PasswordEncoder passwordEncoder;

		LazyPasswordEncoder(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		@Override
		public String encode(CharSequence rawPassword) {
			return getPasswordEncoder().encode(rawPassword);
		}

		@Override
		public boolean matches(CharSequence rawPassword,
			String encodedPassword) {
			return getPasswordEncoder().matches(rawPassword, encodedPassword);
		}

		@Override
		public boolean upgradeEncoding(String encodedPassword) {
			return getPasswordEncoder().upgradeEncoding(encodedPassword);
		}

		private PasswordEncoder getPasswordEncoder() {
			if (this.passwordEncoder != null) {
				return this.passwordEncoder;
			}
			PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);
			if (passwordEncoder == null) {
				passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
			}
			this.passwordEncoder = passwordEncoder;
			return passwordEncoder;
		}

		private <T> T getBeanOrNull(Class<T> type) {
			try {
				return this.applicationContext.getBean(type);
			} catch(NoSuchBeanDefinitionException notFound) {
				return null;
			}
		}

		@Override
		public String toString() {
			return getPasswordEncoder().toString();
		}
	}

}
