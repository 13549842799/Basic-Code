package com.cyz.basic.config.security;

import java.util.List;

import javax.servlet.Filter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;

import com.cyz.basic.config.security.config.annotation.web.builders.HttpSecurity;
import com.cyz.basic.config.security.core.context.CyzSecurityContextHolder;

@EnableConfigurationProperties(value={SecurityProperties.class})
public abstract class WebSecurityAdapter {
	
	private List<Filter> filters;
	
	public WebSecurityAdapter(RedisTemplate<String, Object> redis) {
		CyzSecurityContextHolder.inintHolder(redis);
	}
	
	protected void configure(HttpSecurity http) throws Exception {
		
	}
	
	protected abstract void additionalConfigure(HttpSecurity http);

}
