package com.cyz.basic.config.security.config.annotation.authentication.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.authentication.AuthenticationEventPublisher;
import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.authentication.ProviderManager;
import com.cyz.basic.config.security.config.annotation.AbstractConfiguredSecurityBuilder;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.authentication.ProviderManagerBuilder;
import com.cyz.basic.config.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import com.cyz.basic.config.security.config.annotation.authentication.configurers.userdetails.UserDetailsAwareConfigurer;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;

/**
 * {@link SecurityBuilder} used to create an {@link AuthenticationManager}. Allows for
 * easily building in memory authentication, LDAP authentication, JDBC based
 * authentication, adding {@link UserDetailsService}, and adding
 * {@link AuthenticationProvider}'s.
 *
 * @author Rob Winch
 * @since 3.2
 */
public class AuthenticationManagerBuilder extends
    AbstractConfiguredSecurityBuilder<AuthenticationManager, AuthenticationManagerBuilder>
    implements ProviderManagerBuilder<AuthenticationManagerBuilder>{
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private AuthenticationManager parentAuthenticationManager;
	
	private List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
	
	private UserDetailsService defaultUserDetailsService;
	private Boolean eraseCredentials;
	private AuthenticationEventPublisher eventPublisher;
	
	/**
	 * Creates a new instance
	 * @param objectPostProcessor the {@link ObjectPostProcessor} instance to use.
	 */
	public AuthenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
		super(objectPostProcessor, true);
	}
	
	/**
	 * Allows providing a parent {@link AuthenticationManager} that will be tried if this
	 * {@link AuthenticationManager} was unable to attempt to authenticate the provided
	 * {@link Authentication}.
	 *
	 * @param authenticationManager the {@link AuthenticationManager} that should be used
	 * if the current {@link AuthenticationManager} was unable to attempt to authenticate
	 * the provided {@link Authentication}.
	 * @return the {@link AuthenticationManagerBuilder} for further adding types of
	 * authentication
	 */
	public AuthenticationManagerBuilder parentAuthenticationManager(
			AuthenticationManager authenticationManager) {
		if (authenticationManager instanceof ProviderManager) {
			eraseCredentials(((ProviderManager) authenticationManager)
					.isEraseCredentialsAfterAuthentication());
		}
		this.parentAuthenticationManager = authenticationManager;
		return this;
	}
	
	/**
	 * Sets the {@link AuthenticationEventPublisher}
	 *
	 * @param eventPublisher the {@link AuthenticationEventPublisher} to use
	 * @return the {@link AuthenticationManagerBuilder} for further customizations
	 */
	public AuthenticationManagerBuilder authenticationEventPublisher(
			AuthenticationEventPublisher eventPublisher) {
		Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
		this.eventPublisher = eventPublisher;
		return this;
	}
	
	/**
	 *
	 *
	 * @param eraseCredentials true if {@link AuthenticationManager} should clear the
	 * credentials from the {@link Authentication} object after authenticating
	 * @return the {@link AuthenticationManagerBuilder} for further customizations
	 */
	public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
		this.eraseCredentials = eraseCredentials;
		return this;
	}
	
	/*public JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> MysqlAuthentication()
			throws Exception {
		return apply(new JdbcUserDetailsManagerConfigurer<>());
	}*/
	
	/**
	 * Add authentication based upon the custom {@link UserDetailsService} that is passed
	 * in. It then returns a {@link DaoAuthenticationConfigurer} to allow customization of
	 * the authentication.
	 *
	 * <p>
	 * This method also ensure that the {@link UserDetailsService} is available for the
	 * {@link #getDefaultUserDetailsService()} method. Note that additional
	 * {@link UserDetailsService}'s may override this {@link UserDetailsService} as the
	 * default.
	 * </p>
	 *
	 * @return a {@link DaoAuthenticationConfigurer} to allow customization of the DAO
	 * authentication
	 * @throws Exception if an error occurs when adding the {@link UserDetailsService}
	 * based authentication
	 */
	/*public <T extends UserDetailsService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> userDetailsService(
			T userDetailsService) throws Exception {
		this.defaultUserDetailsService = userDetailsService;
		return apply(new DaoAuthenticationConfigurer<>(
				userDetailsService));
		return null;
	}*/
	
	
	
	
	public <T extends UserDetailsService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> userDetailsService(
			T userDetailsService) throws Exception {
		this.defaultUserDetailsService = userDetailsService;
		return apply(new DaoAuthenticationConfigurer<>(
				userDetailsService));
	}
	
	/**
	 * Add authentication based upon the custom {@link AuthenticationProvider} that is
	 * passed in. Since the {@link AuthenticationProvider} implementation is unknown, all
	 * customizations must be done externally and the {@link AuthenticationManagerBuilder}
	 * is returned immediately.
	 *
	 * <p>
	 * This method <b>does NOT</b> ensure that the {@link UserDetailsService} is available
	 * for the {@link #getDefaultUserDetailsService()} method.
	 *
	 * Note that an {@link Exception} might be thrown if an error occurs when adding the {@link AuthenticationProvider}.
	 *
	 * @return a {@link AuthenticationManagerBuilder} to allow further authentication to
	 * be provided to the {@link AuthenticationManagerBuilder}
	 */
	@Override
	public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProviders.add(authenticationProvider);
		return this;
	}
	
	@Override
	protected AuthenticationManager performBuild() throws Exception {
		if (!isConfigured()) {
			logger.debug("No authenticationProviders and no parentAuthenticationManager defined. Returning null.");
			return null;
		}
		ProviderManager providerManager = new ProviderManager(authenticationProviders,
				parentAuthenticationManager);
		if (eraseCredentials != null) {
			providerManager.setEraseCredentialsAfterAuthentication(eraseCredentials);
		}
		if (eventPublisher != null) {
			providerManager.setAuthenticationEventPublisher(eventPublisher);
		}
		providerManager = postProcess(providerManager);
		return providerManager;
	}
	
	/**
	 * Determines if the {@link AuthenticationManagerBuilder} is configured to build a non
	 * null {@link AuthenticationManager}. This means that either a non-null parent is
	 * specified or at least one {@link AuthenticationProvider} has been specified.
	 *
	 * <p>
	 * When using {@link SecurityConfigurer} instances, the
	 * {@link AuthenticationManagerBuilder} will not be configured until the
	 * {@link SecurityConfigurer#configure(SecurityBuilder)} methods. This means a
	 * {@link SecurityConfigurer} that is last could check this method and provide a
	 * default configuration in the {@link SecurityConfigurer#configure(SecurityBuilder)}
	 * method.
	 *
	 * @return
	 */
	public boolean isConfigured() {
		return !authenticationProviders.isEmpty() || parentAuthenticationManager != null;
	}
	
	/**
	 * Gets the default {@link UserDetailsService} for the
	 * {@link AuthenticationManagerBuilder}. The result may be null in some circumstances.
	 *
	 * @return the default {@link UserDetailsService} for the
	 * {@link AuthenticationManagerBuilder}
	 */
	public UserDetailsService getDefaultUserDetailsService() {
		return this.defaultUserDetailsService;
	}
	
	/**
	 * Captures the {@link UserDetailsService} from any {@link UserDetailsAwareConfigurer}
	 * .
	 *
	 * @param configurer the {@link UserDetailsAwareConfigurer} to capture the
	 * {@link UserDetailsService} from.
	 * @return the {@link UserDetailsAwareConfigurer} for further customizations
	 * @throws Exception if an error occurs
	 */
	private <C extends UserDetailsAwareConfigurer<AuthenticationManagerBuilder, ? extends UserDetailsService>> C apply(
			C configurer) throws Exception {
		this.defaultUserDetailsService = configurer.getUserDetailsService();
		return (C) super.apply(configurer);
	}

}
