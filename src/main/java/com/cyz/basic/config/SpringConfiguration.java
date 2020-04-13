package com.cyz.basic.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
public class SpringConfiguration {
	
	/**
	 * RequestContextFilter实现ServletRequestListener监听器接口， 该监听器监听HTTP请求事件，Web服务器接收的每次请求都会通知该监听器。通过配 
	 * 置RequestContextFilter，Spring容器与Web容器结合的更加密切,令SpringContextHolder类中能够获取到request，如果不加这个监听则会空指针
	 * @return
	 */
	@Bean
	public ServletListenerRegistrationBean<RequestContextListener> lr() {		
		ServletListenerRegistrationBean<RequestContextListener>  srb = new ServletListenerRegistrationBean<>();
		srb.setListener(new RequestContextListener());		
		return srb;
	}
	
	@Autowired
	public void initMessageSource() {
		Locale.setDefault(Locale.CHINESE);
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/messages");// name of the resource bundle
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("UTF-8");
		SpringMessageSource.setMessageSource(new MessageSourceAccessor(source));
	}
	
	public static class SpringMessageSource {
		
		private static MessageSourceAccessor ACCESS = null;
		
		public static void setMessageSource(MessageSourceAccessor a) {
			ACCESS = a;
		}
		
		public static MessageSourceAccessor getAccess() {
			return ACCESS;
		}
	}


}