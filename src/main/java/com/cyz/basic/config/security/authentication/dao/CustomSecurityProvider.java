package com.cyz.basic.config.security.authentication.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;

import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.authentication.UsernamePasswordAuthenticationToken;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.config.security.crypto.password.PasswordEncoder;
import com.cyz.basic.config.security.detail.UserDetailsChecker;
import com.cyz.basic.config.security.exception.AuthenticationException;
import com.cyz.basic.config.security.exception.DisabledException;
import com.cyz.basic.config.security.exception.LockedException;
import com.cyz.basic.config.security.exception.UsernameNotFoundException;
import com.cyz.basic.config.security.service.UserDetailServiceSupport;

public abstract class CustomSecurityProvider implements AuthenticationProvider, InitializingBean{
    
protected final Log logger = LogFactory.getLog(getClass());
	
	//protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    protected MessageSourceAccessor messages = null;
	
	private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
	
	private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
	
	protected UserDetailServiceSupport detailsService;
	
	private PasswordEncoder passwordEncoder;

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	protected void doAfterPropertiesSet() throws Exception {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
				: authentication.getName();

		UserDetails user = detailsService.loadUserByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("");
		}
		
		try {
			preAuthenticationChecks.check(user);
			additionalAuthenticationChecks(user,
					(UsernamePasswordAuthenticationToken) authentication);
		}
		catch (AuthenticationException exception) {			
			throw exception;
		}
		
		postAuthenticationChecks.check(user);
		
		return createSuccessAuthentication(user.getUsername(), authentication, user);
	}
	
	protected Authentication createSuccessAuthentication(Object principal,
			Authentication authentication, UserDetails user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				principal, authentication.getCredentials(),
				user.getAuthorities());
		result.setDetails(authentication.getDetails());
		return result;
	}
	

	@Override
	public boolean supports(Class<?> authentication) {
		System.out.println(authentication.getName());
		return true;
	}


	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setDetailsService(UserDetailServiceSupport detailsService) {
		this.detailsService = detailsService;
	}
	
	protected abstract void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException;

	private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
		public void check(UserDetails user) {
			if (!user.isAccountNonLocked()) {
				logger.debug("User account is locked");

				throw new LockedException("");
			}

			if (!user.isEnabled()) {
				logger.debug("User account is disabled");

				throw new DisabledException("AbstractUserDetailsAuthenticationProvider.disabled,User is disabled");
			}

			if (!user.isAccountNonExpired()) {
				logger.debug("User account is expired");

				throw new AccountExpiredException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.expired",
						"User account has expired"));
			}
		}
	}

	private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
		public void check(UserDetails user) {
			if (!user.isCredentialsNonExpired()) {
				logger.debug("User account credentials have expired");

				throw new CredentialsExpiredException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.credentialsExpired",
						"User credentials have expired"));
			}
		}
	}
}
