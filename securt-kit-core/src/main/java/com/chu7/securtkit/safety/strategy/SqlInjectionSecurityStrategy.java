package com.chu7.securtkit.safety.strategy;

import java.util.List;

/**
 * SQL注入安全策略接口
 * 定义SQL注入防护的基本行为
 * 
 * @author chu7
 * @date 2024/12/19
 */
public interface SqlInjectionSecurityStrategy extends SecurityStrategy<String> {
    
    /**
     * 检查是否包含SQL注入攻击
     * @param content 待检查的内容
     * @return 是否包含SQL注入攻击
     */
    boolean containsSqlInjection(String content);
    
    /**
     * 检查是否包含SQL注入攻击（使用自定义关键词）
     * @param content 待检查的内容
     * @param customKeywords 自定义危险关键词
     * @return 是否包含SQL注入攻击
     */
    boolean containsSqlInjection(String content, List<String> customKeywords);
    
    /**
     * 获取匹配的规则
     * @param content 待检查的内容
     * @return 匹配的规则列表
     */
    List<String> getMatchedRules(String content);
    
    /**
     * SQL转义
     * @param content 待转义的内容
     * @return 转义后的内容
     */
    String escapeSql(String content);
}
