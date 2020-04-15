package com.cyz.basic.valid.configuration;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import com.cyz.basic.valid.annotation.Mapper;

public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MapperScannerConfigurer.class);

    private String basePackage;

    /**
     * 默认为Mapper注解
     */
    private Class<? extends Annotation> markedAnnotation = Mapper.class;

    /**
     * bean name生成器
     */
    private BeanNameGenerator beanNameGenerator;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setMarkedAnnotation(Class<? extends Annotation> markedAnnotation) {
        this.markedAnnotation = markedAnnotation;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    @Override
    public void afterPropertiesSet() {
        if(basePackage == null) {
            throw new IllegalArgumentException("the property 'basePackage'can not be null!");
        }
    }

    /**
     * 扫描指定包, 并且将满足扫描条件的的接口生成代理注册
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        MapperScanner mapperScanner = new MapperScanner(registry);
        // 添加扫描条件, 默认只扫描@Mapper标记的类文件
        mapperScanner.addIncludeFilter(new AnnotationTypeFilter(markedAnnotation));
        if(beanNameGenerator == null) {
            beanNameGenerator = new MapperAnnotationBeanNameGenerator(markedAnnotation);
        }
        mapperScanner.setBeanNameGenerator(beanNameGenerator);
        // 开始扫描, 并注册
        int beanCount = mapperScanner.scan(StringUtils.tokenizeToStringArray(basePackage, ","));
        LOG.debug("The count of registered bean is " + beanCount);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) 
                                           throws BeansException {
        // just do nothing.
    }
}
