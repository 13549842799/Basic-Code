package com.cyz.basic.config.security.web.util.matcher;

import javax.servlet.http.HttpServletRequest;

public final class AnyRequestMatcher implements RequestMatcher {

	public static final RequestMatcher INSTANCE = new AnyRequestMatcher();

	public boolean matches(HttpServletRequest request) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean equals(Object obj) {
		return obj instanceof AnyRequestMatcher
				|| obj instanceof org.springframework.security.web.util.matcher.AnyRequestMatcher;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	private AnyRequestMatcher() {
	}
}
