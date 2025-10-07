package com.chu7.securtkit.safety.config;

/**
 * 安全配置接口
 * 定义安全防护配置的基本行为
 * 
 * @author chu7
 * @date 2024/12/19
 */
public interface SafetyConfigInterface {
    
    /**
     * 是否启用安全防护
     * @return 是否启用
     */
    boolean isEnabled();
    
    /**
     * 设置是否启用安全防护
     * @param enabled 是否启用
     */
    void setEnabled(boolean enabled);
    
    /**
     * 获取XSS配置
     * @return XSS配置
     */
    Object getXssConfig();
    
    /**
     * 获取SQL注入配置
     * @return SQL注入配置
     */
    Object getSqlInjectionConfig();
    
    /**
     * 获取敏感词配置
     * @return 敏感词配置
     */
    Object getSensitiveWordConfig();
    
    /**
     * 获取白名单配置
     * @return 白名单配置
     */
    Object getWhitelistConfig();
}
