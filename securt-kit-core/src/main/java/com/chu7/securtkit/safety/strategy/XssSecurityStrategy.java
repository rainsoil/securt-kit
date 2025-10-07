package com.chu7.securtkit.safety.strategy;

/**
 * XSS安全策略接口
 * 定义XSS防护的基本行为
 * 
 * @author chu7
 * @date 2024/12/19
 */
public interface XssSecurityStrategy extends SecurityStrategy<String> {
    
    /**
     * 检查是否包含XSS攻击
     * @param content 待检查的内容
     * @return 是否包含XSS攻击
     */
    boolean containsXss(String content);
    
    /**
     * HTML编码
     * @param content 待编码的内容
     * @return 编码后的内容
     */
    String htmlEncode(String content);
    
    /**
     * HTML解码
     * @param content 待解码的内容
     * @return 解码后的内容
     */
    String htmlDecode(String content);
    
    /**
     * 清理HTML标签
     * @param content 待清理的内容
     * @return 清理后的内容
     */
    String stripHtml(String content);
}
