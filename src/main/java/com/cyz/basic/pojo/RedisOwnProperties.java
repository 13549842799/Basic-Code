package com.cyz.basic.pojo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author cyz
 *
 */
@ConfigurationProperties(prefix = "own.redis.expire")
public class RedisOwnProperties {
	
	/**
	 * 格式为缓存区名#过期时间(秒)
	 */
	private String[] mappers;
	
	private Map<String, Long> expireMap = new LinkedHashMap<>(0);

	public String[] getMappers() {
		return mappers;
	}

	public void setMappers(String[] mappers) {		
		this.mappers = mappers;
		if (mappers != null && mappers.length > 0) {
			expireMap = new LinkedHashMap<>(mappers.length);
			for (String str : mappers) {
				String[] s = null;
				if (str.indexOf("#") == -1 || (s = str.split("#")).length != 2) {
					continue;
				}
				try {
					expireMap.put(s[0], Long.parseLong(s[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Map<String, Long> getExpireMap() {
		return Collections.unmodifiableMap(this.expireMap);
	}
	

}
