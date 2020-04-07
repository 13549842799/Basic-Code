package com.cyz.basic.config.security.access.vote;

import java.util.Collection;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.access.AccessDecisionVoter;
import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.authentication.AuthenticationTrustResolver;
import com.cyz.basic.config.security.authentication.AuthenticationTrustResolverImpl;
import com.cyz.basic.config.security.core.Authentication;

public class AuthenticatedVoter implements AccessDecisionVoter<Object> {

	public static final String IS_AUTHENTICATED_FULLY = "IS_AUTHENTICATED_FULLY";
	public static final String IS_AUTHENTICATED_REMEMBERED = "IS_AUTHENTICATED_REMEMBERED";
	public static final String IS_AUTHENTICATED_ANONYMOUSLY = "IS_AUTHENTICATED_ANONYMOUSLY";
	
	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
	
	private boolean isFullyAuthenticated(Authentication authentication) {
		return (!authenticationTrustResolver.isAnonymous(authentication) && !authenticationTrustResolver
				.isRememberMe(authentication));
	}

	public void setAuthenticationTrustResolver(
			AuthenticationTrustResolver authenticationTrustResolver) {
		Assert.notNull(authenticationTrustResolver,
				"AuthenticationTrustResolver cannot be set to null");
		this.authenticationTrustResolver = authenticationTrustResolver;
	}

	public boolean supports(ConfigAttribute attribute) {
		if ((attribute.getAttribute() != null)
				&& (IS_AUTHENTICATED_FULLY.equals(attribute.getAttribute())
						|| IS_AUTHENTICATED_REMEMBERED.equals(attribute.getAttribute()) || IS_AUTHENTICATED_ANONYMOUSLY
							.equals(attribute.getAttribute()))) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * This implementation supports any type of class, because it does not query the
	 * presented secure object.
	 *
	 * @param clazz the secure object type
	 *
	 * @return always {@code true}
	 */
	public boolean supports(Class<?> clazz) {
		return true;
	}

	public int vote(Authentication authentication, Object object,
			Collection<ConfigAttribute> attributes) {
		int result = ACCESS_ABSTAIN;

		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {
				result = ACCESS_DENIED;

				if (IS_AUTHENTICATED_FULLY.equals(attribute.getAttribute())) {
					if (isFullyAuthenticated(authentication)) {
						return ACCESS_GRANTED;
					}
				}

				if (IS_AUTHENTICATED_REMEMBERED.equals(attribute.getAttribute())) {
					if (authenticationTrustResolver.isRememberMe(authentication)
							|| isFullyAuthenticated(authentication)) {
						return ACCESS_GRANTED;
					}
				}

				if (IS_AUTHENTICATED_ANONYMOUSLY.equals(attribute.getAttribute())) {
					if (authenticationTrustResolver.isAnonymous(authentication)
							|| isFullyAuthenticated(authentication)
							|| authenticationTrustResolver.isRememberMe(authentication)) {
						return ACCESS_GRANTED;
					}
				}
			}
		}

		return result;
	}
}
