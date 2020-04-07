package com.cyz.basic.config.security.config.annotation.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;

/**
 * Allows registering Objects to participate with an {@link AutowireCapableBeanFactory}'s
 * post processing of {@link Aware} methods, {@link InitializingBean#afterPropertiesSet()}
 * , and {@link DisposableBean#destroy()}.
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class AutowireBeanFactoryObjectPostProcessor 
    implements ObjectPostProcessor<Object>, DisposableBean, SmartInitializingSingleton{
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private final AutowireCapableBeanFactory autowireBeanFactory;
	private final List<DisposableBean> disposableBeans = new ArrayList<>();
	/**
	 * 作用是在spring容器管理的所有单例对象（非懒加载对象）初始化完成之后调用的回调接口
	 */
	private final List<SmartInitializingSingleton> smartSingletons = new ArrayList<>();
	
	public AutowireBeanFactoryObjectPostProcessor(AutowireCapableBeanFactory autowireBeanFactory) {
		Assert.notNull(autowireBeanFactory, "autowireBeanFactory cannot be null");
		this.autowireBeanFactory = autowireBeanFactory;
	}
	

	@Override
	public void afterSingletonsInstantiated() {
		for (SmartInitializingSingleton singleton : smartSingletons) {
			singleton.afterSingletonsInstantiated();
		}
	}

	@Override
	public void destroy() throws Exception {
		for (DisposableBean disposable : this.disposableBeans) {
			try {
				disposable.destroy();
			}
			catch (Exception error) {
				this.logger.error(error);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T postProcess(T object) {
		if (object == null) {
			return null;
		}
		T result = null;
		try {
			result = (T) this.autowireBeanFactory.initializeBean(object,
					object.toString());
		}
		catch (RuntimeException e) {
			Class<?> type = object.getClass();
			throw new RuntimeException(
					"Could not postProcess " + object + " of type " + type, e);
		}
		this.autowireBeanFactory.autowireBean(object);
		if (result instanceof DisposableBean) {
			this.disposableBeans.add((DisposableBean) result);
		}
		if (result instanceof SmartInitializingSingleton) {
			this.smartSingletons.add((SmartInitializingSingleton) result);
		}
		return result;
	}
	
	

}
