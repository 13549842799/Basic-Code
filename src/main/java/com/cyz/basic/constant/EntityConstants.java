package com.cyz.basic.constant;

public interface EntityConstants {
	
	  String REDIS_SESSION_NAME = "session"; //电脑端的session的key名称
	  
	  String REDIS_PHONE_SESSION_NAME = "session_phone"; //移动端的session的key名称
	  
	  String REDIS_TOKEN_NAME = "token";	
	  
	  String REDIS_AUTHORITY_Map_NAME = "AuthMap";
	  
	  //实体keyName
	  String REDIS_ADMIN = "admin"; 
	  
	  String REDIS_EMPLOYEE = "employee";
	  
	  String REDIS_AUTHORITY = "authority";
	  
	  String REDIS_RESOURCE = "resource";
	  
	  public static String loginConstant(int type) {
		 switch (type) {
		     case 1:case 3:
			     return REDIS_SESSION_NAME;
		     case 2:
			     return REDIS_PHONE_SESSION_NAME;
		     default:
			     return null;
		 }
	  }

}