package com.cyz.basic.config.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.security.SecurityPrerequisite;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;

/**
 * 重写后的secury框架所需的参数
 * @author cyz
 *
 */
@ConfigurationProperties(prefix = "own.security")
public class SecurityProperties implements SecurityPrerequisite {

	private String loginPage;
	
	private String loginUrl;
	
	private String logoutUrl;
	
	private Long expireTime = -1l; //毫秒
	
	/**
	 * Order applied to the WebSecurityConfigurerAdapter that is used to configure basic
	 * authentication for application endpoints. If you want to add your own
	 * authentication for all or some of those endpoints the best thing to do is to add
	 * your own WebSecurityConfigurerAdapter with lower order.
	 */
	public static final int BASIC_AUTH_ORDER = Ordered.LOWEST_PRECEDENCE - 5;

	/**
	 * Order applied to the WebSecurityConfigurer that ignores standard static resource
	 * paths.
	 */
	public static final int IGNORED_ORDER = Ordered.HIGHEST_PRECEDENCE;

	/**
	 * Default order of Spring Security's Filter in the servlet container (i.e. amongst
	 * other filters registered with the container). There is no connection between this
	 * and the {@code @Order} on a WebSecurityConfigurer.
	 */
	public static final int DEFAULT_FILTER_ORDER = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER
			- 100;

	private final Filter filter = new Filter();
	
	public Filter getFilter() {
		return this.filter;
	}

	public static class Filter {

		/**
		 * Security filter chain order.
		 */
		private int order = DEFAULT_FILTER_ORDER;

		/**
		 * Security filter chain dispatcher types.
		 */
		private Set<DispatcherType> dispatcherTypes = new HashSet<>(Arrays.asList(
				DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.REQUEST));

		public int getOrder() {
			return this.order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		public Set<DispatcherType> getDispatcherTypes() {
			return this.dispatcherTypes;
		}

		public void setDispatcherTypes(Set<DispatcherType> dispatcherTypes) {
			this.dispatcherTypes = dispatcherTypes;
		}

	}
	

	public String getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}
	
	
}
