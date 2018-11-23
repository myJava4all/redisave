package com.java4all.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description:
 *
 * @author wangzx
 * @version v1.0
 * @date 2018/11/21 17:50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCacheable {

  String cacheName() default "";

  String fieldKey() default "";

  String value() default "";

  long expireTime() default 3600L;
}
