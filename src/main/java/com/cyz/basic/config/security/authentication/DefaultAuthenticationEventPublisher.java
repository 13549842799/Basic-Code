package com.cyz.basic.config.security.authentication;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.cyz.basic.config.security.authentication.event.AbstractAuthenticationEvent;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AuthenticationException;

public class DefaultAuthenticationEventPublisher implements AuthenticationEventPublisher, ApplicationEventPublisherAware {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private ApplicationEventPublisher applicationEventPublisher;
	private final HashMap<String, Constructor<? extends AbstractAuthenticationEvent>> exceptionMappings = new HashMap<String, Constructor<? extends AbstractAuthenticationEvent>>();

	public DefaultAuthenticationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
		
	}

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

}
