package com.open.lcp.core.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Model的描述信息
 * 
 * @author hepengyuan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LcpClassDesc {

	String value();

	/**
	 * 开始支援的版本
	 * 
	 * @return
	 */
	String ver() default "";
}
