package com.cyz.basic.config.security.config.annotation.web.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.cyz.basic.config.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

/**
 * Add this annotation to an {@code @Configuration} class to have the Spring Security
 * configuration defined in any {@link WebSecurityConfigurer} or more likely by extending
 * the {@link WebSecurityConfigurerAdapter} base class and overriding individual methods:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebSecurity
 * public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
 *
 * 	&#064;Override
 * 	public void configure(WebSecurity web) throws Exception {
 * 		web.ignoring()
 * 		// Spring Security should completely ignore URLs starting with /resources/
 * 				.antMatchers(&quot;/resources/**&quot;);
 * 	}
 *
 * 	&#064;Override
 * 	protected void configure(HttpSecurity http) throws Exception {
 * 		http.authorizeRequests().antMatchers(&quot;/public/**&quot;).permitAll().anyRequest()
 * 				.hasRole(&quot;USER&quot;).and()
 * 				// Possibly more configuration ...
 * 				.formLogin() // enable form based log in
 * 				// set permitAll for all URLs associated with Form Login
 * 				.permitAll();
 * 	}
 *
 * 	&#064;Override
 * 	protected void configure(AuthenticationManagerBuilder auth) {
 * 		auth
 * 		// enable in memory based authentication with a user named &quot;user&quot; and &quot;admin&quot;
 * 		.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;)
 * 				.and().withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
 * 	}
 *
 * 	// Possibly more overridden methods ...
 * }
 * </pre>
 * 
 * 被改造过
 *
 * @see WebSecurityConfigurer
 * @see WebSecurityConfigurerAdapter
 *
 * @author Rob Winch
 * @since 3.2
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Import({WebSecurityConfiguration.class
	   , SpringWebMvcImportSelector.class})
@Documented
@EnableGlobalAuthentication
@Configuration
public @interface EnableWebSecurity {
	
	/**
	 * Controls debugging support for Spring Security. Default is false.
	 * @return if true, enables debug support with Spring Security
	 */
	boolean debug() default false;

}
