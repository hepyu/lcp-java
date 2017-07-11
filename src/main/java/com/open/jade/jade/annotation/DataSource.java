package com.open.jade.jade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为了让jade支持多数据库而增加的注解
 * 
 * @author hepengyuan
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {

	String catalog() default "jade.dataSourceFactory";
}
