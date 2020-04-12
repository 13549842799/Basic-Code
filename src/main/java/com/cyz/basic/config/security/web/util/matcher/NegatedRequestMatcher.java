package com.cyz.basic.config.security.web.util.matcher;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * A {@link RequestMatcher} that will negate the {@link RequestMatcher} passed in. For
 * example, if the {@link RequestMatcher} passed in returns true,
 * {@link NegatedRequestMatcher} will return false. If the {@link RequestMatcher} passed
 * in returns false, {@link NegatedRequestMatcher} will return true.
 *
 * @author Rob Winch
 * @since 3.2
 */
public class NegatedRequestMatcher implements RequestMatcher {
	private final Log logger = LogFactory.getLog(getClass());

	private final RequestMatcher requestMatcher;

	/**
	 * Creates a new instance
	 * @param requestMatcher the {@link RequestMatcher} that will be negated.
	 */
	public NegatedRequestMatcher(RequestMatcher requestMatcher) {
		Assert.notNull(requestMatcher, "requestMatcher cannot be null");
		this.requestMatcher = requestMatcher;
	}

	public boolean matches(HttpServletRequest request) {
		boolean result = !requestMatcher.matches(request);
		if (logger.isDebugEnabled()) {
			logger.debug("matches = " + result);
		}
		return result;
	}

	@Override
	public String toString() {
		return "NegatedRequestMatcher [requestMatcher=" + requestMatcher + "]";
	}
}
