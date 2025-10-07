package com.chu7.securtkit.safety.strategy;

/**
 * 安全策略接口
 * 定义安全防护的基本行为
 * 
 * @author chu7
 * @date 2024/12/19
 */
public interface SecurityStrategy<T> {
    
    /**
     * 检查内容是否安全
     * @param content 待检查的内容
     * @return 是否安全
     */
    boolean isSafe(T content);
    
    /**
     * 过滤不安全内容
     * @param content 待过滤的内容
     * @return 过滤后的内容
     */
    T filter(T content);
    
    /**
     * 获取策略类型
     * @return 策略类型
     */
    String getStrategyType();
    
    /**
     * 是否支持指定类型
     * @param type 类型
     * @return 是否支持
     */
    boolean supports(String type);
}
