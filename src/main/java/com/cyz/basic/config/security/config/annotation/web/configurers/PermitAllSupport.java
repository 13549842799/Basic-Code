package com.cyz.basic.config.security.config.annotation.web.configurers;

import javax.servlet.http.HttpServletRequest;

import com.cyz.basic.config.security.access.SecurityConfig;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;
import com.cyz.basic.config.security.config.annotation.web.configurers.AbstractConfigAttributeRequestMatcherRegistry.UrlMapping;


public final class PermitAllSupport {
	public static void permitAll(
			HttpSecurityBuilder<? extends HttpSecurityBuilder<?>> http, String... urls) {
		for (String url : urls) {
			if (url != null) {
				permitAll(http, new ExactUrlRequestMatcher(url));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void permitAll(
			HttpSecurityBuilder<? extends HttpSecurityBuilder<?>> http,
			RequestMatcher... requestMatchers) {
		ExpressionUrlAuthorizationConfigurer<?> configurer = http
				.getConfigurer(ExpressionUrlAuthorizationConfigurer.class);

		if (configurer == null) {
			throw new IllegalStateException(
					"permitAll only works with HttpSecurity.authorizeRequests()");
		}
        
		for (RequestMatcher matcher : requestMatchers) {
			if (matcher != null) {
				configurer
						.getRegistry().addMapping(0, new UrlMapping(matcher,SecurityConfig.createList(ExpressionUrlAuthorizationConfigurer.permitAll)));
			}
		}
	}

	private final static class ExactUrlRequestMatcher implements RequestMatcher {
		private String processUrl;

		private ExactUrlRequestMatcher(String processUrl) {
			this.processUrl = processUrl;
		}

		public boolean matches(HttpServletRequest request) {
			String uri = request.getRequestURI();
			String query = request.getQueryString();

			if (query != null) {
				uri += "?" + query;
			}

			if ("".equals(request.getContextPath())) {
				return uri.equals(processUrl);
			}

			return uri.equals(request.getContextPath() + processUrl);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ExactUrl [processUrl='").append(processUrl).append("']");
			return sb.toString();
		}
	}

	private PermitAllSupport() {
	}
}
