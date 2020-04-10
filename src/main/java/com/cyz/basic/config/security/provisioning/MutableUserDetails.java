package com.cyz.basic.config.security.provisioning;

import com.cyz.basic.config.security.core.userdetails.UserDetails;

public interface MutableUserDetails extends UserDetails {
	void setPassword(String password);
}
