package com.cyz.basic.config.security.core.context;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.cyz.basic.config.security.SecurityProperties;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.constant.EntityConstants;
import com.cyz.basic.util.HttpUtil;
import com.cyz.basic.web.SpringMvcHolder;

public class CacheSecurityContext implements SecurityContext {
	
	private final RedisTemplate<String, Object> redisTemplate;
	
	private SecurityProperties properties = new SecurityProperties();
	 
	public CacheSecurityContext(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	public CacheSecurityContext(RedisTemplate<String, Object> redisTemplate, SecurityProperties properties) {
		this.redisTemplate = redisTemplate;
		this.properties = properties;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9009356224286723022L;

	@Override
	public Authentication getAuthentication() {
		Object obj = redisTemplate.opsForValue().get(createAuthenticationKey());
		if (obj != null) {
			return (Authentication)obj;
		}
		return null;
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		if (authentication == null) {
			throw new IllegalArgumentException("authentication can be null when save it");
		}
		String key = createAuthenticationKey(authentication.getPrincipal());
		if (redisTemplate.hasKey(key)) {
			redisTemplate.opsForValue().set(key, authentication);
			return;
		}
		redisTemplate.opsForValue().set(key, authentication, properties.getExpireTime(), TimeUnit.SECONDS);
	}
	
	public void clearAuthentication() {
		String key = createAuthenticationKey();
		if (redisTemplate.hasKey(key)) {
			redisTemplate.delete(key);
			System.out.println("移除authentication");
		}
	}
	
    private String createAuthenticationKey() {	
		return createAuthenticationKey(null);
	}
	
	/**
	 * 
	 * @return
	 */
	private String createAuthenticationKey(Object username) {
		
		HttpServletRequest request = SpringMvcHolder.getRequest();
		String key = request.getHeader("x-user");
		if (username != null && !username.equals(EntityConstants.ANONYMOUS)) { //如果存在用户名并且用户名不是属于匿名用户的
			key = String.valueOf(username);
		}
		key = !StringUtils.isEmpty(key) ? EntityConstants.tokenKey(HttpUtil.isPhoneLogin(request) ? EntityConstants.ORIGIN_PHONE : EntityConstants.ORIGIN_COMP, key) 
				: "session_" + request.getRequestedSessionId();	
		return key;
	}

	public SecurityProperties getProperties() {
		return properties;
	}

	public void setProperties(SecurityProperties properties) {
		this.properties = properties;
	}
	
	

}
