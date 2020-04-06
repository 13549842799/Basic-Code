package com.cyz.basic.config.security.config.annotation.web.configurers;

import com.cyz.basic.config.security.config.annotation.ObjectPostProcessor;
import com.cyz.basic.config.security.config.annotation.SecurityConfigurerAdapter;
import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.DefaultSecurityFilterChain;

public abstract class AbstractHttpConfigurer<T extends AbstractHttpConfigurer<T, B>, B extends HttpSecurityBuilder<B>> 
    extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {

	/**
	 * Disables the {@link AbstractHttpConfigurer} by removing it. After doing so a fresh
	 * version of the configuration can be applied.
	 *
	 * @return the {@link HttpSecurityBuilder} for additional customizations
	 */
	@SuppressWarnings("unchecked")
	public B disable() {
		getBuilder().removeConfigurer(getClass());
		return getBuilder();
	}

	@SuppressWarnings("unchecked")
	public T withObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
		addObjectPostProcessor(objectPostProcessor);
		return (T) this;
	}
}
