package com.cyz.basic.config.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cyz.basic.config.security.authentication.AbstractAuthenticationRestfulHandler;
import com.cyz.basic.config.security.exception.AuthenticationException;
//import com.cyz.basic.config.security.exception.AuthenticationServiceException;
import com.cyz.basic.config.security.exception.BadCredentialsException;
import com.cyz.basic.config.security.exception.UsernameNotFoundException;
import com.cyz.basic.util.HttpUtil.RespParams;

/**
 * <tt>AuthenticationFailureHandler</tt> which performs a redirect to the value of the
 * {@link #setDefaultFailureUrl defaultFailureUrl} property when the
 * <tt>onAuthenticationFailure</tt> method is called. If the property has not been set it
 * will send a 401 response to the client, with the error message from the
 * <tt>AuthenticationException</tt> which caused the failure.
 * <p>
 * If the {@code useForward} property is set, a {@code RequestDispatcher.forward} call
 * will be made to the destination instead of a redirect.
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class SimpleUrlAuthenticationFailureHandler extends AbstractAuthenticationRestfulHandler implements AuthenticationFailureHandler {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	public SimpleUrlAuthenticationFailureHandler() {
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp,
			AuthenticationException exception) throws IOException, ServletException {
		
		if (exception instanceof UsernameNotFoundException) {
			handle(req, resp, RespParams.create(req, resp).UsernameError());
			return;
		}
		
		if (exception instanceof BadCredentialsException) {
			handle(req, resp, RespParams.create(req, resp).PasswordError());
			return;
		}
		
		/*if (exception instanceof AuthenticationServiceException) {
			handle(req, resp, RespParams.create(req, resp).fail(exception.getMessage()));
			return;
		}*/
		
		handle(req, resp, RespParams.create(req, resp).fail(exception.getMessage()));
	}

}
