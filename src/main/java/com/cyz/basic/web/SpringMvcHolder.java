package com.cyz.basic.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * @author cyz
 *
 */
public class SpringMvcHolder {
	
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		Assert.notNull(servletRequestAttributes, "servletRequestAttributes can not be null");
		return servletRequestAttributes.getRequest();
	}
	
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		Assert.notNull(servletRequestAttributes, "servletRequestAttributes can not be null");
		return servletRequestAttributes.getResponse();
	}

}
