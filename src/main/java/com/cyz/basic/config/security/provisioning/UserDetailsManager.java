package com.cyz.basic.config.security.provisioning;

import com.cyz.basic.config.security.core.userdetails.UserDetails;
import com.cyz.basic.config.security.core.userdetails.UserDetailsService;

/**
 * An extension of the {@link UserDetailsService} which provides the ability to create new
 * users and update existing ones.
 *
 * @author Luke Taylor
 * @since 2.0
 */
public interface UserDetailsManager extends UserDetailsService {

	/**
	 * Create a new user with the supplied details.
	 */
	void createUser(UserDetails user);

	/**
	 * Update the specified user.
	 */
	void updateUser(UserDetails user);

	/**
	 * Remove the user with the given login name from the system.
	 */
	void deleteUser(String username);

	/**
	 * Modify the current user's password. This should change the user's password in the
	 * persistent user repository (datbase, LDAP etc).
	 *
	 * @param oldPassword current password (for re-authentication if required)
	 * @param newPassword the password to change to
	 */
	void changePassword(String oldPassword, String newPassword);

	/**
	 * Check if a user with the supplied login name exists in the system.
	 */
	boolean userExists(String username);

}
