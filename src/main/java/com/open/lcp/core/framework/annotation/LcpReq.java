package com.open.lcp.core.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当前接口仅有一个简单类型参数时，可用此方式实现。
 * 
 * 注意：可选时，对象类型（包括Integer,Long,Boolean等）一律默认为null，基类类型为0(int,byte,long,double,etc)或false（boolean）
 * 
 * @author hepengyuan
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LcpReq {

	/** 是否必选，默认必选 */
	String name();

	/** 是否必选，默认必选 */
	boolean required();

	/** 字段描述 */
	String desc();

	/** 值域：最小。min值在min或max值中有一个不为0时有效。 */
	long min() default 0;

	/** 值域：最大。max仅在不小于min值且不全为0时有效。 */
	long max() default 0;

	/** 错误码，当校验失败时返回。 */
	int errorCode() default 0;

	/** 错误提示，当校验失败时返回。 */
	String errorMsg() default "";

	/** 如果是字符串，是否需要trim，默认不trim，仅在min或max有限制时生效 */
	boolean trim() default false;
}
