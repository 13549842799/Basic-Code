package com.cyz.basic.constant;

public interface EntityConstants {
	  
	interface ENTITY {
		//实体keyName
	    String REDIS_OUSER = "ouser"; 
	  
	    String REDIS_EMPLOYEE = "employee";
	  
	    String REDIS_AUTHORITY = "authority";
	    
	    String REDIS_RESOURCE = "resource";
	}
	
	interface SESSION {
		String REDIS_SESSION_NAME = "authen_computer"; //电脑端的session的key名称
		  
		String REDIS_PHONE_SESSION_NAME = "authen_phone"; //移动端的session的key名称
		
		String ANONYMOUS = "anonymousUser"; 
		 
	}
	  
	  String REDIS_TOKEN_NAME = "token";	
	  
	  String REDIS_AUTHORITY_Map_NAME = "AuthMap";
	  
	  
	  int ORIGIN_COMP = 1;
	  
	  int ORIGIN_PHONE = 2;
	  
	  public static String loginConstant(int type) {
		 switch (type) {
		     case ORIGIN_COMP:case 3:
			     return SESSION.REDIS_SESSION_NAME;
		     case ORIGIN_PHONE:
			     return SESSION.REDIS_PHONE_SESSION_NAME;
		     default:
			     return null;
		 }
	  }
	  
	  public static String tokenKey(int type, String username) {
		  return loginConstant(type).concat("_").concat(username);
	  }

}