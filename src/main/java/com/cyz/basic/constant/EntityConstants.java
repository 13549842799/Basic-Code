package com.cyz.basic.constant;

public interface EntityConstants {
	
	  String REDIS_SESSION_NAME = "authen_computer"; //电脑端的session的key名称
	  
	  String REDIS_PHONE_SESSION_NAME = "authen_phone"; //移动端的session的key名称
	  
	  String REDIS_TOKEN_NAME = "token";	
	  
	  String REDIS_AUTHORITY_Map_NAME = "AuthMap";
	  
	  //实体keyName
	  String REDIS_ADMIN = "admin"; 
	  
	  String REDIS_EMPLOYEE = "employee";
	  
	  String REDIS_AUTHORITY = "authority";
	  
	  String REDIS_RESOURCE = "resource";
	  
	  String ANONYMOUS = "anonymousUser";
	  
	  int ORIGIN_COMP = 1;
	  
	  int ORIGIN_PHONE = 2;
	  
	  public static String loginConstant(int type) {
		 switch (type) {
		     case ORIGIN_COMP:case 3:
			     return REDIS_SESSION_NAME;
		     case ORIGIN_PHONE:
			     return REDIS_PHONE_SESSION_NAME;
		     default:
			     return null;
		 }
	  }
	  
	  public static String tokenKey(int type, String username) {
		  return loginConstant(type).concat("_").concat(username);
	  }

}