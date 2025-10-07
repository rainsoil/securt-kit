package com.chu7.securtkit.safety.rule;

import java.util.List;
import java.util.Map;

/**
 * 安全规则类
 */
public class SecurityRule {
    
    /**
     * 规则ID
     */
    private String id;
    
    /**
     * 规则名称
     */
    private String name;
    
    /**
     * 规则描述
     */
    private String description;
    
    /**
     * 规则类型：XSS、SQL_INJECTION、SENSITIVE_WORD
     */
    private String type;
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 规则优先级（数字越小优先级越高）
     */
    private int priority = 100;
    
    /**
     * 匹配模式
     */
    private List<String> patterns;
    
    /**
     * 排除模式
     */
    private List<String> excludePatterns;
    
    /**
     * 排除参数
     */
    private List<String> excludeParams;
    
    /**
     * 规则参数
     */
    private Map<String, Object> parameters;
    
    /**
     * 动作：FILTER、ENCODE、BLOCK、REPLACE
     */
    private String action = "FILTER";
    
    /**
     * 替换内容（当动作为REPLACE时使用）
     */
    private String replacement;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public List<String> getPatterns() { return patterns; }
    public void setPatterns(List<String> patterns) { this.patterns = patterns; }
    
    public List<String> getExcludePatterns() { return excludePatterns; }
    public void setExcludePatterns(List<String> excludePatterns) { this.excludePatterns = excludePatterns; }
    
    public List<String> getExcludeParams() { return excludeParams; }
    public void setExcludeParams(List<String> excludeParams) { this.excludeParams = excludeParams; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getReplacement() { return replacement; }
    public void setReplacement(String replacement) { this.replacement = replacement; }
}
