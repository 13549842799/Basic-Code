package com.cyz.basic.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import com.cyz.basic.Exception.AddErrorException;
import com.cyz.basic.Exception.AddErrorException.ErrorType;
import com.cyz.basic.enumeration.DeleteFlag;
import com.cyz.basic.mapper.BasicMapper;
import com.cyz.basic.pojo.DeleteAbleEntity;
import com.cyz.basic.pojo.IdEntity;
import com.cyz.basic.service.BasicService;
import com.cyz.basic.service.support.BasicServiceSupport;


/**
 * the baisc to use this is had use the mapper which extend the BasicMapper
 * @author cyz
 *
 * @param <T>
 */
public abstract class BasicServiceImplTemplate<T> extends BasicServiceSupport<T> /*implements BasicService<T> */{
	
	public abstract T newEntity();
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public T getById(long id) {
		T t = this.newEntity(); 
		Assert.isAssignable(IdEntity.class, t.getClass(), "when you use this method， you have to extend the IdEntity");
		((IdEntity<?>)t).acceptId(id);			
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
