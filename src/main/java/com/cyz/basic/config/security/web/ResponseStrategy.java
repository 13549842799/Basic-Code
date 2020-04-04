package com.cyz.basic.config.security.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口的相应策略
 * @author cyz
 *
 */
public interface ResponseStrategy {
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param data
	 * @throws IOException
	 */
	void sendResponse(HttpServletRequest request, HttpServletResponse response, Object data) throws IOException;

}
