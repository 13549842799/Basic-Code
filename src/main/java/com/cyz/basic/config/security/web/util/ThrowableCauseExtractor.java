package com.cyz.basic.config.security.web.util;

public interface ThrowableCauseExtractor {
	/**
	 * Extracts the cause from the provided <code>Throwable</code>.
	 *
	 * @param throwable the <code>Throwable</code>
	 * @return the extracted cause (maybe <code>null</code>)
	 *
	 * @throws IllegalArgumentException if <code>throwable</code> is <code>null</code> or
	 * otherwise considered invalid for the implementation
	 */
	Throwable extractCause(Throwable throwable);
}
