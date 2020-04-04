package com.cyz.basic.config.security.config.annotation.web.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * ImportSelector 这个接口的实现类是配合{@Import} 这个注解来使用的，
 * 众所周知，{@Import}用来把bean注入到容器中的，哪怕那两个bean的实现类中没有相关的注解。
 * 而ImportSelector只有一个方法，返回值是个字符串数组，表示如果你想要那些类实例化注入容器中，
 * 就把实例名放入数组中返回。
 * @author cyz
 *
 */
public class SpringWebMvcImportSelector implements ImportSelector {

	/**
	 * 该方法将返回一个数组，也就是类实例名称
	 */
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		
		return new String[] {};
	}

}
