package com.open.lcp.core.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定当前方法为Mcp的方法，通过name指定方法的�??
 * 
 * @author hepengyuan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LcpHttpMethod {

	/**
	 * 方法�?
	 * 
	 * @return
	 */
	String name();

	/**
	 * �?始支持的版本
	 * 
	 * @return
	 */
	String ver();

	/**
	 * 接口的描�?
	 * 
	 * @return
	 */
	String desc();

	/**
	 * 是否�?要登�?
	 * 
	 * @return true:必须登录才能访问此接口�?�false：对是否登录无要求�??
	 */
	boolean logon() default false;

	/**
	 * 可能出现的非零错误码
	 * 
	 * @return
	 */
	int[] nzcode() default {};

	/**
	 * 作�??
	 * 
	 * @return
	 */
	String[] auths() default {};

	/**
	 * 缓存结果；仅当对象中�?有结点�?�都不变时才能用�?
	 * 
	 * 注意：不明群众请不要使用此�?�项，慎用，用前必须备案.
	 * 
	 * @return
	 */
	boolean cacheResult() default false;

	/**
	 * 是否加载deviceId对应的AppInit数据
	 * 
	 * @return
	 */
	boolean loadAppInitData() default false;

	/**
	 * 免权限，对所有应用开�?
	 * 
	 * @return
	 */
	boolean open() default false;
}
