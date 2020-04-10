package com.cyz.basic.config.security.web.authentication;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.exception.AuthenticationException;
import com.cyz.basic.config.security.web.AuthenticationEntryPoint;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

/**
 * An {@code AuthenticationEntryPoint} which selects a concrete
 * {@code AuthenticationEntryPoint} based on a {@link RequestMatcher} evaluation.
 *
 * <p>
 * A configuration might look like this:
 * </p>
 *
 * <pre>
 * &lt;bean id=&quot;daep&quot; class=&quot;org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint&quot;&gt;
 *     &lt;constructor-arg&gt;
 *         &lt;map&gt;
 *             &lt;entry key=&quot;hasIpAddress('192.168.1.0/24') and hasHeader('User-Agent','Mozilla')&quot; value-ref=&quot;firstAEP&quot; /&gt;
 *             &lt;entry key=&quot;hasHeader('User-Agent','MSIE')&quot; value-ref=&quot;secondAEP&quot; /&gt;
 *         &lt;/map&gt;
 *     &lt;/constructor-arg&gt;
 *     &lt;property name=&quot;defaultEntryPoint&quot; ref=&quot;defaultAEP&quot;/&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * This example uses the {@link RequestMatcherEditor} which creates a
 * {@link ELRequestMatcher} instances for the map keys.
 *
 * @author Mike Wiesner
 * @since 3.0.2
 */
public class DelegatingAuthenticationEntryPoint implements AuthenticationEntryPoint,
		InitializingBean {
	private final Log logger = LogFactory.getLog(getClass());

	private final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints;
	private AuthenticationEntryPoint defaultEntryPoint;

	public DelegatingAuthenticationEntryPoint(
			LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints) {
		this.entryPoints = entryPoints;
	}

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		for (RequestMatcher requestMatcher : entryPoints.keySet()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to match using " + requestMatcher);
			}
			if (requestMatcher.matches(request)) {
				AuthenticationEntryPoint entryPoint = entryPoints.get(requestMatcher);
				if (logger.isDebugEnabled()) {
					logger.debug("Match found! Executing " + entryPoint);
				}
				entryPoint.commence(request, response, authException);
				return;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("No match found. Using default entry point " + defaultEntryPoint);
		}

		// No EntryPoint matched, use defaultEntryPoint
		defaultEntryPoint.commence(request, response, authException);
	}

	/**
	 * EntryPoint which is used when no RequestMatcher returned true
	 */
	public void setDefaultEntryPoint(AuthenticationEntryPoint defaultEntryPoint) {
		this.defaultEntryPoint = defaultEntryPoint;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notEmpty(entryPoints, "entryPoints must be specified");
		Assert.notNull(defaultEntryPoint, "defaultEntryPoint must be specified");
	}
}
