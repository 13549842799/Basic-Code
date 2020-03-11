package com.cyz.basic.net;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SessionInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7896444089723649641L;
	private long id;
	private String username;
	private String ip;
	private String token;
	private Long availableDate;
	private String mac;
	private Map<String,String> resourceList = new HashMap<>();//编号 注销地址
	
	public void addResource(String code, String signouturl) {
		this.resourceList.put(code, signouturl);
	}

	public long getId() {
		
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Map<String, String> getResourceList() {
		return resourceList;
	}
	public void setResourceList(Map<String, String> resourceList) {
		this.resourceList = resourceList;
	}
	public Long getAvailableDate() {
		return availableDate;
	}
	public void setAvailableDate(Long availableDate) {
		this.availableDate = availableDate;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
}
