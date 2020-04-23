package com.cyz.basic.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cyz.basic.util.HttpUtil;
import com.cyz.basic.util.HttpUtil.RespParams;

@Component
public class MyInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		/*String method = request.getMethod();
		String uri = request.getRequestURI();
		if (method.equals("OPTIONS") && uri.endsWith("/ouser/login")) {
			HttpUtil.responseResult(RespParams.create(request, response).success(Boolean.TRUE));
			return false;
		}
		*/
		return true;
	}

}
