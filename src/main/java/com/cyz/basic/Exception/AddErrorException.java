package com.cyz.basic.Exception;

public class AddErrorException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5073660646948517330L;

	public AddErrorException(ErrorType type) {
		super(type.getMessage());
	}
	
	public enum ErrorType {
		
        OBJECT_NOT_ALL(1,"插入对象不成功"),
		
		OBJECT_ADD_NUM_ERROR(2,"实际插入数量与目标不同"),
		
		OBJECT_FUNCTION_ERROR(3,"插入时发生未知异常");
		
		private int sign;
		private String message;
		
		private ErrorType(int sign,String message) {
			this.sign = sign;
			this.message = message;
		}

		public int getSign() {
			return sign;
		}

		public void setSign(int sign) {
			this.sign = sign;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
		
	}

}
