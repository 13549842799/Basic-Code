package com.cyz.basic.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public final class SpringContextHolder implements ApplicationContextAware{
	
	private static  ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
	
	public static ApplicationContext getContext() {
		return context;
	}

	public static <T>T getBean(Class<T> cls) {
		Assert.notNull(context, "context 尚未注入");
		return context.getBean(cls);
	}
}
