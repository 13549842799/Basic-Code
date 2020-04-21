package com.cyz.basic.config.security.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.cyz.basic.util.HttpUtil;
import com.cyz.basic.util.HttpUtil.RespParams;

public class JsonResponseStrategy implements ResponseStrategy {

	@Override
	public void sendResponse(HttpServletRequest request, HttpServletResponse response, Object data) throws IOException {		
		if (data == null) {
			return;
		}
		System.out.println("进入这里");
		HttpUtil.responseResult((RespParams)data);
	}

	@Override
	public void successResponse(HttpServletRequest request, HttpServletResponse response, Object data)
			throws IOException {
		HttpUtil.responseResult(RespParams.create(request, response).success(data));
	}

	@Override
	public void failResponse(HttpServletRequest request, HttpServletResponse response, Object data) throws IOException {
		HttpUtil.responseResult(RespParams.create(request, response).error(data != null ? data.toString() : ""));
	}

	
}
