package com.cyz.basic.config.security.core.context;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

public class CyzSecurityContextHolder {
	
	private static CyzSecurityContext context = null;
	
	public static CyzSecurityContext getContext() {
		return context;
	}
	
	@SuppressWarnings("unchecked")
	public static void inintHolder(Object redisTemplate) {
		synchronized (CyzSecurityContextHolder.class) {
			Assert.isNull(context, "context had init");
			if (!redisTemplate.getClass().isAssignableFrom(RedisTemplate.class)) {
				throw new IllegalArgumentException("CyzSecurityContextHolder init error");
			}
			context = new CacheSecurityContext((RedisTemplate<String, Object>)redisTemplate);
		}
	}

} 
