package com.cyz.basic.enumeration;

public enum DeleteFlag {
	
	
	DELETE("删除",(byte)0),
	VALID("正常",(byte)1);
	
	private String name;
	private byte code;
	
	private DeleteFlag(String name,byte code){
		 this.name = name;
		 this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte getCode() {
		return code;
	}
	public void setCode(byte code) {
		this.code = code;
	}

	
}
