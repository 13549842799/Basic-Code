package com.cyz.basic.config.security.config.annotation.web.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.RedisTemplate;

import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurer;
import com.cyz.basic.config.security.config.annotation.authentication.configuration.WebSecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.web.builders.WebSecurity;
import com.cyz.basic.config.security.context.AbstractSecurityWebApplicationInitializer;
import com.cyz.basic.config.security.context.DelegatingApplicationListener;
import com.cyz.basic.config.security.core.context.SecurityContextHolder;


/**
 * Uses a {@link WebSecurity} to create the {@link FilterChainProxy} that performs the web
 * based security for Spring Security. It then exports the necessary beans. Customizations
 * can be made to {@link WebSecurity} by extending {@link WebSecurityConfigurerAdapter}
 * and exposing it as a {@link Configuration} or implementing
 * {@link WebSecurityConfigurer} and exposing it as a {@link Configuration}. This
 * configuration is imported when using {@link EnableWebSecurity}.
 * 
 * ImportAware接口必须配合@Configuration使用，它会有一个实现方法setImportMetadata,这个方法可以获取到注解上的参数。
 * 
 * BeanClassLoaderAware 的作用好像是获取到类加载器
 *
 *根据自身情况进行改造
 *这个类是WebSecurity的配置类，
 *主要做了以下的工作，包括
 *实现了BeanClassLoaderAware接口来获取beanClassLoader，
 *对DelegatingApplicationListener进行实例化并注入到Spring容器中（获取bean），这个DelegatingApplicationListener
 *是对一个集合的SmartApplicationLitener的管理监听类。我们会把自己所有的事件监听类添加到这个管理类中。
 *
 *
 * @see EnableWebSecurity
 * @see WebSecurity
 *
 * @author Rob Winch
 * @author Keesun Baik
 * @since 3.2
 */
@Configuration
public class WebSecurityConfiguration implements ImportAware, BeanClassLoaderAware{

	private Boolean debugEnabled;
	
	private WebSecurity webSecurity;

	private List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers;
	
	private ClassLoader beanClassLoader;
	
	/**
	 * 在AuthenticationConfiguration中被导入了Spring容器
	 */
	@Autowired(required = false)
	private ObjectPostProcessor<Object> objectObjectPostProcessor;
	
	@Bean
	public static DelegatingApplicationListener delegatingApplicationListener() {
		return new DelegatingApplicationListener();
	}
	
	@Autowired
	public void setCyzSecurityContextHolder(RedisTemplate<String, Object> redisTemplate) {
		SecurityContextHolder.inintHolder(redisTemplate);
	}
	
	@Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
	public Filter springSecurityFilterChain() throws Exception {
		boolean hasConfigurers = webSecurityConfigurers != null && !webSecurityConfigurers.isEmpty();
		if (!hasConfigurers) {
			WebSecurityConfigurerAdapter adapter = objectObjectPostProcessor.postProcess(new WebSecurityConfigurerAdapter() {});
			webSecurity.apply(adapter);
		}		
		return webSecurity.build();
	}

	/**
	 * Sets the {@code <SecurityConfigurer<FilterChainProxy, WebSecurityBuilder>}
	 * instances used to create the web configuration.
	 *
	 * @param objectPostProcessor the {@link ObjectPostProcessor} used to create a
	 * {@link WebSecurity} instance
	 * @param webSecurityConfigurers the
	 * {@code <SecurityConfigurer<FilterChainProxy, WebSecurityBuilder>} instances used to
	 * create the web configuration
	 * @throws Exception
	 */
	@Autowired(required = false)
	public void setFilterChainProxySecurityConfigurer(ObjectPostProcessor<Object> objectPostProcessor,
			@Value("#{@autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()}") List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers) throws Exception {
		webSecurity = objectPostProcessor
				.postProcess(new WebSecurity(objectPostProcessor));
		if (debugEnabled != null) {
			webSecurity.debug(debugEnabled);
		}
		
		Collections.sort(webSecurityConfigurers, AnnotationAwareOrderComparator.INSTANCE);
		Integer previousOrder = null;
		Object previousConfig = null;
		for (SecurityConfigurer<Filter, WebSecurity> config : webSecurityConfigurers) {
			Integer order = AnnotationAwareOrderComparator.lookupOrder(config);
			if (previousOrder != null && previousOrder.equals(order)) {
				throw new IllegalStateException(
						"@Order on WebSecurityConfigurers must be unique. Order of "
								+ order + " was already used on " + previousConfig + ", so it cannot be used on "
								+ config + " too.");
			}
			previousOrder = order;
			previousConfig = config;
		}
		for (SecurityConfigurer<Filter, WebSecurity> webSecurityConfigurer : webSecurityConfigurers) {
			webSecurity.apply(webSecurityConfigurer);
		}
		this.webSecurityConfigurers = webSecurityConfigurers;
	}
	
	@Bean
	public static AutowiredWebSecurityConfigurersIgnoreParents autowiredWebSecurityConfigurersIgnoreParents(
			ConfigurableListableBeanFactory beanFactory) {
		return new AutowiredWebSecurityConfigurersIgnoreParents(beanFactory);
	}
	
	/**
	 * A custom verision of the Spring provided AnnotationAwareOrderComparator that uses
	 * {@link AnnotationUtils#findAnnotation(Class, Class)} to look on super class
	 * instances for the {@link Order} annotation.
	 *
	 * @author Rob Winch
	 * @since 3.2
	 */
	private static class AnnotationAwareOrderComparator extends OrderComparator {
		private static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

		@Override
		protected int getOrder(Object obj) {
			return lookupOrder(obj);
		}

		private static int lookupOrder(Object obj) {
			
			if (obj instanceof Ordered) {
				return ((Ordered) obj).getOrder();
			}
			if (obj != null) {
				Class<?> clazz = (obj instanceof Class ? (Class<?>) obj : obj.getClass());
				Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
				if (order != null) {
					return order.value();
				}
			}
			return Ordered.LOWEST_PRECEDENCE;
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.context.annotation.ImportAware#setImportMetadata(org.
	 * springframework.core.type.AnnotationMetadata)
	 */
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		Map<String, Object> enableWebSecurityAttrMap = importMetadata
				.getAnnotationAttributes(EnableWebSecurity.class.getName());
		AnnotationAttributes enableWebSecurityAttrs = AnnotationAttributes
				.fromMap(enableWebSecurityAttrMap);
		debugEnabled = enableWebSecurityAttrs.getBoolean("debug");
		if (webSecurity != null) {
			webSecurity.debug(debugEnabled);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.
	 * lang.ClassLoader)
	 */
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}
}
