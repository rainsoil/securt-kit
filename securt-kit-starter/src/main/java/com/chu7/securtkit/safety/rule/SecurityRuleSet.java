package com.chu7.securtkit.safety.rule;

import java.util.List;
import java.util.Map;

/**
 * 安全规则集
 */
public class SecurityRuleSet {
    
    /**
     * 规则集名称
     */
    private String name;
    
    /**
     * 规则集描述
     */
    private String description;
    
    /**
     * 规则集版本
     */
    private String version;
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 规则列表
     */
    private List<SecurityRule> rules;
    
    /**
     * 全局配置
     */
    private Map<String, Object> globalConfig;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public List<SecurityRule> getRules() { return rules; }
    public void setRules(List<SecurityRule> rules) { this.rules = rules; }
    
    public Map<String, Object> getGlobalConfig() { return globalConfig; }
    public void setGlobalConfig(Map<String, Object> globalConfig) { this.globalConfig = globalConfig; }
}
