package com.cyz.basic.config.security.authentication;

import com.cyz.basic.config.security.core.Authentication;

public class AuthenticationTrustResolverImpl implements AuthenticationTrustResolver {

	/*private Class<? extends Authentication> anonymousClass = AnonymousAuthenticationToken.class;
	private Class<? extends Authentication> rememberMeClass = RememberMeAuthenticationToken.class;*/
	
	private Class<? extends Authentication> anonymousClass = null;
	private Class<? extends Authentication> rememberMeClass = null;

	// ~ Methods
	// ========================================================================================================

	Class<? extends Authentication> getAnonymousClass() {
		return anonymousClass;
	}

	Class<? extends Authentication> getRememberMeClass() {
		return rememberMeClass;
	}

	public boolean isAnonymous(Authentication authentication) {
		if ((anonymousClass == null) || (authentication == null)) {
			return false;
		}

		return anonymousClass.isAssignableFrom(authentication.getClass());
	}

	public boolean isRememberMe(Authentication authentication) {
		if ((rememberMeClass == null) || (authentication == null)) {
			return false;
		}

		return rememberMeClass.isAssignableFrom(authentication.getClass());
	}

	public void setAnonymousClass(Class<? extends Authentication> anonymousClass) {
		this.anonymousClass = anonymousClass;
	}

	public void setRememberMeClass(Class<? extends Authentication> rememberMeClass) {
		this.rememberMeClass = rememberMeClass;
	}

}
