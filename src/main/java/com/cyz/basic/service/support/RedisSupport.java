package com.cyz.basic.service.support;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis的单独存取工具支持类
 * @author cyz
 *
 */
public abstract class RedisSupport {
	
	protected abstract RedisTemplate<String, Object> getRedisTemplate();
	
	protected abstract StringRedisTemplate getStringRedisTemplate();
	
	public Object get(String key) {
		return this.getRedisTemplate().opsForValue().get(key);
	}
	
    public String getStr(String key) {
    	return this.getStringRedisTemplate().opsForValue().get(key);
    }
    
    public String createToken(Map<String, String> params) {
    	return UUID.randomUUID().toString();
    }
    
    public boolean hasKey(String key) {
    	return this.getRedisTemplate().hasKey(key);
    }

}
