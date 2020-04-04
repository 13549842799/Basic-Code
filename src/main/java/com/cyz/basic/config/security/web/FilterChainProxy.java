package com.cyz.basic.config.security.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.cyz.basic.config.SpringContextHolder;
import com.cyz.basic.config.security.WebSecurityConfig;
import com.cyz.basic.config.security.web.firewall.HttpFirewall;
import com.cyz.basic.config.security.web.firewall.StrictHttpFirewall;

@WebFilter(urlPatterns="/*")
//@Component
@DependsOn("springContextHolder") //表示在springContextHolder（bean名）加载后再加载当前类
public class FilterChainProxy implements Filter, InitializingBean{
	
	private static final Log logger = LogFactory.getLog(FilterChainProxy.class);
	
	private List<SecurityFilterChain> filterChains;
	
	private List<Filter> filters = new ArrayList<>();
	
	private HttpFirewall firewall = new StrictHttpFirewall();
	
	
	@Override
	public void destroy() {
		
	}
	
	public FilterChainProxy() {
		System.out.println("初始化cyzfilter");
	}
	
	public FilterChainProxy(List<SecurityFilterChain> securityFilterChains) {
		this.filterChains = securityFilterChains;
	}
	
	public FilterChainProxy(List<Filter> filters, Object obj) {
		this.filters = filters;
	}
 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (filters == null || filters.size() == 0) {
			logger.debug("has no addition filter");
			chain.doFilter(request, response);
		}
		VirtualFilterChain virt = new VirtualFilterChain(chain, filters);
		virt.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebSecurityConfig webConfig = SpringContextHolder.getBean(WebSecurityConfig.class);
		filters.addAll(webConfig.addFilters());
	}
	
	
	
	public void setFirewall(HttpFirewall firewall) {
		this.firewall = firewall;
	}



	public List<SecurityFilterChain> getFilterChains() {
		return filterChains;
	}

	public void setFilterChains(List<SecurityFilterChain> filterChains) {
		this.filterChains = filterChains;
	}



	private static class VirtualFilterChain implements FilterChain {
		private final FilterChain originalChain;
		private final List<Filter> additionalFilters;
		private final int size;
		private int currentPosition = 0;

		private VirtualFilterChain(
				FilterChain chain, List<Filter> additionalFilters) {
			this.originalChain = chain;
			this.additionalFilters = additionalFilters;
			this.size = additionalFilters.size();
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response)
				throws IOException, ServletException {
			if (currentPosition == size) {
				if (logger.isDebugEnabled()) {
					
				}
				originalChain.doFilter(request, response);
			}
			else {
				currentPosition++;

				Filter nextFilter = additionalFilters.get(currentPosition - 1);
				if (logger.isDebugEnabled()) {
					
				}
				nextFilter.doFilter(request, response, this);
			}
		}
	}

}
