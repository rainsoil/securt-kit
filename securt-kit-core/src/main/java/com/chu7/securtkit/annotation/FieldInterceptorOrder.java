package com.chu7.securtkit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拦截器顺序注解
 * 
 * @author chu7
 * @date 2024/2/1 13:40
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInterceptorOrder {
    /**
     * 拦截器执行顺序
     */
    int value();
}