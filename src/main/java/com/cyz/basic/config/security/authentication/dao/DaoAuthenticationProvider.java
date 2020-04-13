package com.cyz.basic.config.security.authentication.dao;

import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.authentication.UsernamePasswordAuthenticationToken;
import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;
import com.cyz.basic.config.security.crypto.factory.PasswordEncoderFactories;
import com.cyz.basic.config.security.crypto.password.PasswordEncoder;
import com.cyz.basic.config.security.exception.AuthenticationException;
import com.cyz.basic.config.security.exception.BadCredentialsException;

public class DaoAuthenticationProvider extends CustomSecurityProvider {
	
	/**
	 * The plaintext password used to perform
	 * PasswordEncoder#matches(CharSequence, String)}  on when the user is
	 * not found to avoid SEC-2056.
	 */
	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

	// ~ Instance fields
	// ================================================================================================

	private PasswordEncoder passwordEncoder;
	
	/**
	 * The password used to perform
	 * {@link PasswordEncoder#matches(CharSequence, String)} on when the user is
	 * not found to avoid SEC-2056. This is necessary, because some
	 * {@link PasswordEncoder} implementations will short circuit if the password is not
	 * in a valid format.
	 */
	private volatile String userNotFoundEncodedPassword;
	
	/**
	 * 限制一定时间内登录次数
	 */
	private boolean limitMaxCount = false;
	
	protected MaxLoginCount maxLoginCount = new MaxLoginCount();

	private UserDetailsService userDetailsService;

	public DaoAuthenticationProvider() {
		setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}
		
		if (limitMaxCount) {
			int count = getCurrentLoginCount(authentication);
			if (count >= maxLoginCount.getLoginMaxCount()) {
				throw new BadCredentialsException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.overload",
						"Wrong password"));
			}
			count++;
			updateLoginCount(count, authentication);
		}
		
		String presentedPassword = authentication.getCredentials().toString();
		if (!getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");
			
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}
	}
	
	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}
	
	/**
	 * 获取当前的登录次数
	 * @param authentication
	 * @return
	 */
	public int getCurrentLoginCount(UsernamePasswordAuthenticationToken authentication) {
		return 0;
	}
	
	/**
	 * 更新登录次数
	 */
	public void updateLoginCount(int laststCount, UsernamePasswordAuthenticationToken authentication) {
		
	}
	
	
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
		this.passwordEncoder = passwordEncoder;
		this.userNotFoundEncodedPassword = null;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public boolean isLimitMaxCount() {
		return limitMaxCount;
	}

	public void setLimitMaxCount(boolean limitMaxCount) {
		this.limitMaxCount = limitMaxCount;
	}

	/**
	 * 
	 * @author cyz
	 *
	 */
	public static class MaxLoginCount {
		
		private final static int DEF_MAX_LOGIN_COUNT = 5;
		
		private int loginMaxCount = DEF_MAX_LOGIN_COUNT;
		
		private long expire = 0;
		
		private TimeUnit unit = TimeUnit.MINUTES;
		
		public MaxLoginCount() {}

		public MaxLoginCount(int loginMaxCount, long expire, TimeUnit unit) {
			this.loginMaxCount = loginMaxCount;
			this.expire = expire;
			this.unit = unit;
		}

		public int getLoginMaxCount() {
			return loginMaxCount;
		}

		public void setLoginMaxCount(int loginMaxCount) {
			this.loginMaxCount = loginMaxCount;
		}

		public long getExpire() {
			return expire;
		}

		public void setExpire(long expire) {
			this.expire = expire;
		}

		public TimeUnit getUnit() {
			return unit;
		}

		public void setUnit(TimeUnit unit) {
			this.unit = unit;
		}
		
		
	}

}
