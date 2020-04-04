package com.cyz.basic.config.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.web.JsonResponseStrategy;
import com.cyz.basic.config.security.web.ResponseStrategy;

public abstract class AbstractAuthenticationRestfulHandler {
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	private ResponseStrategy responseStrategy = new JsonResponseStrategy();
	
	public AbstractAuthenticationRestfulHandler() {}
	
	protected void handle(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {		
		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to send response");
			return;
		}
		responseStrategy.sendResponse(request, response, authentication);
	}

	public void setResponseStrategy(ResponseStrategy responseStrategy) {
		Assert.notNull(responseStrategy, "response Strategy is null");
		this.responseStrategy = responseStrategy;
	}


}
