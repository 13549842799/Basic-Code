package com.cyz.basic.config.security.access.intercept;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.access.AccessDecisionManager;
import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.access.SecurityMetadataSource;
import com.cyz.basic.config.security.access.event.AuthenticationCredentialsNotFoundEvent;
import com.cyz.basic.config.security.access.event.AuthorizationFailureEvent;
import com.cyz.basic.config.security.access.event.AuthorizedEvent;
import com.cyz.basic.config.security.access.event.PublicInvocationEvent;
import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.core.context.SecurityContextHolder;
import com.cyz.basic.config.security.exception.AccessDeniedException;
import com.cyz.basic.config.security.exception.AuthenticationCredentialsNotFoundException;
import com.cyz.basic.web.SpringMvcHolder;


public abstract class AbstractSecurityInterceptor implements InitializingBean, ApplicationEventPublisherAware, MessageSourceAware{

	protected final Log logger = LogFactory.getLog(getClass());
	
	protected MessageSourceAccessor messages = null;
	private AuthenticationManager authenticationManager;
	private AccessDecisionManager accessDecisionManager;
	/**
	 * SpringBoot发布的时间监听器，详细的百度一下
	 */
	private ApplicationEventPublisher eventPublisher;
	
	private boolean alwaysReauthenticate = false; //总是从新获取authenticate
	private boolean rejectPublicInvocations = false; //拒绝触发事件监听
	private boolean publishAuthorizationSuccess = false;
	
	public void afterPropertiesSet() throws Exception {
		
	}
	
	protected void beforeInvocation(Object object) {
		Assert.notNull(object, "Object was null");
		final boolean debug = logger.isDebugEnabled();
		
		if (!getSecureObjectClass().isAssignableFrom(object.getClass())) {
			throw new IllegalArgumentException(
					"Security invocation attempted for object "
							+ object.getClass().getName()
							+ " but AbstractSecurityInterceptor only configured to support secure objects of type: "
							+ getSecureObjectClass());
		}
		
		Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource()
				.getAttributes(object);
		
		if (attributes == null || attributes.isEmpty()) {
			if (rejectPublicInvocations) {
				throw new IllegalArgumentException(
						"Secure object invocation "
								+ object
								+ " was denied as public invocations are not allowed via this interceptor. "
								+ "This indicates a configuration error because the "
								+ "rejectPublicInvocations property is set to 'true'");
			}

			if (debug) {
				logger.debug("Public object - authentication not attempted");
			}

			//因为如果找不到路径的话会出问题
			SpringMvcHolder.getRequest().setAttribute("NoMapperAttr", Boolean.TRUE);
			//publishEvent(new PublicInvocationEvent(object));

			//return null; // no further work post-invocation
			return;
		}
		
		if (debug) {
			logger.debug("Secure object: " + object + "; Attributes: " + attributes);
		}
		
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			credentialsNotFound(messages.getMessage(
					"AbstractSecurityInterceptor.authenticationNotFound",
					"An Authentication object was not found in the SecurityContext"),
					object, attributes);
		}
		
		Authentication authenticated = authenticateIfRequired();
		
		// Attempt authorization
		try {
			this.accessDecisionManager.decide(authenticated, object, attributes);
		}
		catch (AccessDeniedException accessDeniedException) {
			publishEvent(new AuthorizationFailureEvent(object, attributes, authenticated,
					accessDeniedException));

			throw accessDeniedException;
		}

		if (debug) {
			logger.debug("Authorization successful");
		}

		if (publishAuthorizationSuccess) {
			publishEvent(new AuthorizedEvent(object, attributes, authenticated));
		}

		
	}
	
	private Authentication authenticateIfRequired() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		if (authentication.isAuthenticated() && !alwaysReauthenticate) {
			if (logger.isDebugEnabled()) {
				logger.debug("Previously Authenticated: " + authentication);
			}

			return authentication;
		}

		authentication = authenticationManager.authenticate(authentication);

		// We don't authenticated.setAuthentication(true), because each provider should do
		// that
		if (logger.isDebugEnabled()) {
			logger.debug("Successfully Authenticated: " + authentication);
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return authentication;
	}
	
	/**
	 * Helper method which generates an exception containing the passed reason, and
	 * publishes an event to the application context.
	 * <p>
	 * Always throws an exception.
	 *
	 * @param reason to be provided in the exception detail
	 * @param secureObject that was being called
	 * @param configAttribs that were defined for the secureObject
	 */
	private void credentialsNotFound(String reason, Object secureObject,
			Collection<ConfigAttribute> configAttribs) {
		AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException(
				reason);

		AuthenticationCredentialsNotFoundEvent event = new AuthenticationCredentialsNotFoundEvent(
				secureObject, configAttribs, exception);
		publishEvent(event);

		throw exception;
	}
	
	
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
	

	public void setAuthenticationManager(AuthenticationManager newManager) {
		this.authenticationManager = newManager;
	}
	
	/**
	 * Indicates the type of secure objects the subclass will be presenting to the
	 * abstract parent for processing. This is used to ensure collaborators wired to the
	 * {@code AbstractSecurityInterceptor} all support the indicated secure object class.
	 *
	 * @return the type of secure object the subclass provides services for
	 */
	public abstract Class<?> getSecureObjectClass();
	
	public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
		this.accessDecisionManager = accessDecisionManager;
	}
	
	/**
	 * 把请求转换为所需权限
	 * @return
	 */
	public abstract SecurityMetadataSource obtainSecurityMetadataSource();
	
	private void publishEvent(ApplicationEvent event) {
		if (this.eventPublisher != null) {
			this.eventPublisher.publishEvent(event);
		}
	}
	
	/**
	 * Only {@code AuthorizationFailureEvent} will be published. If you set this property
	 * to {@code true}, {@code AuthorizedEvent}s will also be published.
	 *
	 * @param publishAuthorizationSuccess default value is {@code false}
	 */
	public void setPublishAuthorizationSuccess(boolean publishAuthorizationSuccess) {
		this.publishAuthorizationSuccess = publishAuthorizationSuccess;
	}
}
