package com.cyz.basic.config.security.authentication;

import java.io.Serializable;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebAuthenticationDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6896394917272112891L;
	
	private final String remoteAddress;
	private final String sessionId;
	private final String token;
	
	/**
	 * Records the remote address and will also set the session Id if a session already
	 * exists (it won't create one).
	 *
	 * @param request that the authentication request was received from
	 */
	public WebAuthenticationDetails(HttpServletRequest request) {
		this.remoteAddress = request.getRemoteAddr();

		HttpSession session = request.getSession(false);
		this.sessionId = (session != null) ? session.getId() : null;
		this.token = createToken();
	}

	/**
	 * Constructor to add Jackson2 serialize/deserialize support
	 *
	 * @param remoteAddress remote address of current request
	 * @param sessionId session id
	 */
	private WebAuthenticationDetails(final String remoteAddress, final String sessionId) {
		this.remoteAddress = remoteAddress;
		this.sessionId = sessionId;
		this.token = createToken();
	}
	
	private String createToken() {
		return UUID.randomUUID().toString();
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails rhs = (WebAuthenticationDetails) obj;

			if ((remoteAddress == null) && (rhs.getRemoteAddress() != null)) {
				return false;
			}

			if ((remoteAddress != null) && (rhs.getRemoteAddress() == null)) {
				return false;
			}

			if (remoteAddress != null) {
				if (!remoteAddress.equals(rhs.getRemoteAddress())) {
					return false;
				}
			}

			if ((sessionId == null) && (rhs.getSessionId() != null)) {
				return false;
			}

			if ((sessionId != null) && (rhs.getSessionId() == null)) {
				return false;
			}

			if (sessionId != null) {
				if (!sessionId.equals(rhs.getSessionId())) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Indicates the TCP/IP address the authentication request was received from.
	 *
	 * @return the address
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Indicates the <code>HttpSession</code> id the authentication request was received
	 * from.
	 *
	 * @return the session ID
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getToken() {
		return token;
	}

	@Override
	public int hashCode() {
		int code = 7654;

		if (this.remoteAddress != null) {
			code = code * (this.remoteAddress.hashCode() % 7);
		}

		if (this.sessionId != null) {
			code = code * (this.sessionId.hashCode() % 7);
		}

		return code;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("RemoteIpAddress: ").append(this.getRemoteAddress()).append("; ");
		sb.append("SessionId: ").append(this.getSessionId());

		return sb.toString();
	}
}
