package com.cyz.basic.config.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.cyz.basic.config.security.access.vote.MyAuthenticatedVoter;
import com.cyz.basic.config.security.authentication.AuthenticationSuccessHandler;
import com.cyz.basic.config.security.config.annotation.authentication.configuration.WebSecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.web.builders.HttpSecurity;
import com.cyz.basic.config.security.config.annotation.web.builders.WebSecurity;

@EnableConfigurationProperties(value={SecurityProperties.class})
public abstract class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private SecurityProperties properties;
	
	//protected AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler ();
	
	public WebSecurityConfig() {
		super();
	}
	
	

	/*@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();

		
		http.authorizeRequests()
		.anyRequest().authenticated()
		.and().formLogin().loginProcessingUrl(properties.getLoginUrl()).successHandler(successHandler).permitAll();//登录行为任意访问     
		http.logout().logoutUrl(properties.getLogoutUrl()).permitAll();//注销行为任意访问
		
		MyAuthenticatedVoter voter = new MyAuthenticatedVoter();
        List<AccessDecisionVoter<? extends Object>> list = new ArrayList<AccessDecisionVoter<? extends Object>>();
        list.add(voter);
        AffirmativeBased affi = new AffirmativeBased(list);
		
        FilterSecurityInterceptor fsi = new FilterSecurityInterceptor();
		fsi.setAccessDecisionManager(affi);
		SecurityMetadataSourceSupport sms = this.getApplicationContext().getBean("securityMetadataSource", SecurityMetadataSourceSupport.class);		
		fsi.setSecurityMetadataSource(sms);
		http.addFilterAt(fsi, FilterSecurityInterceptor.class);
		
		additionalConfigure(http);
	}*/



	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated()
		.and().formLogin().loginProcessingUrl(properties.getLoginUrl()).permitAll();//登录行为任意访问     
		http.logout().clearAuthentication(true).logoutUrl(properties.getLogoutUrl()); //只允许已登录的访问
	}



	protected abstract void additionalConfigure(HttpSecurity http);
	
	public abstract List<Filter> addFilters();
	
	

}
