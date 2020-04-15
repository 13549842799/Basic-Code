package com.cyz.basic.valid.configuration;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

public class MapperScanner extends ClassPathBeanDefinitionScanner {
	public MapperScanner(BeanDefinitionRegistry registry) {
        // 禁用掉默认的扫描选择条件
        // 默认只扫描被@Component标记的类,
        // 当然了衍生注解(@Controller、@Service、@Repository)也会被扫描
        super(registry, false);
    }

    /**
     * 默认情况下只有顶层具体类才会通过
     * 只返回是接口的beanDefinition
     * @param beanDefinition bean
     * @return true / false
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() 
            && beanDefinition.getMetadata().isIndependent();
    }

    /**
     * 注册扫描的类
     * @param basePackages 包数组, 形如{"com.wangtao"}
     * @return 返回实际注册的bean数量
     */
    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        // 父类方法已经向Spring容器中注册过这些BeanDefinition了, 只需修改此引用
        // 不需要再注册
        Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : holders) {
            convertToMapperFactoryBean(holder.getBeanDefinition());
        }
        
        return holders;
    }

    /**
     * 修改原有Mapper接口的beanDefinition, 将其转化为MapperFactoryBean的beanDefinition
     * @param beanDefinition Mapper接口的beanDefinition
     */
    private void convertToMapperFactoryBean(BeanDefinition beanDefinition) {
        // 强转
        GenericBeanDefinition mapperFactoryDefinition = (GenericBeanDefinition) beanDefinition;
        // 只能拿到接口名, 不能拿到Class对象, 因为此时还没有被类加载器加载
        String mapperInterfaceName = beanDefinition.getBeanClassName();
        mapperFactoryDefinition.setBeanClass(MapperFactoryBean.class);

        // 使用构造函数注入
        // 这里给的只是接口的完全限定名, 而不是Class对象, 因为Spring初始化的时候
        // 会自动将字符串转化成对应的类型, 而对应这里将会使用的是ClassEditor转化功能.
        // 之所以不用Class, 是因为对应Class文件此时还没有被类加载器加载
        mapperFactoryDefinition.getConstructorArgumentValues()
            .addIndexedArgumentValue(0, mapperInterfaceName);
    }
}
