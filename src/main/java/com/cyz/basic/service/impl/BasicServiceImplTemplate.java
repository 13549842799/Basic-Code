package com.cyz.basic.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import com.cyz.basic.pojo.IdEntity;
import com.cyz.basic.service.support.BasicServiceSupport;


/**
 * the baisc to use this is had use the mapper which extend the BasicMapper
 * @author cyz
 *
 * @param <T>
 */
public abstract class BasicServiceImplTemplate<T, V> extends BasicServiceSupport<T, V>{
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public abstract T newEntity();
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public T getById(V id) {
		T t = this.newEntity();
		if (t instanceof IdEntity<V> entity) {
			entity.setId(id);
		} else {
			Assert.isAssignable(IdEntity.class, t.getClass(), "when you use this method， you have to extend the IdEntity");	
		}	
		return this.get(t);	
	}
	
	/**
	 * ͨ
	 * @param t
	 * @return
	 */
	public List<T> getList(T t) {
		fullDeleteSign(t);
		return basicMapper.getList(t);
	}
	
}
