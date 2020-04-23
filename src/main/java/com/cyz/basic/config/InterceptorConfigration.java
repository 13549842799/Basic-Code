package com.cyz.basic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.cyz.basic.web.interceptor.MyInterceptor;

@Configuration
public class InterceptorConfigration extends WebMvcConfigurationSupport {
	
    @Bean 
    public MyInterceptor myInterceptor() {
    	return new MyInterceptor();
    }
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor()).addPathPatterns("/**");
    }


}
