package com.cyz.basic.config.security.config.annotation.web;

import javax.servlet.Filter;

import com.cyz.basic.config.security.config.annotation.SecurityBuilder;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurer;

public interface WebSecurityConfigurer<T extends SecurityBuilder<Filter>> extends SecurityConfigurer<Filter, T> {

}
