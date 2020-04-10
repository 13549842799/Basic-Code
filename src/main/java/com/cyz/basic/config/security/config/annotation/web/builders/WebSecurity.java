package com.cyz.basic.config.security.config.annotation.web.builders;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.cyz.basic.config.security.config.annotation.AbstractConfiguredSecurityBuilder;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.SecurityBuilder;
import com.cyz.basic.config.security.web.DefaultSecurityFilterChain;
import com.cyz.basic.config.security.web.FilterChainProxy;
import com.cyz.basic.config.security.web.SecurityFilterChain;
import com.cyz.basic.config.security.web.access.intercept.FilterSecurityInterceptor;
import com.cyz.basic.config.security.web.debug.DebugFilter;
import com.cyz.basic.config.security.web.firewall.HttpFirewall;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

/**
 * <p>
 * The {@link WebSecurity} is created by {@link WebSecurityConfiguration} to create the
 * {@link FilterChainProxy} known as the Spring Security Filter Chain
 * (springSecurityFilterChain). The springSecurityFilterChain is the {@link Filter} that
 * the {@link DelegatingFilterProxy} delegates to.
 * </p>
 *
 * <p>
 * Customizations to the {@link WebSecurity} can be made by creating a
 * {@link WebSecurityConfigurer} or more likely by overriding
 * {@link WebSecurityConfigurerAdapter}.
 * </p>
 *
 * @see EnableWebSecurity
 * @see WebSecurityConfiguration
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class WebSecurity extends
    AbstractConfiguredSecurityBuilder<Filter, WebSecurity> implements SecurityBuilder<Filter>, ApplicationContextAware {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private final List<RequestMatcher> ignoredRequests = new ArrayList<>();
	
	private final List<SecurityBuilder<? extends SecurityFilterChain>> securityFilterChainBuilders = new ArrayList<SecurityBuilder<? extends SecurityFilterChain>>();
	
	private FilterSecurityInterceptor filterSecurityInterceptor;
	
	private boolean debugEnabled;
	
	private HttpFirewall httpFirewall;
	
	/**
	 * 当build创建后执行线程
	 */
	private Runnable postBuildAction = new Runnable() {
		public void run() {
		}
	};
	
	/**
	 * Creates a new instance
	 * @param objectPostProcessor the {@link ObjectPostProcessor} to use
	 * @see WebSecurityConfiguration
	 */
	public WebSecurity(ObjectPostProcessor<Object> objectPostProcessor) {
		super(objectPostProcessor);
	}
	
	/**
	 * Allows customizing the {@link HttpFirewall}. The default is
	 * {@link StrictHttpFirewall}.
	 *
	 * @param httpFirewall the custom {@link HttpFirewall}
	 * @return the {@link WebSecurity} for further customizations
	 */
	public WebSecurity httpFirewall(HttpFirewall httpFirewall) {
		this.httpFirewall = httpFirewall;
		return this;
	}

	public WebSecurity debug(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
	}
	
	/**
	 * Executes the Runnable immediately after the build takes place
	 *
	 * @param postBuildAction
	 * @return the {@link WebSecurity} for further customizations
	 */
	public WebSecurity postBuildAction(Runnable postBuildAction) {
		this.postBuildAction = postBuildAction;
		return this;
	}

	@Override
	protected Filter performBuild() throws Exception {
		Assert.state(
				!securityFilterChainBuilders.isEmpty(),
				"At least one SecurityBuilder<? extends SecurityFilterChain> needs to be specified. Typically this done by adding a @Configuration that extends WebSecurityConfigurerAdapter. More advanced users can invoke "
						+ WebSecurity.class.getSimpleName()
						+ ".addSecurityFilterChainBuilder directly");
		int chainSize = ignoredRequests.size() + securityFilterChainBuilders.size();
		System.out.println("chainSize:" + chainSize);
		List<SecurityFilterChain> securityFilterChains = new ArrayList<>(
				chainSize);
		for (RequestMatcher ignoredRequest : ignoredRequests) {
			securityFilterChains.add(new DefaultSecurityFilterChain(ignoredRequest));
		}
		for (SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder : securityFilterChainBuilders) {
			logger.info("create build:" + securityFilterChainBuilder.getClass().getName());
			securityFilterChains.add(securityFilterChainBuilder.build());
		}
		System.out.println(securityFilterChains.get(0).getFilters().size());
		System.out.println("chains:" + securityFilterChains.size());
		FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
		if (httpFirewall != null) {
			filterChainProxy.setFirewall(httpFirewall);
		}		
		filterChainProxy.afterPropertiesSet();
		
		Filter result = filterChainProxy;
		if (debugEnabled) {
			logger.warn("\n\n"
					+ "********************************************************************\n"
					+ "**********        Security debugging is enabled.       *************\n"
					+ "**********    This may include sensitive information.  *************\n"
					+ "**********      Do not use in a production system!     *************\n"
					+ "********************************************************************\n\n");
			result = new DebugFilter(filterChainProxy);
		}
		
		postBuildAction.run();
		
		return result;
	}
	
	/**
	 * <p>
	 * Adds builders to create {@link SecurityFilterChain} instances.
	 * </p>
	 *
	 * <p>
	 * Typically this method is invoked automatically within the framework from
	 * {@link WebSecurityConfigurerAdapter#init(WebSecurity)}
	 * </p>
	 *
	 * @param securityFilterChainBuilder the builder to use to create the
	 * {@link SecurityFilterChain} instances
	 * @return the {@link WebSecurity} for further customizations
	 */
	public WebSecurity addSecurityFilterChainBuilder(
			SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder) {
		System.out.println("addFilterBuilder");
		this.securityFilterChainBuilders.add(securityFilterChainBuilder);
		return this;
	}
	
	/**
	 * Sets the {@link FilterSecurityInterceptor}. This is typically invoked by
	 * {@link WebSecurityConfigurerAdapter}.
	 * @param securityInterceptor the {@link FilterSecurityInterceptor} to use
	 * @return the {@link WebSecurity} for further customizations
	 */
	public WebSecurity securityInterceptor(FilterSecurityInterceptor securityInterceptor) {
		this.filterSecurityInterceptor = securityInterceptor;
		return this;
	}

}
