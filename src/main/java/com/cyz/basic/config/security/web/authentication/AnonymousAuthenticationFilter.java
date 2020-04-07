package com.cyz.basic.config.security.web.authentication;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import com.cyz.basic.config.security.authentication.AnonymousAuthenticationToken;
import com.cyz.basic.config.security.authentication.AuthenticationDetailsSource;
import com.cyz.basic.config.security.authentication.WebAuthenticationDetailsSource;
import com.cyz.basic.config.security.authority.AuthorityUtils;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.core.context.CyzSecurityContextHolder;
import com.cyz.basic.config.security.detail.GrantedAuthority;

public class AnonymousAuthenticationFilter extends GenericFilterBean implements InitializingBean {
	
	
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private String key;
	private Object principal;
	private List<GrantedAuthority> authorities;

	/**
	 * Creates a filter with a principal named "anonymousUser" and the single authority
	 * "ROLE_ANONYMOUS".
	 *
	 * @param key the key to identify tokens created by this filter
	 */
	public AnonymousAuthenticationFilter(String key) {
		this(key, "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
	}
	
	/**
	 *
	 * @param key key the key to identify tokens created by this filter
	 * @param principal the principal which will be used to represent anonymous users
	 * @param authorities the authority list for anonymous users
	 */
	public AnonymousAuthenticationFilter(String key, Object principal,
			List<GrantedAuthority> authorities) {
		Assert.hasLength(key, "key cannot be null or empty");
		Assert.notNull(principal, "Anonymous authentication principal must be set");
		Assert.notNull(authorities, "Anonymous authorities must be set");
		this.key = key;
		this.principal = principal;
		this.authorities = authorities;
	}
	
	@Override
	public void afterPropertiesSet() {
		Assert.hasLength(key, "key must have length");
		Assert.notNull(principal, "Anonymous authentication principal must be set");
		Assert.notNull(authorities, "Anonymous authorities must be set");
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		if (CyzSecurityContextHolder.getContext().getAuthentication() == null) {
			CyzSecurityContextHolder.getContext().setAuthentication(
					createAuthentication((HttpServletRequest) req));

			if (logger.isDebugEnabled()) {
				logger.debug("Populated SecurityContextHolder with anonymous token: '"
						+ CyzSecurityContextHolder.getContext().getAuthentication() + "'");
			}
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("SecurityContextHolder not populated with anonymous token, as it already contained: '"
						+ CyzSecurityContextHolder.getContext().getAuthentication() + "'");
			}
		}

		chain.doFilter(req, res);

	}
	
	protected Authentication createAuthentication(HttpServletRequest request) {
		AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(key,
				principal, authorities);
		auth.setDetails(authenticationDetailsSource.buildDetails(request));

		return auth;
	}
	
	public void setAuthenticationDetailsSource(
			AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
		Assert.notNull(authenticationDetailsSource,
				"AuthenticationDetailsSource required");
		this.authenticationDetailsSource = authenticationDetailsSource;
	}
	
	public Object getPrincipal() {
		return principal;
	}

	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}

}
