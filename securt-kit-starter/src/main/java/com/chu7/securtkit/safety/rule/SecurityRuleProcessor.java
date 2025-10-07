package com.chu7.securtkit.safety.rule;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 安全规则处理器
 */
public class SecurityRuleProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityRuleProcessor.class);
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    /**
     * 处理XSS规则
     * @param input 输入内容
     * @param rules 规则列表
     * @param requestUri 请求URI
     * @param paramName 参数名
     * @return 处理结果
     */
    public SecurityRuleResult processXssRules(String input, List<SecurityRule> rules, 
                                            String requestUri, String paramName) {
        if (StrUtil.isBlank(input) || rules == null || rules.isEmpty()) {
            return new SecurityRuleResult(false, input, "No rules to process");
        }
        
        for (SecurityRule rule : rules) {
            if (!rule.isEnabled() || !"XSS".equals(rule.getType())) {
                continue;
            }
            
            // 检查排除条件
            if (isExcluded(rule, requestUri, paramName)) {
                continue;
            }
            
            // 检查是否匹配规则
            if (matchesRule(input, rule)) {
                log.warn("XSS rule matched: {} - {}", rule.getName(), input);
                
                String result = applyAction(input, rule);
                return new SecurityRuleResult(true, result, 
                    "Matched rule: " + rule.getName());
            }
        }
        
        return new SecurityRuleResult(false, input, "No rules matched");
    }
    
    /**
     * 处理SQL注入规则
     * @param input 输入内容
     * @param rules 规则列表
     * @param requestUri 请求URI
     * @param paramName 参数名
     * @return 处理结果
     */
    public SecurityRuleResult processSqlInjectionRules(String input, List<SecurityRule> rules, 
                                                     String requestUri, String paramName) {
        if (StrUtil.isBlank(input) || rules == null || rules.isEmpty()) {
            return new SecurityRuleResult(false, input, "No rules to process");
        }
        
        for (SecurityRule rule : rules) {
            if (!rule.isEnabled() || !"SQL_INJECTION".equals(rule.getType())) {
                continue;
            }
            
            // 检查排除条件
            if (isExcluded(rule, requestUri, paramName)) {
                continue;
            }
            
            // 检查是否匹配规则
            if (matchesRule(input, rule)) {
                log.warn("SQL injection rule matched: {} - {}", rule.getName(), input);
                
                if ("BLOCK".equals(rule.getAction())) {
                    return new SecurityRuleResult(true, input, 
                        "Blocked by rule: " + rule.getName());
                }
                
                String result = applyAction(input, rule);
                return new SecurityRuleResult(true, result, 
                    "Matched rule: " + rule.getName());
            }
        }
        
        return new SecurityRuleResult(false, input, "No rules matched");
    }
    
    /**
     * 处理敏感词规则
     * @param input 输入内容
     * @param rules 规则列表
     * @param requestUri 请求URI
     * @param paramName 参数名
     * @return 处理结果
     */
    public SecurityRuleResult processSensitiveWordRules(String input, List<SecurityRule> rules, 
                                                      String requestUri, String paramName) {
        if (StrUtil.isBlank(input) || rules == null || rules.isEmpty()) {
            return new SecurityRuleResult(false, input, "No rules to process");
        }
        
        String result = input;
        boolean matched = false;
        
        for (SecurityRule rule : rules) {
            if (!rule.isEnabled() || !"SENSITIVE_WORD".equals(rule.getType())) {
                continue;
            }
            
            // 检查排除条件
            if (isExcluded(rule, requestUri, paramName)) {
                continue;
            }
            
            // 检查是否匹配规则
            if (matchesRule(result, rule)) {
                log.warn("Sensitive word rule matched: {} - {}", rule.getName(), result);
                result = applyAction(result, rule);
                matched = true;
            }
        }
        
        return new SecurityRuleResult(matched, result, 
            matched ? "Sensitive words filtered" : "No sensitive words found");
    }
    
    /**
     * 检查是否被排除
     */
    private boolean isExcluded(SecurityRule rule, String requestUri, String paramName) {
        // 检查URL排除模式
        if (rule.getExcludePatterns() != null) {
            for (String pattern : rule.getExcludePatterns()) {
                if (pathMatcher.match(pattern, requestUri)) {
                    return true;
                }
            }
        }
        
        // 检查参数排除
        if (rule.getExcludeParams() != null) {
            return rule.getExcludeParams().contains(paramName);
        }
        
        return false;
    }
    
    /**
     * 检查是否匹配规则
     */
    private boolean matchesRule(String input, SecurityRule rule) {
        if (rule.getPatterns() == null || rule.getPatterns().isEmpty()) {
            return false;
        }
        
        for (String pattern : rule.getPatterns()) {
            try {
                if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(input).find()) {
                    return true;
                }
            } catch (Exception e) {
                log.warn("Invalid regex pattern: {}", pattern, e);
            }
        }
        
        return false;
    }
    
    /**
     * 应用动作
     */
    private String applyAction(String input, SecurityRule rule) {
        switch (rule.getAction()) {
            case "FILTER":
                return filterContent(input, rule);
            case "ENCODE":
                return encodeContent(input);
            case "REPLACE":
                return replaceContent(input, rule);
            case "BLOCK":
                throw new SecurityException("Content blocked by rule: " + rule.getName());
            default:
                return input;
        }
    }
    
    /**
     * 过滤内容
     */
    private String filterContent(String input, SecurityRule rule) {
        String result = input;
        
        if (rule.getPatterns() != null) {
            for (String pattern : rule.getPatterns()) {
                try {
                    result = result.replaceAll("(?i)" + pattern, "");
                } catch (Exception e) {
                    log.warn("Invalid regex pattern: {}", pattern, e);
                }
            }
        }
        
        return result.trim();
    }
    
    /**
     * 编码内容
     */
    private String encodeContent(String input) {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }
    
    /**
     * 替换内容
     */
    private String replaceContent(String input, SecurityRule rule) {
        String replacement = rule.getReplacement();
        if (StrUtil.isBlank(replacement)) {
            replacement = "***";
        }
        
        String result = input;
        
        if (rule.getPatterns() != null) {
            for (String pattern : rule.getPatterns()) {
                try {
                    result = result.replaceAll("(?i)" + pattern, replacement);
                } catch (Exception e) {
                    log.warn("Invalid regex pattern: {}", pattern, e);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 安全规则处理结果
     */
    public static class SecurityRuleResult {
        private final boolean matched;
        private final String result;
        private final String message;
        
        public SecurityRuleResult(boolean matched, String result, String message) {
            this.matched = matched;
            this.result = result;
            this.message = message;
        }
        
        public boolean isMatched() { return matched; }
        public String getResult() { return result; }
        public String getMessage() { return message; }
    }
}
