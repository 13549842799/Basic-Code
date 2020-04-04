package com.cyz.basic.config.security.config.annotation.authentication.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.authentication.AuthenticationEventPublisher;
import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.crypto.factory.PasswordEncoderFactories;
import com.cyz.basic.config.security.crypto.password.PasswordEncoder;
import com.cyz.basic.config.security.exception.AuthenticationException;



/**
 * Exports the authentication {@link Configuration}
 *
 * @author Rob Winch
 * @since 3.2
 *
 */
@Configuration
@Import(ObjectPostProcessorConfiguration.class)
public class AuthenticationConfiguration {
	
	/**
	 * AtomicBoolean是java.util.concurrent.atomic的原子变量的类；这样的类具有原子性，在多线程的环境下使用是线程安全的；
	 * 这个类和普通的boolean的区别在于后者线程不安全，举例来说，当有多个线程要用到某个boolean变量的值，然后修改它，但存在多个线程同时修改的情况，
	 * 形成脏数据，所以此时就要用到线程安全的AtomicBoolean了。
	 */
	private AtomicBoolean buildingAuthenticationManager = new AtomicBoolean();
	
	private ApplicationContext applicationContext;
	
	private AuthenticationManager authenticationManager;

	private boolean authenticationManagerInitialized;
	
	private ObjectPostProcessor<Object> objectPostProcessor;
	
	private List<GlobalAuthenticationConfigurerAdapter> globalAuthConfigurers = Collections
			.emptyList();
	
	@Bean
	public AuthenticationManagerBuilder authenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor
			, ApplicationContext applicationContext) {
		LazyPasswordEncoder defaultPasswordEncoder = new LazyPasswordEncoder(applicationContext);
		AuthenticationEventPublisher authenticationEventPublisher = getBeanOrNull(applicationContext, AuthenticationEventPublisher.class);
		DefaultPasswordEncoderAuthenticationManagerBuilder result = new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, defaultPasswordEncoder);
		if (authenticationEventPublisher != null) {
			result.authenticationEventPublisher(authenticationEventPublisher);
		}
		return result;
	}
	
	@Bean
	public static GlobalAuthenticationConfigurerAdapter enableGlobalAuthenticationAutowiredConfigurer(
			ApplicationContext context) {
		return new EnableGlobalAuthenticationAutowiredConfigurer(context);
	}
	
	public AuthenticationManager getAuthenticationManager() throws Exception {
		if (authenticationManagerInitialized) {
			return authenticationManager;
		}
		AuthenticationManagerBuilder authBuilder = authenticationManagerBuilder(objectPostProcessor, applicationContext);
		if (this.buildingAuthenticationManager.getAndSet(true)) {
			return new AuthenticationManagerDelegator(authBuilder);
		}
		
		for (GlobalAuthenticationConfigurerAdapter config : globalAuthConfigurers) {
			authBuilder.apply(config);
		}
		
		authenticationManager = authBuilder.build();
		
		if (authenticationManager == null) {
			authenticationManager = getAuthenticationManagerBean();
		}
		
		this.authenticationManagerInitialized = true;
		
		return authenticationManager;
	}
	
	@Autowired(required = false)
	public void setGlobalAuthenticationConfigurers(
			List<GlobalAuthenticationConfigurerAdapter> configurers) throws Exception {
		Collections.sort(configurers, AnnotationAwareOrderComparator.INSTANCE);
		this.globalAuthConfigurers = configurers;
	}

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Autowired
	public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
		this.objectPostProcessor = objectPostProcessor;
	}

	
	@SuppressWarnings("unchecked")
	private <T> T lazyBean(Class<T> interfaceName) {
		LazyInitTargetSource lazyTargetSource = new LazyInitTargetSource();
		String[] beanNamesForType = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				applicationContext, interfaceName);
		if (beanNamesForType.length == 0) {
			return null;
		}
		String beanName;
		if (beanNamesForType.length > 1) {
			List<String> primaryBeanNames = Arrays.stream(beanNamesForType)
				.filter(i -> applicationContext instanceof ConfigurableApplicationContext)
				.filter(n -> ((ConfigurableApplicationContext) applicationContext).getBeanFactory().getBeanDefinition(n).isPrimary())
				.collect(Collectors.toList());

			Assert.isTrue(primaryBeanNames.size() != 0, () -> "Found " + beanNamesForType.length
					+ " beans for type " + interfaceName + ", but none marked as primary");
			Assert.isTrue(primaryBeanNames.size() == 1, () -> "Found " + primaryBeanNames.size()
					+ " beans for type " + interfaceName + " marked as primary");
			beanName = primaryBeanNames.get(0);
		} else {
			beanName = beanNamesForType[0];
		}

		lazyTargetSource.setTargetBeanName(beanName);
		lazyTargetSource.setBeanFactory(applicationContext);
		ProxyFactoryBean proxyFactory = new ProxyFactoryBean();
		proxyFactory = objectPostProcessor.postProcess(proxyFactory);
		proxyFactory.setTargetSource(lazyTargetSource);
		return (T) proxyFactory.getObject();
	}
	
	private AuthenticationManager getAuthenticationManagerBean() {
		return lazyBean(AuthenticationManager.class);
	}
	
	
	private static <T> T getBeanOrNull(ApplicationContext applicationContext, Class<T> type) {
		try {
			return applicationContext.getBean(type);
		} catch(NoSuchBeanDefinitionException notFound) {
			return null;
		}
	}
	
	private static class EnableGlobalAuthenticationAutowiredConfigurer extends GlobalAuthenticationConfigurerAdapter {
	    private final ApplicationContext context;
	    private static final Log logger = LogFactory.getLog(EnableGlobalAuthenticationAutowiredConfigurer.class);
	
	    public EnableGlobalAuthenticationAutowiredConfigurer(ApplicationContext context) {
		    this.context = context;
	    }
	
	    @Override
	    public void init(AuthenticationManagerBuilder auth) {
			Map<String, Object> beansWithAnnotation = context
					.getBeansWithAnnotation(EnableGlobalAuthentication.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Eagerly initializing " + beansWithAnnotation);
			}
	    }
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
		public JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> jdbcAuthentication()
			throws Exception {
			return super.jdbcAuthentication()
				.passwordEncoder(this.defaultPasswordEncoder);
		}*/

		/*@Override
		public <T extends UserDetailsService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> userDetailsService(
			T userDetailsService) throws Exception {
			return super.userDetailsService(userDetailsService)
				.passwordEncoder(this.defaultPasswordEncoder);
		}*/
	}
	
	/**
	 * Prevents infinite recursion in the event that initializing the
	 * AuthenticationManager.
	 *
	 * @author Rob Winch
	 * @since 4.1.1
	 */
	static final class AuthenticationManagerDelegator implements AuthenticationManager {
		private AuthenticationManagerBuilder delegateBuilder;
		private AuthenticationManager delegate;
		private final Object delegateMonitor = new Object();

		AuthenticationManagerDelegator(AuthenticationManagerBuilder delegateBuilder) {
			Assert.notNull(delegateBuilder, "delegateBuilder cannot be null");
			this.delegateBuilder = delegateBuilder;
		}

		@Override
		public Authentication authenticate(Authentication authentication)
				throws AuthenticationException {
			if (this.delegate != null) {
				return this.delegate.authenticate(authentication);
			}

			synchronized (this.delegateMonitor) {
				if (this.delegate == null) {
					this.delegate = this.delegateBuilder.getObject();
					this.delegateBuilder = null;
				}
			}

			return this.delegate.authenticate(authentication);
		}

		@Override
		public String toString() {
			return "AuthenticationManagerDelegator [delegate=" + this.delegate + "]";
		}
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
			PasswordEncoder passwordEncoder = getBeanOrNull(this.applicationContext, PasswordEncoder.class);
			if (passwordEncoder == null) {
				passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
			}
			this.passwordEncoder = passwordEncoder;
			return passwordEncoder;
		}

		@Override
		public String toString() {
			return getPasswordEncoder().toString();
		}
	}

}
