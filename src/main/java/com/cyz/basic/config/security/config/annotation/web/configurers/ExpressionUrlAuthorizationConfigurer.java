package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import com.cyz.basic.config.security.access.AccessDecisionVoter;
import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.access.expression.DefaultBaseMetadataSource;
import com.cyz.basic.config.security.web.servlet.util.matcher.MvcRequestMatcher;
import com.cyz.basic.config.security.web.util.matcher.RequestMatcher;

/**
 * Adds URL based authorization based upon SpEL expressions to an application. At least
 * one {@link org.springframework.web.bind.annotation.RequestMapping} needs to be mapped
 * to {@link ConfigAttribute}'s for this {@link SecurityContextConfigurer} to have
 * meaning. <h2>Security Filters</h2>
 *
 * The following Filters are populated
 *
 * <ul>
 * <li>{@link com.cyz.basic.config.security.web.access.intercept.security.web.access.intercept.FilterSecurityInterceptor}
 * </li>
 * </ul>
 *
 * <h2>Shared Objects Created</h2>
 *
 * The following shared objects are populated to allow other
 * {@link org.springframework.security.config.annotation.SecurityConfigurer}'s to
 * customize:
 * <ul>
 * <li>{@link com.cyz.basic.config.security.web.access.intercept.security.web.access.intercept.FilterSecurityInterceptor}
 * </li>
 * </ul>
 *
 * <h2>Shared Objects Used</h2>
 *
 * <ul>
 * <li>{@link AuthenticationTrustResolver} is optionally used to populate the
 * {@link DefaultWebSecurityExpressionHandler}</li>
 * </ul>
 *
 * @param <H> the type of {@link HttpSecurityBuilder} that is being configured
 *
 * @author Rob Winch
 * @since 3.2
 * @see org.springframework.security.config.annotation.web.builders.HttpSecurity#authorizeRequests()
 * 
 * 
 * 权限管理配置 ，其中的一个功能是生成SpringSecurityFilter
 */
public final class ExpressionUrlAuthorizationConfigurer<H extends HttpSecurityBuilder<H>>
    extends AbstractInterceptUrlConfigurer<ExpressionUrlAuthorizationConfigurer<H>, H>{
	
	static final String permitAll = "permitAll";
	private static final String denyAll = "denyAll";
	private static final String anonymous = "anonymous";
	private static final String authenticated = "authenticated";
	private static final String fullyAuthenticated = "fullyAuthenticated";
	private static final String rememberMe = "rememberMe";
	
	private final ExpressionInterceptUrlRegistry REGISTRY;
	
	/**
	 * Creates a new instance
	 * @see HttpSecurity#authorizeRequests()
	 */
	public ExpressionUrlAuthorizationConfigurer(ApplicationContext context) {
		this.REGISTRY = new ExpressionInterceptUrlRegistry(context);
	}

	public ExpressionInterceptUrlRegistry getRegistry() {
		return REGISTRY;
	}
	
	
	public class ExpressionInterceptUrlRegistry extends ExpressionUrlAuthorizationConfigurer<H>.AbstractInterceptUrlRegistry<ExpressionInterceptUrlRegistry, AuthorizedUrl> {

		/**
		 * @param context
		 */
		private ExpressionInterceptUrlRegistry(ApplicationContext context) {
			setApplicationContext(context);
		}
		
		/*@Override
		public MvcMatchersAuthorizedUrl mvcMatchers(HttpMethod method, String... mvcPatterns) {
			return new MvcMatchersAuthorizedUrl(createMvcMatchers(method, mvcPatterns));
		}
		
		@Override
		public MvcMatchersAuthorizedUrl mvcMatchers(String... patterns) {
			return mvcMatchers(null, patterns);
		}*/
		
		@Override
		protected final AuthorizedUrl chainRequestMatchersInternal(
				List<RequestMatcher> requestMatchers) {
			return new AuthorizedUrl(requestMatchers);
		}
		
		/**
		 * Allows customization of the {@link SecurityExpressionHandler} to be used. The
		 * default is {@link DefaultWebSecurityExpressionHandler}
		 *
		 * @param expressionHandler the {@link SecurityExpressionHandler} to be used
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization.
		 */
		/*public ExpressionInterceptUrlRegistry expressionHandler(
				SecurityExpressionHandler<FilterInvocation> expressionHandler) {
			ExpressionUrlAuthorizationConfigurer.this.expressionHandler = expressionHandler;
			return this;
		}*/
		
		/**
		 * Adds an {@link ObjectPostProcessor} for this class.
		 *
		 * @param objectPostProcessor
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customizations
		 */
		public ExpressionInterceptUrlRegistry withObjectPostProcessor(
				ObjectPostProcessor<?> objectPostProcessor) {
			addObjectPostProcessor(objectPostProcessor);
			return this;
		}
		
		public H and() {
			return ExpressionUrlAuthorizationConfigurer.this.and();
		}
	
	}
	
	
	@Override
	final DefaultBaseMetadataSource createMetadataSource(
			H http) {
		LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = REGISTRY
				.createRequestMap();
		if (requestMap.isEmpty()) {
			throw new IllegalStateException(
					"At least one mapping is required (i.e. authorizeRequests().anyRequest().authenticated())");
		}
		return new DefaultBaseMetadataSource(requestMap);
	}
	
	
	
	/**
	 * An {@link AuthorizedUrl} that allows optionally configuring the
	 * {@link MvcRequestMatcher#setMethod(HttpMethod)}
	 *
	 * @author Rob Winch
	 */
	public class MvcMatchersAuthorizedUrl extends AuthorizedUrl {
		/**
		 * Creates a new instance
		 *
		 * @param requestMatchers the {@link RequestMatcher} instances to map
		 */
		private MvcMatchersAuthorizedUrl(List<MvcRequestMatcher> requestMatchers) {
			super(requestMatchers);
		}

		public AuthorizedUrl servletPath(String servletPath) {
			for (MvcRequestMatcher matcher : (List<MvcRequestMatcher>) getMatchers()) {
				matcher.setServletPath(servletPath);
			}
			return this;
		}
	}
	
	
	public class AuthorizedUrl {
		private List<? extends RequestMatcher> requestMatchers;
		private boolean not;

		/**
		 * Creates a new instance
		 *
		 * @param requestMatchers the {@link RequestMatcher} instances to map
		 */
		private AuthorizedUrl(List<? extends RequestMatcher> requestMatchers) {
			this.requestMatchers = requestMatchers;
		}

		protected List<? extends RequestMatcher> getMatchers() {
			return this.requestMatchers;
		}

		/**
		 * Negates the following expression.
		 *
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		public AuthorizedUrl not() {
			this.not = true;
			return this;
		}

		/**
		 * Shortcut for specifying URLs require a particular role. If you do not want to
		 * have "ROLE_" automatically inserted see {@link #hasAuthority(String)}.
		 *
		 * @param role the role to require (i.e. USER, ADMIN, etc). Note, it should not
		 * start with "ROLE_" as this is automatically inserted.
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		/*public ExpressionInterceptUrlRegistry hasRole(String role) {
			return access(ExpressionUrlAuthorizationConfigurer.hasRole(role));
		}*/

		/**
		 * Shortcut for specifying URLs require any of a number of roles. If you do not
		 * want to have "ROLE_" automatically inserted see
		 * {@link #hasAnyAuthority(String...)}
		 *
		 * @param roles the roles to require (i.e. USER, ADMIN, etc). Note, it should not
		 * start with "ROLE_" as this is automatically inserted.
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		/*public ExpressionInterceptUrlRegistry hasAnyRole(String... roles) {
			return access(ExpressionUrlAuthorizationConfigurer.hasAnyRole(roles));
		}*/

		/**
		 * Specify that URLs require a particular authority.
		 *
		 * @param authority the authority to require (i.e. ROLE_USER, ROLE_ADMIN, etc).
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		/*public ExpressionInterceptUrlRegistry hasAuthority(String authority) {
			return access(ExpressionUrlAuthorizationConfigurer.hasAuthority(authority));
		}*/

		/**
		 * Specify that URLs requires any of a number authorities.
		 *
		 * @param authorities the requests require at least one of the authorities (i.e.
		 * "ROLE_USER","ROLE_ADMIN" would mean either "ROLE_USER" or "ROLE_ADMIN" is
		 * required).
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		/*public ExpressionInterceptUrlRegistry hasAnyAuthority(String... authorities) {
			return access(ExpressionUrlAuthorizationConfigurer
					.hasAnyAuthority(authorities));
		}*/

		/**
		 * Specify that URLs requires a specific IP Address or <a href=
		 * "http://forum.springsource.org/showthread.php?102783-How-to-use-hasIpAddress&p=343971#post343971"
		 * >subnet</a>.
		 *
		 * @param ipaddressExpression the ipaddress (i.e. 192.168.1.79) or local subnet
		 * (i.e. 192.168.0/24)
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		/*public ExpressionInterceptUrlRegistry hasIpAddress(String ipaddressExpression) {
			return access(ExpressionUrlAuthorizationConfigurer
					.hasIpAddress(ipaddressExpression));
		}*/

		/**
		 * Specify that URLs are allowed by anyone.
		 *
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		public ExpressionInterceptUrlRegistry permitAll() {
			return access(permitAll);
		}

		/**
		 * Specify that URLs are allowed by anonymous users.
		 *
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		public ExpressionInterceptUrlRegistry anonymous() {
			return access(anonymous);
		}

		/**
		 * Specify that URLs are allowed by users that have been remembered.
		 *
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 * @see RememberMeConfigurer
		 */
		public ExpressionInterceptUrlRegistry rememberMe() {
			return access(rememberMe);
		}

		/**
		 * Specify that URLs are not allowed by anyone.
		 *
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		public ExpressionInterceptUrlRegistry denyAll() {
			return access(denyAll);
		}

		/**
		 * Specify that URLs are allowed by any authenticated user.
		 *
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		public ExpressionInterceptUrlRegistry authenticated() {
			return access(authenticated);
		}

		/**
		 * Specify that URLs are allowed by users who have authenticated and were not
		 * "remembered".
		 *
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 * @see RememberMeConfigurer
		 */
		public ExpressionInterceptUrlRegistry fullyAuthenticated() {
			return access(fullyAuthenticated);
		}

		/**
		 * Allows specifying that URLs are secured by an arbitrary expression
		 *
		 * @param attribute the expression to secure the URLs (i.e.
		 * "hasRole('ROLE_USER') and hasRole('ROLE_SUPER')")
		 * @return the {@link ExpressionUrlAuthorizationConfigurer} for further
		 * customization
		 */
		public ExpressionInterceptUrlRegistry access(String attribute) {
			/*if (not) {
				attribute = "!" + attribute;
			}
			interceptUrl(requestMatchers, SecurityConfig.createList(attribute));*/
			return ExpressionUrlAuthorizationConfigurer.this.REGISTRY;
		}
	}


	@Override
	List<AccessDecisionVoter<? extends Object>> getDecisionVoters(H http) {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<AccessDecisionVoter<? extends Object>>();
		/*WebExpressionVoter expressionVoter = new WebExpressionVoter();
		expressionVoter.setExpressionHandler(getExpressionHandler(http));*/
		//decisionVoters.add(expressionVoter);
		return decisionVoters;
	}

}
