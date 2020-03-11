package com.cyz.basic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
public abstract class BasicServiceImplTemplate<T> extends BasicServiceSupport<T> implements BasicService<T> {

	@Autowired
	protected BasicMapper<T> basicMapper;
	
	public abstract T newEntity();
	
	public T get(T t) {
		fullDeleteSign(t);
		return basicMapper.get(t);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public T getById(long id) throws Exception{
		T t = this.newEntity();
		if (t instanceof IdEntity) {
			((IdEntity<?>)t).acceptId(id);			
			return this.get(t);
		}		
		throw new Exception("when you use this method， you have to extend the IdEntity");
	}
	
	/**
	 * ͨ
	 * @param t
	 * @return
	 */
	public List<T> getList(T t) {
		return basicMapper.getList(t);
	}
	
	public void add(T t) throws AddErrorException {
		fullDeleteSign(t);
	    try {
		  	int count = basicMapper.add(t);
		  	if (count == 0) {
		  		throw new AddErrorException(ErrorType.OBJECT_NOT_ALL);
		  	}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddErrorException(ErrorType.OBJECT_FUNCTION_ERROR);
		}
	}
	
	@Transactional
	public int update(T t) {
		fullDeleteSign(t);
		int count = basicMapper.update(t);
		if (count == 0 || count == 1) {
			return count;
		}
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return -1;
	}
	
	@Transactional
	public int updateFull(T t) {
		fullDeleteSign(t);
		int count = basicMapper.updateFull(t);
		if (count == 0 || count == 1) {
			return count;
		}
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return -1;
	}
	
	public boolean delete(T t) {
		fullDelSign(t);
		return basicMapper.delete(t) == 1;
	}
	
	/**
	 * 
	 * @param t
	 */
	private void fullDeleteSign (T t) {
		fullDeleteSign(t, DeleteFlag.VALID.getCode()); 
	}
	
	/**
	 * 
	 * @param t
	 */
	private void fullDelSign(T t) {
		fullDeleteSign(t, DeleteFlag.DELETE.getCode()); 
	}
	
	@SuppressWarnings("rawtypes")
	private void fullDeleteSign(T t, byte delflag) {
		if (t instanceof DeleteAbleEntity) {
			((DeleteAbleEntity)t).setDelflag(delflag);
		}
	}
}
