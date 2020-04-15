package com.cyz.basic.valid.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * the annotation is do for checking the class is open the valid
 * @author cyz
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface EnableCheckOut {

}
