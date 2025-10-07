package com.chu7.securtkit.safety.core;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 安全防护策略接口
 * 使用策略模式定义不同的安全防护策略
 * 
 * @author chu7
 * @date 2024/12/19
 */
public interface SecurityProtectionStrategy {
    
    /**
     * 获取防护类型
     * @return 防护类型
     */
    SecurityProtectionType getProtectionType();
    
    /**
     * 检查内容是否包含攻击
     * @param content 待检查的内容
     * @return 是否包含攻击
     */
    boolean containsAttack(String content);
    
    /**
     * 过滤攻击内容
     * @param content 待过滤的内容
     * @return 过滤后的内容
     */
    String filterAttack(String content);
    
    /**
     * 获取匹配的攻击关键字
     * @param content 待检查的内容
     * @return 匹配的关键字列表
     */
    List<String> getMatchedKeywords(String content);
    
    /**
     * 检查请求是否需要进行防护
     * @param request HTTP请求
     * @return 是否需要防护
     */
    boolean needsProtection(HttpServletRequest request);
    
    /**
     * 初始化防护策略
     */
    void initialize();
    
    /**
     * 重新加载防护策略
     */
    void reload();
    
    /**
     * 获取关键字数量
     * @return 关键字数量
     */
    int getKeywordCount();
    
    /**
     * 设置检测模式
     * @param mode 检测模式
     */
    void setDetectionMode(String mode);
    
    /**
     * 获取检测模式
     * @return 检测模式
     */
    String getDetectionMode();
}
