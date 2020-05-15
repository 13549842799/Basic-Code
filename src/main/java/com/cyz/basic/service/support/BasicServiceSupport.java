package com.cyz.basic.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.cyz.basic.Exception.AddErrorException;
import com.cyz.basic.Exception.AddErrorException.ErrorType;
import com.cyz.basic.enumeration.DeleteFlag;
import com.cyz.basic.mapper.BasicMapper;
import com.cyz.basic.pojo.DeleteAbleEntity;
import com.cyz.basic.service.BasicService;

/**
 *
 * @author cyz
 *
 * @param <T>
 */
public abstract class BasicServiceSupport<T, V>  implements BasicService<T, V>{
    
	@Autowired
	protected BasicMapper<T> basicMapper;
	
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
	
	public boolean delete(T t) {
		fullDelSign(t);
		return basicMapper.delete(t) == 1;
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
	
	/**
	 * 为对象填充delflag = 1
	 * @param t
	 */
	protected void fullDeleteSign (T t) {
		fullDeleteSign(t, DeleteFlag.VALID.getCode()); 
	}
	
	/**
	 * 为对象填充delflag = 0
	 * @param t
	 */
	protected void fullDelSign(T t) {
		fullDeleteSign(t, DeleteFlag.DELETE.getCode()); 
	}
	
	private void fullDeleteSign(T t, byte delflag) {
		if (t instanceof DeleteAbleEntity<V> entity) {
			entity.setDelflag(delflag);
		}
	}
	
	public T get(T t) {
		fullDeleteSign(t);
		return basicMapper.get(t);
	}
}
