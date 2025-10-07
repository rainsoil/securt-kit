package com.chu7.securtkit.safety.strategy;

import java.util.List;

/**
 * 敏感词安全策略接口
 * 定义敏感词过滤的基本行为
 * 
 * @author chu7
 * @date 2024/12/19
 */
public interface SensitiveWordSecurityStrategy extends SecurityStrategy<String> {
    
    /**
     * 查找敏感词
     * @param content 待检查的内容
     * @return 包含的敏感词列表
     */
    List<String> findSensitiveWords(String content);
    
    /**
     * 检查是否包含敏感词
     * @param content 待检查的内容
     * @return 是否包含敏感词
     */
    boolean containsSensitiveWords(String content);
    
    /**
     * 过滤敏感词
     * @param content 待过滤的内容
     * @param replaceChar 替换字符
     * @return 过滤后的内容
     */
    String filterSensitiveWords(String content, String replaceChar);
    
    /**
     * 初始化敏感词库
     * @param wordFiles 敏感词文件路径列表
     * @param enableBuiltin 是否启用内置敏感词
     */
    void initSensitiveWords(List<String> wordFiles, boolean enableBuiltin);
    
    /**
     * 清空缓存
     */
    void clearCache();
    
    /**
     * 获取缓存统计信息
     * @return 统计信息
     */
    java.util.Map<String, Integer> getCacheStats();
}
