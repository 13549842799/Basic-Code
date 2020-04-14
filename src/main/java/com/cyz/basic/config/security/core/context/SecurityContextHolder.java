package com.cyz.basic.config.security.core.context;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.SecurityProperties;

public class SecurityContextHolder {
	
	private static SecurityContext context = null;
	
	public static SecurityContext getContext() {
		return context;
	}
	
	@SuppressWarnings("unchecked")
	public static void inintHolder(Object redisTemplate, SecurityProperties properties) {
		synchronized (SecurityContextHolder.class) {
			Assert.isNull(context, "context had init");
			if (!redisTemplate.getClass().isAssignableFrom(RedisTemplate.class)) {
				throw new IllegalArgumentException("CyzSecurityContextHolder init error");
			}
			context = new CacheSecurityContext((RedisTemplate<String, Object>)redisTemplate, properties);
		}
	}

} 
