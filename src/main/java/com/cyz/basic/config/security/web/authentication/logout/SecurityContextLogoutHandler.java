package com.cyz.basic.config.security.web.authentication.logout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.core.context.CyzSecurityContext;
import com.cyz.basic.config.security.core.context.SecurityContextHolder;

public class SecurityContextLogoutHandler implements LogoutHandler {
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	private boolean invalidateHttpSession = true;
	private boolean clearAuthentication = true;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Assert.notNull(request, "HttpServletRequest required");
		/*if (invalidateHttpSession) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				logger.debug("Invalidating session: " + session.getId());
				session.invalidate();
			}
		}*/

		if (clearAuthentication) {
			CyzSecurityContext context = SecurityContextHolder.getContext();
			context.clearAuthentication();
		}
	}
	
	public boolean isInvalidateHttpSession() {
		return invalidateHttpSession;
	}

	/**
	 * Causes the {@link HttpSession} to be invalidated when this {@link LogoutHandler} is
	 * invoked. Defaults to true.
	 *
	 * @param invalidateHttpSession true if you wish the session to be invalidated
	 * (default) or false if it should not be.
	 */
	public void setInvalidateHttpSession(boolean invalidateHttpSession) {
		this.invalidateHttpSession = invalidateHttpSession;
	}

	/**
	 * If true, removes the {@link Authentication} from the {@link SecurityContext} to
	 * prevent issues with concurrent requests.
	 *
	 * @param clearAuthentication true if you wish to clear the {@link Authentication}
	 * from the {@link SecurityContext} (default) or false if the {@link Authentication}
	 * should not be removed.
	 */
	public void setClearAuthentication(boolean clearAuthentication) {
		this.clearAuthentication = clearAuthentication;
	}

}
