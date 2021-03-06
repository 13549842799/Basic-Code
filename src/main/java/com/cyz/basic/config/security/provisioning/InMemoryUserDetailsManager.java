package com.cyz.basic.config.security.provisioning;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.cyz.basic.config.security.authentication.AuthenticationManager;
import com.cyz.basic.config.security.authentication.UsernamePasswordAuthenticationToken;
import com.cyz.basic.config.security.core.Authentication;
import com.cyz.basic.config.security.core.context.SecurityContextHolder;
import com.cyz.basic.config.security.core.userdetails.User;
import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.config.security.exception.AccessDeniedException;
import com.cyz.basic.config.security.exception.UsernameNotFoundException;

/**
 * Non-persistent implementation of {@code UserDetailsManager} which is backed by an
 * in-memory map.
 * <p>
 * Mainly intended for testing and demonstration purposes, where a full blown persistent
 * system isn't required.
 *
 * @author Luke Taylor
 * @since 3.1
 */
public class InMemoryUserDetailsManager implements UserDetailsManager {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private final Map<String, MutableUserDetails> users = new HashMap<>();
	
	private AuthenticationManager authenticationManager;
	
	public InMemoryUserDetailsManager() {
	}

	public InMemoryUserDetailsManager(Collection<UserDetails> users) {
		for (UserDetails user : users) {
			createUser(user);
		}
	}

	public InMemoryUserDetailsManager(UserDetails... users) {
		for (UserDetails user : users) {
			createUser(user);
		}
	}

	public InMemoryUserDetailsManager(Properties users) {
		/*Enumeration<?> names = users.propertyNames();
		UserAttributeEditor editor = new UserAttributeEditor();

		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			editor.setAsText(users.getProperty(name));
			UserAttribute attr = (UserAttribute) editor.getValue();
			UserDetails user = new User(name, attr.getPassword(), attr.isEnabled(), true,
					true, true, attr.getAuthorities());
			createUser(user);
		}*/
	}

	public void createUser(UserDetails user) {
		Assert.isTrue(!userExists(user.getUsername()), "user should not exist");

		users.put(user.getUsername().toLowerCase(), new MutableUser(user));
	}

	public void deleteUser(String username) {
		users.remove(username.toLowerCase());
	}

	public void updateUser(UserDetails user) {
		Assert.isTrue(userExists(user.getUsername()), "user should exist");

		users.put(user.getUsername().toLowerCase(), new MutableUser(user));
	}

	public boolean userExists(String username) {
		return users.containsKey(username.toLowerCase());
	}

	public void changePassword(String oldPassword, String newPassword) {
		Authentication currentUser = SecurityContextHolder.getContext()
				.getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password as no Authentication object found in context "
							+ "for current user.");
		}

		String username = currentUser.getName();

		logger.debug("Changing password for user '" + username + "'");

		// If an authentication manager has been set, re-authenticate the user with the
		// supplied password.
		if (authenticationManager != null) {
			logger.debug("Reauthenticating user '" + username
					+ "' for password change request.");

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					username, oldPassword));
		}
		else {
			logger.debug("No authentication manager set. Password won't be re-checked.");
		}

		MutableUserDetails user = users.get(username);

		if (user == null) {
			throw new IllegalStateException("Current user doesn't exist in database.");
		}

		user.setPassword(newPassword);
	}

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UserDetails user = users.get(username.toLowerCase());

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		return new User(user.getUsername(), user.getPassword(), user.isEnabled(),
				user.isAccountNonExpired(), user.isCredentialsNonExpired(),
				user.isAccountNonLocked(), user.getAuthorities());
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

}
