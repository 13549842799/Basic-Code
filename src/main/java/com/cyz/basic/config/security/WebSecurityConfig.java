package com.cyz.basic.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.cyz.basic.config.security.config.annotation.authentication.configuration.WebSecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.web.builders.HttpSecurity;

@EnableConfigurationProperties(value={SecurityProperties.class})
public abstract class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private SecurityProperties properties;

	public WebSecurityConfig() {
		super();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated()
		.and().formLogin().loginProcessingUrl(properties.getLoginUrl()).permitAll();//登录行为任意访问     
		http.logout().clearAuthentication(true).logoutUrl(properties.getLogoutUrl()); //只允许已登录的访问
	}
	

}
