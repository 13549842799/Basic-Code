package com.cyz.basic.config.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.exception.AuthenticationException;
import com.cyz.basic.config.security.exception.InsufficientAuthenticationException;
import com.cyz.basic.config.security.web.AuthenticationEntryPoint;
import com.cyz.basic.config.security.web.JsonResponseStrategy;
import com.cyz.basic.config.security.web.PortMapper;
import com.cyz.basic.config.security.web.PortMapperImpl;
import com.cyz.basic.config.security.web.PortResolver;
import com.cyz.basic.config.security.web.PortResolverImpl;
import com.cyz.basic.config.security.web.ResponseStrategy;
import com.cyz.basic.util.HttpUtil;
import com.cyz.basic.util.HttpUtil.RespParams;

/**
 * Used by the {@link ExceptionTranslationFilter} to commence a form login authentication
 * via the {@link UsernamePasswordAuthenticationFilter}.
 * <p>
 * Holds the location of the login form in the {@code loginFormUrl} property, and uses
 * that to build a redirect URL to the login page. Alternatively, an absolute URL can be
 * set in this property and that will be used exclusively.
 * <p>
 * When using a relative URL, you can set the {@code forceHttps} property to true, to
 * force the protocol used for the login form to be {@code HTTPS}, even if the original
 * intercepted request for a resource used the {@code HTTP} protocol. When this happens,
 * after a successful login (via HTTPS), the original resource will still be accessed as
 * HTTP, via the original request URL. For the forced HTTPS feature to work, the
 * {@link PortMapper} is consulted to determine the HTTP:HTTPS pairs. The value of
 * {@code forceHttps} will have no effect if an absolute URL is used.
 *
 *参考LoginUrlAuthenticationEntryPoint类
 *把跳转改为返回json
 *
 * @author Ben Alex
 * @author colin sampaleanu
 * @author Omri Spector
 * @author Luke Taylor
 * @since 3.0
 */
public class LoginUrlAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
	
	// ~ Static fields/initializers
	// =====================================================================================

	private static final Log logger = LogFactory
			.getLog(LoginUrlAuthenticationEntryPoint.class);

	// ~ Instance fields
	// ================================================================================================

	private PortMapper portMapper = new PortMapperImpl();

	private PortResolver portResolver = new PortResolverImpl();
	
	private boolean forceHttps = false;
	
	private ResponseStrategy strategy = new JsonResponseStrategy();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.info("判断登录问题类型,返回json");
		if (authException instanceof InsufficientAuthenticationException) {
			strategy.sendResponse(request, response, RespParams.create(request, response).ReLogin());
			logger.info("InsufficientAuthenticationException");
			return;
		}
		strategy.sendResponse(request, response, RespParams.create(request, response).fail(authException.getMessage()));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(portMapper, "portMapper must be specified");
		Assert.notNull(portResolver, "portResolver must be specified");
		
	}
	
	public void setPortMapper(PortMapper portMapper) {
		Assert.notNull(portMapper, "portMapper cannot be null");
		this.portMapper = portMapper;
	}

	protected PortMapper getPortMapper() {
		return portMapper;
	}

	public void setPortResolver(PortResolver portResolver) {
		Assert.notNull(portResolver, "portResolver cannot be null");
		this.portResolver = portResolver;
	}

	protected PortResolver getPortResolver() {
		return portResolver;
	}

}
