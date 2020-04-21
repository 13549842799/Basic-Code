package com.cyz.basic.pojo;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public abstract class ModifyEntity<T> extends CreatorEntity<T> {
	
	protected Integer modify;
	
	protected LocalDateTime modifyTime;
	
	

	public ModifyEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ModifyEntity(T id, Byte delflag) {
		super(id, delflag);
		// TODO Auto-generated constructor stub
	}

	public ModifyEntity(T id) {
		super(id);
	}

	public Integer getModify() {
		return modify;
	}

	public void setModify(Integer modify) {
		this.modify = modify;
	}

	public LocalDateTime getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(LocalDateTime modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	

}
