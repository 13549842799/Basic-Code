package com.cyz.basic.service;

import java.lang.reflect.InvocationTargetException;

import com.cyz.basic.Exception.AddErrorException;
import com.cyz.basic.pojo.IdEntity;

public interface BasicService<T> {
	 
	void add(T t) throws AddErrorException;
     
	/**
	 * �̳���IdEntity��ʵ�������ӷ�����
	 * ��ΪIdEntity���Ƿ��͵�id�����Ի�ȡ����������ʱ��id�����ͻ������⵼�µ���get��������
	 * ��������������id������
	 * @param t �����ʵ��
	 * @param cls id�����͵�class
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
	 * ��������
	 * @param t
	 * @return 1-�ɹ�����  0-û�и���  -1-���¶���
	 */
	int update(T t);
	
	/**
	 * ȫ������
	 * @param t
	 * @return 1-�ɹ�����  0-û�и���  -1-���¶���
	 */
	int updateFull(T t);
	
	/**
	 * ɾ����¼
	 * @param id
	 * @return
	 */
	boolean delete(T t);
}
