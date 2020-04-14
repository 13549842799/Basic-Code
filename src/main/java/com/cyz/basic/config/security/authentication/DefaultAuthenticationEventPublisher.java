package com.cyz.basic.config.security.authentication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.authentication.event.AbstractAuthenticationEvent;
import com.cyz.basic.config.security.authentication.event.AbstractAuthenticationFailureEvent;
import com.cyz.basic.config.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import com.cyz.basic.config.security.authentication.event.AuthenticationFailureDisabledEvent;
import com.cyz.basic.config.security.authentication.event.AuthenticationFailureExpiredEvent;
import com.cyz.basic.config.security.authentication.event.AuthenticationFailureLockedEvent;
import com.cyz.basic.config.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import com.cyz.basic.config.security.authentication.event.AuthenticationSuccessEvent;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AccountExpiredException;
import com.cyz.basic.config.security.exception.AuthenticationException;
import com.cyz.basic.config.security.exception.BadCredentialsException;
import com.cyz.basic.config.security.exception.DisabledException;
import com.cyz.basic.config.security.exception.LockedException;
import com.cyz.basic.config.security.exception.ProviderNotFoundException;
import com.cyz.basic.config.security.exception.UsernameNotFoundException;

public class DefaultAuthenticationEventPublisher implements AuthenticationEventPublisher, ApplicationEventPublisherAware {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private ApplicationEventPublisher applicationEventPublisher;
	private final HashMap<String, Constructor<? extends AbstractAuthenticationEvent>> exceptionMappings = new HashMap<String, Constructor<? extends AbstractAuthenticationEvent>>();

	public DefaultAuthenticationEventPublisher() {
		this(null);
	}
	
	public DefaultAuthenticationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
		addMapping(BadCredentialsException.class.getName(),
				AuthenticationFailureBadCredentialsEvent.class);
		addMapping(UsernameNotFoundException.class.getName(),
				AuthenticationFailureBadCredentialsEvent.class);
		addMapping(AccountExpiredException.class.getName(),
				AuthenticationFailureExpiredEvent.class);
		addMapping(ProviderNotFoundException.class.getName(),
				AuthenticationFailureProviderNotFoundEvent.class);
		addMapping(DisabledException.class.getName(),
				AuthenticationFailureDisabledEvent.class);
		addMapping(LockedException.class.getName(),
				AuthenticationFailureLockedEvent.class);
		/*addMapping(AuthenticationServiceException.class.getName(),
				AuthenticationFailureServiceExceptionEvent.class);*/
		/*addMapping(CredentialsExpiredException.class.getName(),
				AuthenticationFailureCredentialsExpiredEvent.class);*/
		/*addMapping(
				"org.springframework.security.authentication.cas.ProxyUntrustedException",
				AuthenticationFailureProxyUntrustedEvent.class);*/
	}

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		if (applicationEventPublisher != null) {
			applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(
					authentication));
		}

	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		System.out.println(exception.getClass().getName());
		Constructor<? extends AbstractAuthenticationEvent> constructor = exceptionMappings
				.get(exception.getClass().getName());
		AbstractAuthenticationEvent event = null;

		if (constructor != null) {
			try {
				event = constructor.newInstance(authentication, exception);
			}
			catch (IllegalAccessException ignored) {
			}
			catch (InstantiationException ignored) {
			}
			catch (InvocationTargetException ignored) {
			}
		}

		if (event != null) {
			if (applicationEventPublisher != null) {
				applicationEventPublisher.publishEvent(event);
			}
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("No event was found for the exception "
						+ exception.getClass().getName());
			}
		}

	}
	
	@SuppressWarnings({ "unchecked" })
	public void setAdditionalExceptionMappings(Properties additionalExceptionMappings) {
		Assert.notNull(additionalExceptionMappings,
				"The exceptionMappings object must not be null");
		for (Object exceptionClass : additionalExceptionMappings.keySet()) {
			String eventClass = (String) additionalExceptionMappings.get(exceptionClass);
			try {
				Class<?> clazz = getClass().getClassLoader().loadClass(eventClass);
				Assert.isAssignable(AbstractAuthenticationFailureEvent.class, clazz);
				addMapping((String) exceptionClass,
						(Class<? extends AbstractAuthenticationFailureEvent>) clazz);
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to load authentication event class "
						+ eventClass);
			}
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	private void addMapping(String exceptionClass,
			Class<? extends AbstractAuthenticationFailureEvent> eventClass) {
		try {
			Constructor<? extends AbstractAuthenticationEvent> constructor = eventClass
					.getConstructor(Authentication.class, AuthenticationException.class);
			exceptionMappings.put(exceptionClass, constructor);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException("Authentication event class "
					+ eventClass.getName() + " has no suitable constructor");
		}
	}
}
