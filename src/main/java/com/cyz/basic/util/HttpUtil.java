package com.cyz.basic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.cyz.basic.pojo.ResponseResult;

/**
 * 网络请求工具
 * @author cyz
 *
 */
public abstract class HttpUtil {
	
	private static final String JSON_TYPE = "application/json;charset=UTF-8";

	private static final String TEXT_TYPE = "text/plain";
	
	public static Map<String, Object> request(String url, Map<String, String> params, Map<String, String> headers, String type, String responseType) {
		switch (type) {
		case "GET":			
			return doGet(url, params, headers, responseType);
		case "POST":
			return doPost(url, params, headers, responseType);
		default:
			break;
		}
		return null;
	}
	
    private static Map<String, Object> doGet(String httpUrl, Map<String, String> params, Map<String, String> headers, String responseType) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		int code = 400;
		httpUrl += params != null && params.size() > 0 ? "?" + urlParams(params) : "";		
		
		HttpURLConnection connection = null;
	    try {
	        // 创建远程url连接对象
	        URL url = new URL(httpUrl);
	        // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
	        connection = (HttpURLConnection) url.openConnection();
	        // 设置连接方式：get
	        connection.setRequestMethod("GET");
	        // 设置连接主机服务器的超时时间：15000毫秒
	        connection.setConnectTimeout(15000);
	        // 设置读取远程返回的数据时间：60000毫秒
	        connection.setReadTimeout(60000);
	        
	        // 设置头信息
	        if (headers != null && headers.size() > 0) {
	        	for (Map.Entry<String, String> h : headers.entrySet()) {
	        		connection.setRequestProperty(h.getKey(), h.getValue());
				}
	        }
	        // 发送请求
	        connection.connect();
	        // 通过connection连接，获取输入流
	        if ((code = connection.getResponseCode()) == 200) {     
	            Map<String, Object> res = dealResult(responseType, acceptStrResult(connection));
	            result.put(String.valueOf(code), res);
	        } else {
	        	result.put(String.valueOf(code), connection.getResponseMessage());
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        result.put(String.valueOf(code), e.getMessage());
	    } finally {
	        connection.disconnect();// 关闭远程连接
	    }
	    if (result != null) {
	    	result.put("url", httpUrl);
	    }
	    return result;
	}
    
    private static String urlParams(Map<String, String> params) {
	    StringBuilder sb = new StringBuilder();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			try {
				sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "GBK")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (params.size() > 1) {
		    sb.delete(sb.length()-1, sb.length());
		}
		return sb.toString();
	}
    
    private static String acceptStrResult(HttpURLConnection connection) {
		String message = null;
		// 封装输入流is，并指定字符集
		try (InputStream is = connection.getInputStream();BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			StringBuffer sbf = new StringBuffer();
	        String temp = null;
	        while ((temp = br.readLine()) != null) {
	            sbf.append(temp);
	            sbf.append("\r\n");
	        }
	        message = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return message;
	}
    
    private static Map<String, Object> dealResult(String responseType, String resultMessage) {
		Map<String, Object> result = new HashMap<String, Object>();
		switch (responseType) {
		case "String":
			result.put("data", resultMessage);
			break; 
		case "xml":
			/*try {
				result.put("data", dealXmlResult(resultMessage));
			} catch (DocumentException e) {
				e.printStackTrace();
			}*/
		default:
			break;
		}
		return result;
	}
    
    private static Map<String, Object> doPost(String httpUrl, Map<String, String> params, Map<String, String> headers,  String responseType) {
    	Map<String, Object> result = new HashMap<String, Object>();
		int code = 400;
		
		HttpURLConnection connection = null;
	    try {
	        // 创建远程url连接对象
	        URL url = new URL(httpUrl);
	        // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
	        connection = (HttpURLConnection) url.openConnection();
	        // 设置连接方式：get
	        connection.setRequestMethod("POST");
	        // 设置连接主机服务器的超时时间：15000毫秒
	        connection.setConnectTimeout(15000);
	        // 设置读取远程返回的数据时间：60000毫秒
	        connection.setReadTimeout(60000);
	        connection.setDoOutput(true);
	        connection.setDoInput(true);
	        connection.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
	        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
	        connection.setRequestProperty("accept","application/json");
	        // 设置头信息
	        if (headers != null && headers.size() > 0) {
	        	for (Map.Entry<String, String> h : headers.entrySet()) {
	        		connection.setRequestProperty(h.getKey(), h.getValue());
				}
	        }
	        // 发送请求
	        connection.connect();
	        //发送参数
	        sendData(connection, params);
	        // 通过connection连接，获取输入流
	        if ((code = connection.getResponseCode()) == 200) {     
	            Map<String, Object> res = dealResult(responseType, acceptStrResult(connection));
	            result.put(String.valueOf(code), res);
	        } else {
	        	result.put(String.valueOf(code), connection.getResponseMessage());
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        result.put(String.valueOf(code), e.getMessage());
	    } finally {
	        connection.disconnect();// 关闭远程连接
	    }
	    if (result != null) {
	    	result.put("url", httpUrl);
	    }
	    return result;
    }
    
    private static void sendData(HttpURLConnection connection, Map<String, String> params) {
    	if (params != null && params.size() > 0) {
    		String jsonStr = JSONObject.toJSONString(params);
            byte[] writebytes = jsonStr.getBytes();
            // 设置文件长度
            try(OutputStream outwritestream = connection.getOutputStream()) {
            	connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                outwritestream.write(writebytes);
                outwritestream.flush();
                outwritestream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * 
     * @param params
     * @throws IOException 
     */
    public static void responseResult(RespParams params) throws IOException {
    	Assert.notNull(params, "params is null");
    	
    	HttpServletResponse resp = params.getResp();
    	
    	resp.setContentType(params.getContentType());
    	
    	if (params.hasExtraHeaders()) {
    		for (String header : params.getExtraHeaders()) {
    			String[] hs = header.split("=");
    			if (hs.length != 2) {
    				continue;
    			}
				resp.addHeader(hs[0], hs[1]);
			}
    	}
    	
    	ResponseResult<Object> result = new ResponseResult<>();
    	result.setStatus(params.getCode());
    	result.setMessage(params.getErrorMessage());
    	result.setData(params.getContent());
    
		resp.getWriter().write(JSONObject.toJSONString(result));
		resp.getWriter().flush();

    }
    
    public static class RespParams {
    	
    	private final HttpServletRequest req;
    	private final HttpServletResponse resp;
    	private String contentType = JSON_TYPE;
    	private Object content;
    	private String[] extraHeaders;
    	private int code;
    	private String errorMessage;
    	
    	public RespParams (HttpServletRequest req, HttpServletResponse resp) {
    		Assert.notNull(req, "request is null");
    		this.req = req;
    		Assert.notNull(resp, "response is null");
    		this.resp = resp;
    	}
    	
    	public static RespParams create(HttpServletRequest req, HttpServletResponse resp) {
    		return new RespParams(req, resp);
    	}
    	
    	public RespParams contentType(String contentType) {
    		this.contentType = contentType;
    		return this;
    	}
    	
    	public RespParams content(Object content) {
    		this.content = content;
    		return this;
    	}
    	
    	public RespParams extraHeaders(String... extraHeaders) {
    		this.extraHeaders = extraHeaders;
    		return this;
    	}
    	
    	public RespParams success(Object content) {
    		this.code = ResponseResult.RESPONSE_SUCCESS;
    		this.content = content;
    		return this;
    	}
    	
    	public RespParams error(String message) {
    		this.code = ResponseResult.RESPONSE_ERROR;
    		this.errorMessage = message;
    		return this;
    	}
    	
    	public RespParams errorMessage(String errorMessage) {
    		this.errorMessage = errorMessage;
    		return this;
    	}

		public HttpServletRequest getReq() {
			return req;
		}

		public HttpServletResponse getResp() {
			return resp;
		}

		public String getContentType() {
			return contentType;
		}

		public Object getContent() {
			return content;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getErrorMessage() {
			return errorMessage;
		}

		public String[] getExtraHeaders() {
			return extraHeaders;
		}
    	
		public boolean hasExtraHeaders() {
			return extraHeaders != null && extraHeaders.length > 0;
		}
    	
    }
}
