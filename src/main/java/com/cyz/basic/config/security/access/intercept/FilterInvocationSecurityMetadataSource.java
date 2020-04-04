package com.cyz.basic.config.security.access.intercept;

import org.springframework.security.web.FilterInvocation;

import com.cyz.basic.config.security.access.SecurityMetadataSource;

/**
 * Marker interface for <code>SecurityMetadataSource</code> implementations that are
 * designed to perform lookups keyed on {@link FilterInvocation}s.
 *
 * @author Ben Alex
 */
public interface FilterInvocationSecurityMetadataSource extends SecurityMetadataSource {

}
