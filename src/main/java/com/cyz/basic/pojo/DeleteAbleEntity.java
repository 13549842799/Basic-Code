package com.cyz.basic.pojo;

@SuppressWarnings("serial")
public abstract class DeleteAbleEntity<T> extends IdEntity<T> {
	
	protected Byte delflag; //删除标志 0-删除 1-整除

	public DeleteAbleEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DeleteAbleEntity(T id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	public DeleteAbleEntity(T id, Byte delflag) {
		this(id);
		this.delflag = delflag;
	}

	public Byte getDelflag() {
		return delflag;
	}

	public void setDelflag(Byte delflag) {
		this.delflag = delflag;
	}
	
	

}
