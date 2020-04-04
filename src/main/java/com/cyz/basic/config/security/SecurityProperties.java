package com.cyz.basic.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 重写后的secury框架所需的参数
 * @author cyz
 *
 */
@ConfigurationProperties(prefix = "own.security")
public class SecurityProperties {

	private String loginPage;
	
	private String loginUrl;
	
	private String logoutUrl;
	
	private Long expireTime = -1l; //毫秒
	
	

	public String getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}
	
	
}
