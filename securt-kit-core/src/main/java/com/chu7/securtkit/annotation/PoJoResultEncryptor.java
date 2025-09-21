package com.chu7.securtkit.annotation;

import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import com.chu7.securtkit.strategy.DefaultStrategyBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * POJO模式强制指定响应解密
 * 当POJO模式遇到不兼容的场景，但是还是想要在查询结果进行解密处理时使用
 * 注意: 这里是响应类，不是实体类！！！
 * 
 * @author chu7
 * @date 2024/2/1 13:40
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PoJoResultEncryptor {
    /**
     * 指定加解密策略
     */
    Class<? extends FieldEncryptorStrategy> value() default DefaultStrategyBase.EncryptorBeanStrategy.class;
}