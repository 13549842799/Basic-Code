package com.cyz.basic.config.security.config.annotation;

/**
 * Interface for building an Object
 *
 * @author Rob Winch
 * @since 3.2
 *
 * @param <O> The type of the Object being built
 */
public interface SecurityBuilder<O> {

	/**
	 * Builds the object and returns it or null.
	 *
	 * @return the Object to be built or null if the implementation allows it.
	 * @throws Exception if an error occurred when building the Object
	 */
	O build() throws Exception;
}