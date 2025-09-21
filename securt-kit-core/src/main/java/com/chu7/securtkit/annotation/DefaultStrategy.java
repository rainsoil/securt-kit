package com.chu7.securtkit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识默认策略的注解
 * 当项目中存在多个同类型策略时，使用此注解标注默认策略
 * 
 * @author chu7
 * @date 2025/6/30 17:42
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultStrategy {
}