package com.chu7.securtkit.strategy;

/**
 * 加解密策略，想要实现自定义的加解密策略的话，实现这个接口
 * 注意：目前T 仅支持 String 和 Expression类型
 *
 * @author chu7
 * @date 2025/6/30 17:42
 */
public interface FieldEncryptorStrategy<T> {
    
    /**
     * 加密算法
     *
     * @author chu7
     * @date 2024/4/8 14:12
     * @param oldExpression 原始表达式
     * @return 加密后的表达式
     */
    T encryption(T oldExpression);

    /**
     * 解密算法
     *
     * @author chu7
     * @date 2024/4/8 14:13
     * @param oldExpression 原始表达式
     * @return 解密后的表达式
     */
    T decryption(T oldExpression);
}