package com.cyz.basic.pojo;

/**
 * 本注解当前并没有实际意义，只是用来标记哪些方法会触发到mysql的触发器
 * @author cyz
 *
 */
public @interface UseTrigger {
	/**
	 * 触发器的名称
	 * @return
	 */
	String name();
	
	/**
	 * 触发器的内容
	 * @return
	 */
	String effect() default "";
}
