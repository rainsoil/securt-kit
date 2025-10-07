package com.chu7.securtkit.safety.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全防护配置类
 * 支持XSS防护、SQL注入防护、敏感词过滤的配置
 * 支持规则文件配置和内置规则
 */
@Component
@ConfigurationProperties(prefix = "securt-kit.safety")
public class SafetyConfig {

    /**
     * 是否启用安全防护
     */
    private boolean enabled = true;

    /**
     * 规则配置
     */
    private RuleConfig rules = new RuleConfig();

    /**
     * XSS防护配置（兼容旧配置）
     */
    private XssConfig xss = new XssConfig();

    /**
     * SQL注入防护配置（兼容旧配置）
     */
    private SqlInjectionConfig sqlInjection = new SqlInjectionConfig();

    /**
     * 敏感词过滤配置（兼容旧配置）
     */
    private SensitiveWordConfig sensitiveWord = new SensitiveWordConfig();

    public static class RuleConfig {
        /**
         * 是否启用规则文件配置
         */
        private boolean enabled = true;

        /**
         * 是否启用内置规则
         */
        private boolean enableBuiltin = true;

        /**
         * 规则文件路径列表
         */
        private List<String> ruleFiles = new ArrayList<>();

        /**
         * 规则缓存配置
         */
        private CacheConfig cache = new CacheConfig();

        public static class CacheConfig {
            /**
             * 是否启用缓存
             */
            private boolean enabled = true;

            /**
             * 缓存过期时间（秒）
             */
            private long expireTime = 3600;

            /**
             * 最大缓存大小
             */
            private int maxSize = 1000;

            // Getters and Setters
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            public long getExpireTime() { return expireTime; }
            public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
            public int getMaxSize() { return maxSize; }
            public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
        }

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isEnableBuiltin() { return enableBuiltin; }
        public void setEnableBuiltin(boolean enableBuiltin) { this.enableBuiltin = enableBuiltin; }
        public List<String> getRuleFiles() { return ruleFiles; }
        public void setRuleFiles(List<String> ruleFiles) { this.ruleFiles = ruleFiles; }
        public CacheConfig getCache() { return cache; }
        public void setCache(CacheConfig cache) { this.cache = cache; }
    }

    public static class XssConfig {
        /**
         * 是否启用XSS防护
         */
        private boolean enabled = true;

        /**
         * 是否启用内置XSS规则
         */
        private boolean enableBuiltin = true;

        /**
         * 排除的URL模式（白名单）
         */
        private List<String> excludePatterns = new ArrayList<>();

        /**
         * XSS规则文件路径列表（classpath下）
         */
        private List<String> ruleFiles = new ArrayList<>();

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isEnableBuiltin() { return enableBuiltin; }
        public void setEnableBuiltin(boolean enableBuiltin) { this.enableBuiltin = enableBuiltin; }
        public List<String> getExcludePatterns() { return excludePatterns; }
        public void setExcludePatterns(List<String> excludePatterns) { this.excludePatterns = excludePatterns; }
        public List<String> getRuleFiles() { return ruleFiles; }
        public void setRuleFiles(List<String> ruleFiles) { this.ruleFiles = ruleFiles; }
    }

    public static class SqlInjectionConfig {
        /**
         * 是否启用SQL注入防护
         */
        private boolean enabled = true;

        /**
         * 是否启用内置SQL注入规则
         */
        private boolean enableBuiltin = true;

        /**
         * 排除的URL模式（白名单）
         */
        private List<String> excludePatterns = new ArrayList<>();

        /**
         * SQL注入规则文件路径列表（classpath下）
         */
        private List<String> ruleFiles = new ArrayList<>();

        /**
         * 危险SQL关键词（兼容旧配置）
         */
        private List<String> dangerousKeywords = new ArrayList<>();

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isEnableBuiltin() { return enableBuiltin; }
        public void setEnableBuiltin(boolean enableBuiltin) { this.enableBuiltin = enableBuiltin; }
        public List<String> getExcludePatterns() { return excludePatterns; }
        public void setExcludePatterns(List<String> excludePatterns) { this.excludePatterns = excludePatterns; }
        public List<String> getRuleFiles() { return ruleFiles; }
        public void setRuleFiles(List<String> ruleFiles) { this.ruleFiles = ruleFiles; }
        public List<String> getDangerousKeywords() { return dangerousKeywords; }
        public void setDangerousKeywords(List<String> dangerousKeywords) { this.dangerousKeywords = dangerousKeywords; }
    }

    public static class SensitiveWordConfig {
        /**
         * 是否启用敏感词过滤
         */
        private boolean enabled = true;

        /**
         * 是否启用内置敏感词
         */
        private boolean enableBuiltin = true;

        /**
         * 排除的URL模式（白名单）
         */
        private List<String> excludePatterns = new ArrayList<>();

        /**
         * 敏感词文件路径列表（classpath下）
         */
        private List<String> wordFiles = new ArrayList<>();

        /**
         * 替换字符
         */
        private String replaceChar = "*";

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isEnableBuiltin() { return enableBuiltin; }
        public void setEnableBuiltin(boolean enableBuiltin) { this.enableBuiltin = enableBuiltin; }
        public List<String> getExcludePatterns() { return excludePatterns; }
        public void setExcludePatterns(List<String> excludePatterns) { this.excludePatterns = excludePatterns; }
        public List<String> getWordFiles() { return wordFiles; }
        public void setWordFiles(List<String> wordFiles) { this.wordFiles = wordFiles; }
        public String getReplaceChar() { return replaceChar; }
        public void setReplaceChar(String replaceChar) { this.replaceChar = replaceChar; }
    }

    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public RuleConfig getRules() { return rules; }
    public void setRules(RuleConfig rules) { this.rules = rules; }
    public XssConfig getXss() { return xss; }
    public void setXss(XssConfig xss) { this.xss = xss; }
    public SqlInjectionConfig getSqlInjection() { return sqlInjection; }
    public void setSqlInjection(SqlInjectionConfig sqlInjection) { this.sqlInjection = sqlInjection; }
    public SensitiveWordConfig getSensitiveWord() { return sensitiveWord; }
    public void setSensitiveWord(SensitiveWordConfig sensitiveWord) { this.sensitiveWord = sensitiveWord; }
}
