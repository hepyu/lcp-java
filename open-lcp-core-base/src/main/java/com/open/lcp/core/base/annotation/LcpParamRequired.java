package com.open.lcp.core.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识请求或响应的数据为必须提供，两种使用方式 Required("这是一个属性的描述") Required(value"这是一个属性的描述")<br/>
 * <br/>
 * 
 * 请求对象的Field为数组或List时，会自动把逗号分隔的字符串自动转为相应的数组或List，且数组或List的Field不必声明set方法。<br/>
 * <br/>
 * 
 * max仅在不小于min值且不全为0时有效。min值在min或max值中有一个不为0时有效。
 * 
 * @author Marshal(imdeep@gmail.com) Initial Created at 2013-10-17
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LcpParamRequired {

	/** 是否必选，默认必选 */
	boolean value() default true;

	/** 字段描述 */
	String desc() default "";

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

	/** 是否已经使用aes加密 **/
	boolean aes() default false;

	/** 是否已经使用gz压缩 **/
	boolean gz() default false;

	/** 如果是结构体，确认期解析规则，默认不是结构体 */
	Struct struct() default Struct.NONE;

	public enum Struct {
		NONE, JSON
	}
}
