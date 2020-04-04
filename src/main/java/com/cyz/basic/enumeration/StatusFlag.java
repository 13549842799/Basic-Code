package com.cyz.basic.enumeration;

public enum StatusFlag {
	
	
	DISABLE("禁用",(byte)0),
	ENABLE("正常",(byte)1);
	
	private String name;
	private byte code;
	
	private StatusFlag(String name,byte code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public byte getCode() {
		return code;
	}

	public static StatusFlag theEnum(byte code) {
		switch (code) {
		case (byte)0:		
			return StatusFlag.DISABLE;
		default:
			return StatusFlag.ENABLE;
		}
	}

	
}
