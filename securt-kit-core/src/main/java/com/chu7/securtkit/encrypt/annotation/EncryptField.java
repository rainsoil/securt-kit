package com.chu7.securtkit.encrypt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段加密注解
 * 标注在需要加密的字段上
 *
 * @author chu7
 * @date 2025/8/15
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {
    
    /**
     * 加密算法，默认AES
     */
    String algorithm() default "AES";
    
    /**
     * 是否启用加密
     */
    boolean enabled() default true;
    
    /**
     * 自定义加密策略类
     */
    Class<?> strategy() default Object.class;
}
