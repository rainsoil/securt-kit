package com.chu7.securtkit.safety.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全防护配置类
 * 支持XSS防护、SQL注入防护、敏感词过滤的配置
 * 支持规则文件配置和内置规则
 */
@Data
@Component
@ConfigurationProperties(prefix = "securt-kit.safety")
public class SafetyConfig {

    // Getters and Setters
    /**
     * 是否启用安全防护
     */
    private boolean enabled = true;


    /**
     * 根级URL白名单（统一生效）
     */
    private UrlWhitelistConfig urlWhitelist = new UrlWhitelistConfig();

    /**
     * 根级IP白名单（统一生效）
     */
    private IpWhitelistConfig ipWhitelist = new IpWhitelistConfig();

    /**
     * XSS防护配置
     */
    private XssConfig xss = new XssConfig();

    /**
     * SQL注入防护配置
     */
    private SqlInjectionConfig sqlInjection = new SqlInjectionConfig();

    /**
     * 敏感词过滤配置
     */
    private SensitiveWordConfig sensitiveWord = new SensitiveWordConfig();


    @Data
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
         * XSS检测引擎：regex 或 jsoup（默认 regex）
         * regex：基于关键字/正则的轻量检测
         * jsoup：基于Jsoup白名单的HTML清洗
         */
        private String engine = "regex";

        /**
         * 触发时处理动作：replace 或 block（默认 replace）
         */
        private String action = "replace";

        /**
         * 替换字符
         */
        private String replaceChar = "*";

        /**
         * 内置关键字文件路径
         */
        private String builtinKeywordFile = "xss-keywords/builtin/xss-keywords.txt";

        /**
         * 外部关键字文件路径列表
         */
        private List<String> externalKeywordFiles = new ArrayList<>();

        /**
         * URL白名单配置
         */
        private UrlWhitelistConfig urlWhitelist = new UrlWhitelistConfig();

        /**
         * IP白名单配置
         */
        private IpWhitelistConfig ipWhitelist = new IpWhitelistConfig();


    }

    @Data
    public static class UrlWhitelistConfig {
        /**
         * 是否启用URL白名单
         */
        private boolean enabled = true;

        /**
         * URL白名单列表（支持Ant路径模式）
         */
        private List<String> allowedUrls = new ArrayList<>();


    }

    @Data

    public static class IpWhitelistConfig {
        /**
         * 是否启用IP白名单
         */
        private boolean enabled = true;

        /**
         * 是否启用内置IP白名单规则
         */
        private boolean enableBuiltin = true;

        /**
         * 外部IP白名单配置文件路径列表
         */
        private List<String> configFiles = new ArrayList<>();

        /**
         * 直接配置的IP白名单列表
         */
        private List<String> allowedIps = new ArrayList<>();


    }

    @Data
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
         * 内置关键字文件路径
         */
        private String builtinKeywordFile = "sql-injection-keywords/builtin/sql-injection-keywords.txt";

        /**
         * 外部关键字文件路径列表
         */
        private List<String> externalKeywordFiles = new ArrayList<>();

        /**
         * URL白名单配置
         */
        private UrlWhitelistConfig urlWhitelist = new UrlWhitelistConfig();

        /**
         * IP白名单配置
         */
        private IpWhitelistConfig ipWhitelist = new IpWhitelistConfig();

        /**
         * 触发时处理动作：replace 或 block（默认 replace）
         */
        private String action = "replace";

        /**
         * 替换字符
         */
        private String replaceChar = "*";


    }

    @Data
    public static class SensitiveWordConfig {
        /**
         * 是否启用敏感词过滤
         */
        private boolean enabled = true;

        /**
         * 是否启用内置敏感词规则
         */
        private boolean enableBuiltin = true;
        /**
         * 触发时处理动作：replace 或 block（默认 replace）
         */
        private String action = "replace";

        /**
         * 内置关键字文件路径
         */
        private String builtinKeywordFile = "sensitive-word-keywords/builtin/sensitive-word-keywords.txt";

        /**
         * 外部关键字文件路径列表
         */
        private List<String> externalKeywordFiles = new ArrayList<>();

        /**
         * URL白名单配置
         */
        private UrlWhitelistConfig urlWhitelist = new UrlWhitelistConfig();

        /**
         * IP白名单配置
         */
        private IpWhitelistConfig ipWhitelist = new IpWhitelistConfig();

        /**
         * 替换字符
         */
        private String replaceChar = "*";


    }

}
