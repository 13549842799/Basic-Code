package com.cyz.basic.pojo;

/**
 * 方法结果返回类
 * 结果类可以为方法区分多种返回结果
 * 包括 1个正确的返回结果 和 多个错误的返回结果
 * @author cyz
 *
 * @param <T>
 */
public final class MethodResult<T> {
public static final byte SUCCESS = 1;
	
	public static final byte FAIL = 0;
	
	public static final String ERROR_KEY = "error";
	
	private byte status = SUCCESS;
	
	private T data;
	
	private String errorName;
	
	private String message;
	
	public boolean isSuccess () {
		return this.status == SUCCESS;
	}
	
	public boolean fail () {
		return this.status == FAIL;
	}
	
	public MethodResult<T> success() {
		return this.success(null);
	}

	public MethodResult<T> success(T data) {
		this.data = data;
		this.status = SUCCESS;
		return this;
	}
	
	public MethodResult<T> fail(String message) {
	
		return this.fail(ERROR_KEY, message);
	}
	
	public MethodResult<T> fail(String errorName, String message) {
		this.status = FAIL;
		this.errorName = errorName;
		this.message = message;
		return this;
	}
	
	public String getErrorMessage() {
		return this.fail() ? this.message : null;
	}
	
	public String error() {
		return this.fail() ? this.errorName : null;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
