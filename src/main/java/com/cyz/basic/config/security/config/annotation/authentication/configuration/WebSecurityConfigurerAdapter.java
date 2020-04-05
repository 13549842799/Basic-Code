package com.cyz.basic.config.security.config.annotation.authentication.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import com.cyz.basic.config.security.access.intercept.FilterSecurityInterceptor;
import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import com.cyz.basic.config.security.config.annotation.web.WebSecurityConfigurer;
import com.cyz.basic.config.security.config.annotation.web.builders.HttpSecurity;
import com.cyz.basic.config.security.config.annotation.web.builders.WebSecurity;


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
		
		/*DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
				.postProcess(new DefaultAuthenticationEventPublisher());
		localConfigureAuthenticationBldr.authenticationEventPublisher(eventPublisher);*/
		
		http = new HttpSecurity();
		
		return http;
	}
	
	/**
	 * Override this method to configure {@link WebSecurity}. For example, if you wish to
	 * ignore certain requests.
	 */
	public void configure(WebSecurity web) throws Exception {
	}
	
	public void init(final WebSecurity web) throws Exception {
		final HttpSecurity http = getHttp();
		/*web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
			public void run() {
				FilterSecurityInterceptor securityInterceptor = http
						.getSharedObject(FilterSecurityInterceptor.class);
				web.securityInterceptor(securityInterceptor);
			}
		});*/
	}

}
