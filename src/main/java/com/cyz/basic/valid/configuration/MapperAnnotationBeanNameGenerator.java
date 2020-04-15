package com.cyz.basic.valid.configuration;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * bean name生成器
 * 继承自AnnotationBeanNameGenerator
 * AnnotationBeanNameGenerator会解析Componnet注解或者其衍生注解(@Service、@Repository等)的
 * value属性
 * 获取名字, 如果没有显示指定value属性, 那么会生成一个默认名字, 即类名首字母小写后的名字
 * 现在想要实现解析指定注解的value属性而不是Component注解
 * 如果value属性没有指定, 那么采用默认策略
 */
public class MapperAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    /**
     * 以这个注解指定的名字来生成bean name
     */
    private Class<? extends Annotation> annotation;

    public MapperAnnotationBeanNameGenerator(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
        
    }

    /*
     * 所有注解信息都是通过.class文件解析出来的, 基于ASM实现, 也意味着这个类还被有被加载
     */
    @Override
    public String generateBeanName(BeanDefinition definition, 
                                   BeanDefinitionRegistry registry) {
        return super.generateBeanName(definition, registry);
    }

    /**
     * 重写此方法, 使得适配@Mapper注解指定的bean name.
     * 决定generateBeanName方法是否解析此annotationType代表的注解
     * 举例:
     * 若 annotationType = org.springframework.stereotype.Service(@Service)
     * 那么 metaAnnotationTypes = [@Target, @Retention, @Documented, @Component]
     * 即标记annotationType这个注解的注解集合
     * 那么 attributes = {value: ''}
     * @param annotationType 想要解析的注解
     * @param metaAnnotationTypes 注解annotationType的注解集合
     * @param attributes annotationType注解的属性集合
     */
    @Override
    protected boolean isStereotypeWithNameValue(String annotationType, 
                                                Set<String> metaAnnotationTypes, 
                                                Map<String, Object> attributes) {
        boolean isStereotype = (Objects.equals(annotationType, annotation.getName()))
                || (metaAnnotationTypes != null 
                    && metaAnnotationTypes.contains(annotation.getName()));
        return isStereotype && attributes.containsKey("value");
    }
}