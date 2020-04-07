package com.cyz.basic.config.security.access.vote;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.access.AccessDecisionManager;
import com.cyz.basic.config.security.access.AccessDecisionVoter;
import com.cyz.basic.config.security.access.ConfigAttribute;
import com.cyz.basic.config.security.exception.AccessDeniedException;


public abstract class AbstractAccessDecisionManager implements AccessDecisionManager, InitializingBean, MessageSourceAware {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private List<AccessDecisionVoter<? extends Object>> decisionVoters;
	
	protected MessageSourceAccessor messages = null;
	
	private boolean allowIfAllAbstainDecisions = false;
	
	protected AbstractAccessDecisionManager(
			List<AccessDecisionVoter<? extends Object>> decisionVoters) {
		Assert.notEmpty(decisionVoters, "A list of AccessDecisionVoters is required");
		this.decisionVoters = decisionVoters;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notEmpty(this.decisionVoters, "A list of AccessDecisionVoters is required");
		Assert.notNull(this.messages, "A message source must be set");
	}
	
	public List<AccessDecisionVoter<? extends Object>> getDecisionVoters() {
		return this.decisionVoters;
	}

	public boolean isAllowIfAllAbstainDecisions() {
		return allowIfAllAbstainDecisions;
	}

	public void setAllowIfAllAbstainDecisions(boolean allowIfAllAbstainDecisions) {
		this.allowIfAllAbstainDecisions = allowIfAllAbstainDecisions;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}
	
	protected final void checkAllowIfAllAbstainDecisions() {
		if (!this.isAllowIfAllAbstainDecisions()) {
			throw new AccessDeniedException(messages.getMessage(
					"AbstractAccessDecisionManager.accessDenied", "Access is denied"));
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(ConfigAttribute attribute) {
		for (AccessDecisionVoter voter : this.decisionVoters) {
			if (voter.supports(attribute)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Iterates through all <code>AccessDecisionVoter</code>s and ensures each can support
	 * the presented class.
	 * <p>
	 * If one or more voters cannot support the presented class, <code>false</code> is
	 * returned.
	 *
	 * @param clazz the type of secured object being presented
	 * @return true if this type is supported
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean supports(Class<?> clazz) {
		for (AccessDecisionVoter voter : this.decisionVoters) {
			if (!voter.supports(clazz)) {
				return false;
			}
		}

		return true;
	}

}
