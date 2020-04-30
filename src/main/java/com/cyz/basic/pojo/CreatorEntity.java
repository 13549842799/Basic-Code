package com.cyz.basic.pojo;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public abstract class CreatorEntity<T> extends DeleteAbleEntity<T> {
	
	protected Integer creator;
	
	protected LocalDateTime createTime;

	public CreatorEntity() {
		super();
		createTime = LocalDateTime.now();
	}

	public CreatorEntity(T id, Byte delflag) {
		super(id, delflag);
		createTime = LocalDateTime.now();
	}

	public CreatorEntity(T id) {
		super(id);
		createTime = LocalDateTime.now();
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	
    public void create(Integer userId) {
    	this.creator = userId;
    	this.createTime = LocalDateTime.now();
    }
}
