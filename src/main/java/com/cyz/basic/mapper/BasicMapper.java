package com.cyz.basic.mapper;

import java.util.List;

/**
 * 
 * @author cyz
 *
 * @param <T>
 */
public interface BasicMapper<T> {
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public int add(T t);
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public int delete(T t);
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public int update(T t);
	
	public int updateFull(T t);
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public T get(T t);
	
	public List<T> getList(T t);

}
