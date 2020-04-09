package com.cyz.basic.config.security.web.access;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cyz.basic.config.security.exception.AccessDeniedException;
import com.cyz.basic.config.security.web.JsonResponseStrategy;
import com.cyz.basic.config.security.web.ResponseStrategy;
import com.cyz.basic.util.HttpUtil;
import com.cyz.basic.util.HttpUtil.RespParams;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
	
	protected static final Log logger = LogFactory.getLog(AccessDeniedHandlerImpl.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (!response.isCommitted()) {
			HttpUtil.responseResult(RespParams.create(request, response).fail("权限不足"));
		}

	}

}
