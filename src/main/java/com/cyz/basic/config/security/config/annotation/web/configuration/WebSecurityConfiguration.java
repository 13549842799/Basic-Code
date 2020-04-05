package com.cyz.basic.config.security.config.annotation.web.configuration;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurer;
import com.cyz.basic.config.security.config.annotation.authentication.configuration.WebSecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.web.builders.WebSecurity;
import com.cyz.basic.config.security.context.AbstractSecurityWebApplicationInitializer;
import com.cyz.basic.config.security.context.DelegatingApplicationListener;


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
	
	@Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
	public Filter springSecurityFilterChain() throws Exception {
		boolean hasConfigurers = webSecurityConfigurers != null && !webSecurityConfigurers.isEmpty();
		
		if (!hasConfigurers) {
			WebSecurityConfigurerAdapter adapter = objectObjectPostProcessor.postProcess(new WebSecurityConfigurerAdapter() {});
			webSecurity.apply(adapter);
		}		
		return webSecurity.build();
	}

	
	@Autowired(required = false)
	public void setFilterChainProxySecurityConfigurer() {
		
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
