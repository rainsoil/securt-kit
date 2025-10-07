package com.chu7.securtkit.safety.rule;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安全规则加载器
 */
public class SecurityRuleLoader {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityRuleLoader.class);
    
    // 简化实现，暂时不支持JSON/YAML解析
    // 在实际使用中需要添加Jackson依赖
    
    /**
     * 规则缓存
     */
    private static final Map<String, SecurityRuleSet> RULE_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 加载规则文件
     * @param ruleFiles 规则文件路径列表
     * @param enableBuiltin 是否启用内置规则
     * @return 规则集列表
     */
    public static List<SecurityRuleSet> loadRules(List<String> ruleFiles, boolean enableBuiltin) {
        List<SecurityRuleSet> ruleSets = new ArrayList<>();
        
        // 加载内置规则
        if (enableBuiltin) {
            ruleSets.addAll(BuiltinRules.getAllBuiltinRules());
            log.info("Loaded {} builtin rule sets", BuiltinRules.getAllBuiltinRules().size());
        }
        
        // 加载文件规则
        if (ruleFiles != null && !ruleFiles.isEmpty()) {
            for (String ruleFile : ruleFiles) {
                try {
                    SecurityRuleSet ruleSet = loadRuleFile(ruleFile);
                    if (ruleSet != null) {
                        ruleSets.add(ruleSet);
                        log.info("Loaded rule file: {}", ruleFile);
                    }
                } catch (Exception e) {
                    log.error("Failed to load rule file: {}", ruleFile, e);
                }
            }
        }
        
        return ruleSets;
    }
    
    /**
     * 加载单个规则文件
     */
    private static SecurityRuleSet loadRuleFile(String ruleFile) throws IOException {
        // 检查缓存
        if (RULE_CACHE.containsKey(ruleFile)) {
            return RULE_CACHE.get(ruleFile);
        }
        
        ClassPathResource resource = new ClassPathResource(ruleFile);
        if (!resource.exists()) {
            log.warn("Rule file not found: {}", ruleFile);
            return null;
        }
        
        // 简化实现：暂时返回空规则集
        // 在实际使用中需要添加Jackson依赖来解析YAML/JSON文件
        SecurityRuleSet ruleSet = new SecurityRuleSet();
        ruleSet.setName("Empty Rule Set");
        ruleSet.setDescription("Empty rule set for " + ruleFile);
        ruleSet.setVersion("1.0.0");
        ruleSet.setEnabled(true);
        ruleSet.setRules(new ArrayList<>());
        
        // 验证规则集
        validateRuleSet(ruleSet);
        RULE_CACHE.put(ruleFile, ruleSet);
        
        log.warn("Rule file loading is simplified. Please add Jackson dependency for full YAML/JSON support: {}", ruleFile);
        
        return ruleSet;
    }
    
    /**
     * 验证规则集
     */
    private static void validateRuleSet(SecurityRuleSet ruleSet) {
        if (StrUtil.isBlank(ruleSet.getName())) {
            ruleSet.setName("Unnamed Rule Set");
        }
        
        if (StrUtil.isBlank(ruleSet.getVersion())) {
            ruleSet.setVersion("1.0.0");
        }
        
        if (ruleSet.getRules() == null) {
            ruleSet.setRules(new ArrayList<>());
        }
        
        // 验证规则
        for (SecurityRule rule : ruleSet.getRules()) {
            validateRule(rule);
        }
    }
    
    /**
     * 验证规则
     */
    private static void validateRule(SecurityRule rule) {
        if (StrUtil.isBlank(rule.getId())) {
            rule.setId(UUID.randomUUID().toString());
        }
        
        if (StrUtil.isBlank(rule.getName())) {
            rule.setName("Unnamed Rule");
        }
        
        if (StrUtil.isBlank(rule.getType())) {
            rule.setType("UNKNOWN");
        }
        
        if (rule.getPatterns() == null) {
            rule.setPatterns(new ArrayList<>());
        }
        
        if (rule.getExcludePatterns() == null) {
            rule.setExcludePatterns(new ArrayList<>());
        }
        
        if (rule.getExcludeParams() == null) {
            rule.setExcludeParams(new ArrayList<>());
        }
        
        if (rule.getParameters() == null) {
            rule.setParameters(new HashMap<>());
        }
        
        if (StrUtil.isBlank(rule.getAction())) {
            rule.setAction("FILTER");
        }
    }
    
    /**
     * 根据类型获取规则
     * @param ruleSets 规则集列表
     * @param type 规则类型
     * @return 指定类型的规则列表
     */
    public static List<SecurityRule> getRulesByType(List<SecurityRuleSet> ruleSets, String type) {
        List<SecurityRule> result = new ArrayList<>();
        
        for (SecurityRuleSet ruleSet : ruleSets) {
            if (!ruleSet.isEnabled()) {
                continue;
            }
            
            for (SecurityRule rule : ruleSet.getRules()) {
                if (rule.isEnabled() && type.equals(rule.getType())) {
                    result.add(rule);
                }
            }
        }
        
        // 按优先级排序
        result.sort(Comparator.comparingInt(SecurityRule::getPriority));
        
        return result;
    }
    
    /**
     * 清空缓存
     */
    public static void clearCache() {
        RULE_CACHE.clear();
        log.info("Rule cache cleared");
    }
    
    /**
     * 获取缓存统计信息
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, SecurityRuleSet> entry : RULE_CACHE.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().getRules().size());
        }
        return stats;
    }
}
