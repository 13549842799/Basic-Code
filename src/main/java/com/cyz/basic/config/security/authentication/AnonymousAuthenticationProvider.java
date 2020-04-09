package com.cyz.basic.config.security.authentication;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.exception.AuthenticationException;
import com.cyz.basic.config.security.exception.BadCredentialsException;

public class AnonymousAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

	// ~ Instance fields
	// ================================================================================================

	protected MessageSourceAccessor messages = null;
	private String key;

	public AnonymousAuthenticationProvider(String key) {
		Assert.hasLength(key, "A Key is required");
		this.key = key;
	}

	// ~ Methods
	// ========================================================================================================

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if (!supports(authentication.getClass())) {
			return null;
		}

		if (this.key.hashCode() != ((AnonymousAuthenticationToken) authentication)
				.getKeyHash()) {
			throw new BadCredentialsException(
					messages.getMessage("AnonymousAuthenticationProvider.incorrectKey",
							"The presented AnonymousAuthenticationToken does not contain the expected key"));
		}

		return authentication;
	}

	public String getKey() {
		return key;
	}

	public void setMessageSource(MessageSource messageSource) {
		Assert.notNull(messageSource, "messageSource cannot be null");
		this.messages = new MessageSourceAccessor(messageSource);
	}

	public boolean supports(Class<?> authentication) {
		return (AnonymousAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
