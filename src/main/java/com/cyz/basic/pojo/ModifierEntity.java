package com.cyz.basic.pojo;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public abstract class ModifierEntity<T> extends CreatorEntity<T> {
	
	protected Integer modifier;
	
	protected LocalDateTime modifyTime;
	
	

	public ModifierEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ModifierEntity(T id, Byte delflag) {
		super(id, delflag);
		// TODO Auto-generated constructor stub
	}

	public ModifierEntity(T id) {
		super(id);
	}

	

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	public LocalDateTime getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(LocalDateTime modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	

}
