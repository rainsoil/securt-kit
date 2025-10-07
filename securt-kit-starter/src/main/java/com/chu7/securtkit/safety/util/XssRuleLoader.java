package com.chu7.securtkit.safety.util;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * XSS规则加载器
 * 支持从classpath下的多个文件读取XSS规则
 */
public class XssRuleLoader {
    
    private static final Logger log = LoggerFactory.getLogger(XssRuleLoader.class);
    
    /**
     * XSS规则缓存
     */
    private static final Map<String, Set<String>> XSS_RULE_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 内置XSS规则文件路径
     */
    private static final List<String> BUILTIN_RULE_FILES = Arrays.asList(
        "xss-rules/builtin/script-tags.txt",
        "xss-rules/builtin/event-handlers.txt",
        "xss-rules/builtin/protocols.txt",
        "xss-rules/builtin/attributes.txt"
    );
    
    /**
     * 初始化XSS规则
     * @param ruleFiles 规则文件路径列表
     * @param enableBuiltin 是否启用内置规则
     */
    public static void initXssRules(List<String> ruleFiles, boolean enableBuiltin) {
        // 清空缓存
        XSS_RULE_CACHE.clear();
        
        // 加载内置规则
        if (enableBuiltin) {
            for (String builtinFile : BUILTIN_RULE_FILES) {
                try {
                    loadXssRulesFromFile(builtinFile);
                } catch (Exception e) {
                    log.warn("加载内置XSS规则文件失败: {}", builtinFile, e);
                }
            }
            log.info("内置XSS规则库加载完成，共加载 {} 个文件", BUILTIN_RULE_FILES.size());
        }
        
        // 加载自定义规则文件
        if (ruleFiles != null && !ruleFiles.isEmpty()) {
            for (String filePath : ruleFiles) {
                try {
                    loadXssRulesFromFile(filePath);
                } catch (Exception e) {
                    log.error("加载XSS规则文件失败: {}", filePath, e);
                }
            }
            log.info("自定义XSS规则库加载完成，共加载 {} 个文件", ruleFiles.size());
        }
        
        log.info("XSS规则库初始化完成，总缓存文件数: {}", XSS_RULE_CACHE.size());
    }
    
    /**
     * 初始化XSS规则（兼容旧方法）
     * @param ruleFiles 规则文件路径列表
     */
    public static void initXssRules(List<String> ruleFiles) {
        initXssRules(ruleFiles, true);
    }
    
    /**
     * 从文件加载XSS规则
     */
    private static void loadXssRulesFromFile(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        if (!resource.exists()) {
            log.warn("XSS规则文件不存在: {}", filePath);
            return;
        }
        
        Set<String> rules = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StrUtil.isNotBlank(line) && !line.startsWith("#")) {
                    rules.add(line.toLowerCase());
                }
            }
        }
        
        XSS_RULE_CACHE.put(filePath, rules);
        log.info("从文件 {} 加载了 {} 个XSS规则", filePath, rules.size());
    }
    
    /**
     * 检查是否包含XSS规则
     * @param input 输入内容
     * @return 是否包含XSS规则
     */
    public static boolean containsXss(String input) {
        if (StrUtil.isBlank(input)) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        
        for (Set<String> ruleSet : XSS_RULE_CACHE.values()) {
            for (String rule : ruleSet) {
                if (lowerInput.contains(rule)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 获取匹配的XSS规则
     * @param input 输入内容
     * @return 匹配的规则列表
     */
    public static List<String> getMatchedRules(String input) {
        if (StrUtil.isBlank(input)) {
            return Collections.emptyList();
        }
        
        List<String> matchedRules = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        
        for (Set<String> ruleSet : XSS_RULE_CACHE.values()) {
            for (String rule : ruleSet) {
                if (lowerInput.contains(rule)) {
                    matchedRules.add(rule);
                }
            }
        }
        
        return matchedRules;
    }
    
    /**
     * 过滤XSS规则
     * @param input 输入内容
     * @return 过滤后的内容
     */
    public static String filterXss(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        String result = input;
        
        for (Set<String> ruleSet : XSS_RULE_CACHE.values()) {
            for (String rule : ruleSet) {
                result = result.replaceAll("(?i)" + Pattern.quote(rule), "");
            }
        }
        
        return result.trim();
    }
    
    /**
     * HTML编码
     * @param input 输入内容
     * @return 编码后的内容
     */
    public static String htmlEncode(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }
    
    /**
     * 清空缓存
     */
    public static void clearCache() {
        XSS_RULE_CACHE.clear();
        log.info("XSS规则缓存已清空");
    }
    
    /**
     * 获取缓存统计信息
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : XSS_RULE_CACHE.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }
}
