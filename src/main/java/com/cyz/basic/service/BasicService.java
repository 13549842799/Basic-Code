package com.cyz.basic.service;

import java.lang.reflect.InvocationTargetException;

import com.cyz.basic.Exception.AddErrorException;
import com.cyz.basic.pojo.IdEntity;

public interface BasicService<T> {
	 
	void add(T t) throws AddErrorException;
     
	/**
	 * 继承了IdEntity的实体类的添加方法，
	 * 因为IdEntity中是泛型的id，所以获取自增主键的时候id的类型会有问题导致调用get方法出错
	 * 所以重新设置了id的类型
	 * @param t 保存的实体
	 * @param cls id的类型的class
	 * @throws AddErrorException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	default void add(T t,  Class cls) throws AddErrorException {
		add(t);
		if (t instanceof IdEntity) {
			try {
				((IdEntity<?>)t).mainTainId(cls);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 变量更新
	 * @param t
	 * @return 1-成功更新  0-没有更新  -1-更新多条
	 */
	int update(T t);
	
	/**
	 * 全量更新
	 * @param t
	 * @return 1-成功更新  0-没有更新  -1-更新多条
	 */
	int updateFull(T t);
	
	/**
	 * 删除记录
	 * @param id
	 * @return
	 */
	boolean delete(T t);
}
