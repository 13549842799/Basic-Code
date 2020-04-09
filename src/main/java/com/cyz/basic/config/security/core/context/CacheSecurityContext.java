package com.cyz.basic.config.security.core.context;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cyz.basic.config.RedisConfig;
import com.cyz.basic.config.security.SecurityProperties;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.web.SpringMvcHolder;

public class CacheSecurityContext implements CyzSecurityContext {
	
	private final RedisTemplate<String, Object> redisTemplate;
	
	private SecurityProperties properties = new SecurityProperties();
	 
	public CacheSecurityContext(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
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
		String key = createAuthenticationKey();
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
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String createAuthenticationKey() {
		
		HttpServletRequest reqeust = SpringMvcHolder.getRequest();
		
		String token = reqeust.getHeader("x-token");
         
	    token = token != null ? token : "";
		
		return token;
	}

	public SecurityProperties getProperties() {
		return properties;
	}

	public void setProperties(SecurityProperties properties) {
		this.properties = properties;
	}
	
	

}
