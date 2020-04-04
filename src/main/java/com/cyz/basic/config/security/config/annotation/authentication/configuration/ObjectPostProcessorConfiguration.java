package com.cyz.basic.config.security.config.annotation.authentication.configuration;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.configuration.AutowireBeanFactoryObjectPostProcessor;

/**
 * Spring {@link Configuration} that exports the default {@link ObjectPostProcessor}. This
 * class is not intended to be imported manually rather it is imported automatically when
 * using {@link EnableWebSecurity} or {@link EnableGlobalMethodSecurity}.
 *
 * @see EnableWebSecurity
 * @see EnableGlobalMethodSecurity
 *
 * @author Rob Winch
 * @since 3.2
 */
@Configuration
public class ObjectPostProcessorConfiguration {

	@Bean
	public ObjectPostProcessor<Object> objectPostProcessor(
			AutowireCapableBeanFactory beanFactory) {
		return new AutowireBeanFactoryObjectPostProcessor(beanFactory);
	}
}
