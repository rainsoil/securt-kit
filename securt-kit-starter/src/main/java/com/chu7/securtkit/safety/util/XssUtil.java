package com.chu7.securtkit.safety.util;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * XSS防护工具类
 * 支持规则文件配置和内置规则
 */
public class XssUtil {
    
    /**
     * 常见的XSS攻击模式
     */
    private static final Pattern[] XSS_PATTERNS = {
        // 脚本标签
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script[^>]*>", Pattern.CASE_INSENSITIVE),
        
        // 事件处理器
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        
        // JavaScript协议
        Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE),
        
        // 表达式
        Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 样式标签
        Pattern.compile("<style[^>]*>.*?</style>", Pattern.CASE_INSENSITIVE),
        
        // 链接标签
        Pattern.compile("<link[^>]*>", Pattern.CASE_INSENSITIVE),
        
        // 对象标签
        Pattern.compile("<object[^>]*>.*?</object>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*>", Pattern.CASE_INSENSITIVE),
        
        // 表单标签
        Pattern.compile("<form[^>]*>", Pattern.CASE_INSENSITIVE),
        
        // iframe标签
        Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE),
        
        // 注释中的脚本
        Pattern.compile("<!--.*?<script.*?-->", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        
        // 数据URI
        Pattern.compile("data\\s*:\\s*text/html", Pattern.CASE_INSENSITIVE),
        
        // VBScript
        Pattern.compile("vbscript\\s*:", Pattern.CASE_INSENSITIVE),
        
        // 其他危险标签
        Pattern.compile("<meta[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<base[^>]*>", Pattern.CASE_INSENSITIVE)
    };
    
    /**
     * 检测是否包含XSS攻击（使用规则文件）
     * @param input 输入内容
     * @return 是否包含XSS攻击
     */
    public static boolean containsXss(String input) {
        if (StrUtil.isBlank(input)) {
            return false;
        }
        
        // 优先使用规则文件检测
        if (XssRuleLoader.containsXss(input)) {
            return true;
        }
        
        // 兼容旧方法：检查XSS模式
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 过滤XSS攻击内容（使用规则文件）
     * @param input 输入内容
     * @return 过滤后的内容
     */
    public static String filterXss(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        // 优先使用规则文件过滤
        String result = XssRuleLoader.filterXss(input);
        if (!result.equals(input)) {
            return result;
        }
        
        // 兼容旧方法：移除所有脚本标签
        result = input.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        result = result.replaceAll("(?i)<script[^>]*>", "");
        
        // 兼容旧方法：移除事件处理器
        result = result.replaceAll("(?i)\\s*on\\w+\\s*=\\s*[\"'][^\"']*[\"']", "");
        result = result.replaceAll("(?i)\\s*on\\w+\\s*=\\s*[^\\s>]+", "");
        
        // 兼容旧方法：移除JavaScript协议
        result = result.replaceAll("(?i)javascript\\s*:", "");
        result = result.replaceAll("(?i)vbscript\\s*:", "");
        
        // 兼容旧方法：移除表达式
        result = result.replaceAll("(?i)expression\\s*\\(", "");
        
        // 兼容旧方法：移除样式标签
        result = result.replaceAll("(?i)<style[^>]*>.*?</style>", "");
        
        // 兼容旧方法：移除链接标签
        result = result.replaceAll("(?i)<link[^>]*>", "");
        
        // 兼容旧方法：移除对象标签
        result = result.replaceAll("(?i)<object[^>]*>.*?</object>", "");
        result = result.replaceAll("(?i)<embed[^>]*>", "");
        
        // 兼容旧方法：移除表单标签
        result = result.replaceAll("(?i)<form[^>]*>", "");
        
        // 兼容旧方法：移除iframe标签
        result = result.replaceAll("(?i)<iframe[^>]*>.*?</iframe>", "");
        
        // 兼容旧方法：移除meta和base标签
        result = result.replaceAll("(?i)<meta[^>]*>", "");
        result = result.replaceAll("(?i)<base[^>]*>", "");
        
        // 兼容旧方法：移除注释中的脚本
        result = result.replaceAll("(?i)<!--.*?<script.*?-->", "");
        
        // 兼容旧方法：移除数据URI
        result = result.replaceAll("(?i)data\\s*:\\s*text/html", "");
        
        return result;
    }
    
    /**
     * 过滤XSS攻击内容（兼容旧方法）
     * @param input 输入内容
     * @param allowedTags 允许的标签白名单
     * @param allowedAttributes 允许的属性白名单
     * @return 过滤后的内容
     */
    public static String filterXss(String input, java.util.List<String> allowedTags, 
                                   java.util.List<String> allowedAttributes) {
        // 使用规则文件过滤
        return filterXss(input);
    }
    
    /**
     * HTML编码（使用规则文件）
     * @param input 输入内容
     * @return 编码后的内容
     */
    public static String htmlEncode(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        // 优先使用规则文件编码
        String result = XssRuleLoader.htmlEncode(input);
        if (!result.equals(input)) {
            return result;
        }
        
        // 兼容旧方法：基本HTML编码
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }
    
    /**
     * HTML解码
     * @param input 输入内容
     * @return 解码后的内容
     */
    public static String htmlDecode(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        return input
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#x27;", "'")
            .replace("&#x2F;", "/");
    }
    
    /**
     * 清理HTML标签，只保留文本内容
     * @param input 输入内容
     * @return 清理后的内容
     */
    public static String stripHtml(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        return input.replaceAll("<[^>]+>", "");
    }
}
