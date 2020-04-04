package com.cyz.basic.config.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.cyz.basic.config.security.voter.MyAuthenticatedVoter;

@EnableConfigurationProperties(value={SecurityProperties.class})
public abstract class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private SecurityProperties properties;
	
	protected AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler ();
	
	public WebSecurityConfig() {
		super();
	}

	@Override
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
		
        /*FilterSecurityInterceptor fsi = new FilterSecurityInterceptor();
		fsi.setAccessDecisionManager(affi);
		SecurityMetadataSourceSupport sms = this.getApplicationContext().getBean("securityMetadataSource", SecurityMetadataSourceSupport.class);		
		fsi.setSecurityMetadataSource(sms);
		http.addFilterAt(fsi, FilterSecurityInterceptor.class);*/
		
		additionalConfigure(http);
	}
	
	protected abstract void additionalConfigure(HttpSecurity http);
	
	public abstract List<Filter> addFilters();

	public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
		this.successHandler = successHandler;
	}
	
	

}
