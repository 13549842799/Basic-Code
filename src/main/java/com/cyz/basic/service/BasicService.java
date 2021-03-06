package com.cyz.basic.service;

import java.lang.reflect.InvocationTargetException;

import com.cyz.basic.Exception.AddErrorException;
import com.cyz.basic.pojo.IdEntity;

public interface BasicService<T, V> {
	 
	void add(T t) throws AddErrorException;
     
	/**
	 * 添加
	 * @param t
	 * @param cls 自动新增的id的类型
	 * @throws AddErrorException
	 */
	default void add(T t,  Class<V> cls) throws AddErrorException {
		add(t);
		if (t instanceof IdEntity<V> entity) {
			try {
				entity.mainTainId(cls);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 更新
	 * @param t
	 * @return 
	 */
	int update(T t);
	
	/**
	 *全字段更新方法
	 * @param t
	 * @return 
	 */
	int updateFull(T t);
	
	/**
	 * 删除方法
	 * @param id
	 * @return
	 */
	boolean delete(T t);
}
