package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.List;
import java.util.UUID;

import com.cyz.basic.config.security.authentication.AnonymousAuthenticationProvider;
import com.cyz.basic.config.security.authentication.AuthenticationProvider;
import com.cyz.basic.config.security.authority.AuthorityUtils;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.detail.GrantedAuthority;
import com.cyz.basic.config.security.web.authentication.AnonymousAuthenticationFilter;
import com.cyz.basic.constant.EntityConstants;

public class AnonymousConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<AnonymousConfigurer<H>, H> {
	
	private String key;
	private AuthenticationProvider authenticationProvider;
	private AnonymousAuthenticationFilter authenticationFilter;
	private Object principal = EntityConstants.SESSION.ANONYMOUS;
	private List<GrantedAuthority> authorities = AuthorityUtils
			.createAuthorityList("ROLE_ANONYMOUS");
	
	/**
	 * Creates a new instance
	 * @see HttpSecurity#anonymous()
	 */
	public AnonymousConfigurer() {
	}

	/**
	 * Sets the key to identify tokens created for anonymous authentication. Default is a
	 * secure randomly generated key.
	 *
	 * @param key the key to identify tokens created for anonymous authentication. Default
	 * is a secure randomly generated key.
	 * @return the {@link AnonymousConfigurer} for further customization of anonymous
	 * authentication
	 */
	public AnonymousConfigurer<H> key(String key) {
		this.key = key;
		return this;
	}
	
	/**
	 * Sets the principal for {@link Authentication} objects of anonymous users
	 *
	 * @param principal used for the {@link Authentication} object of anonymous users
	 * @return the {@link AnonymousConfigurer} for further customization of anonymous
	 * authentication
	 */
	public AnonymousConfigurer<H> principal(Object principal) {
		this.principal = principal;
		return this;
	}

	/**
	 * Sets the {@link org.springframework.security.core.Authentication#getAuthorities()}
	 * for anonymous users
	 *
	 * @param authorities Sets the
	 * {@link org.springframework.security.core.Authentication#getAuthorities()} for
	 * anonymous users
	 * @return the {@link AnonymousConfigurer} for further customization of anonymous
	 * authentication
	 */
	public AnonymousConfigurer<H> authorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
		return this;
	}

	/**
	 * Sets the {@link org.springframework.security.core.Authentication#getAuthorities()}
	 * for anonymous users
	 *
	 * @param authorities Sets the
	 * {@link org.springframework.security.core.Authentication#getAuthorities()} for
	 * anonymous users (i.e. "ROLE_ANONYMOUS")
	 * @return the {@link AnonymousConfigurer} for further customization of anonymous
	 * authentication
	 */
	public AnonymousConfigurer<H> authorities(String... authorities) {
		return authorities(AuthorityUtils.createAuthorityList(authorities));
	}
	
	/**
	 * Sets the {@link AuthenticationProvider} used to validate an anonymous user. If this
	 * is set, no attributes on the {@link AnonymousConfigurer} will be set on the
	 * {@link AuthenticationProvider}.
	 *
	 * @param authenticationProvider the {@link AuthenticationProvider} used to validate
	 * an anonymous user. Default is {@link AnonymousAuthenticationProvider}
	 *
	 * @return the {@link AnonymousConfigurer} for further customization of anonymous
	 * authentication
	 */
	public AnonymousConfigurer<H> authenticationProvider(
			AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
		return this;
	}

	/**
	 * Sets the {@link AnonymousAuthenticationFilter} used to populate an anonymous user.
	 * If this is set, no attributes on the {@link AnonymousConfigurer} will be set on the
	 * {@link AnonymousAuthenticationFilter}.
	 *
	 * @param authenticationFilter the {@link AnonymousAuthenticationFilter} used to
	 * populate an anonymous user.
	 *
	 * @return the {@link AnonymousConfigurer} for further customization of anonymous
	 * authentication
	 */
	public AnonymousConfigurer<H> authenticationFilter(
			AnonymousAuthenticationFilter authenticationFilter) {
		this.authenticationFilter = authenticationFilter;
		return this;
	}

	@Override
	public void init(H http) throws Exception {
		if (authenticationProvider == null) {
			authenticationProvider = new AnonymousAuthenticationProvider(getKey());
		}
		if (authenticationFilter == null) {
			authenticationFilter = new AnonymousAuthenticationFilter(getKey(), principal,
					authorities);
		}
		authenticationProvider = postProcess(authenticationProvider);
		http.authenticationProvider(authenticationProvider);
	}

	@Override
	public void configure(H http) throws Exception {
		authenticationFilter.afterPropertiesSet();
		http.addFilter(authenticationFilter);
	}

	private String getKey() {
		if (key == null) {
			key = UUID.randomUUID().toString();
		}
		return key;
	}

}
