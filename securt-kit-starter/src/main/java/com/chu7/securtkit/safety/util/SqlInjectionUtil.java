package com.chu7.securtkit.safety.util;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SQL注入防护工具类
 * 支持规则文件配置和内置规则
 */
public class SqlInjectionUtil {
    
    /**
     * 危险的SQL关键词
     */
    private static final List<String> DANGEROUS_KEYWORDS = Arrays.asList(
        "select", "insert", "update", "delete", "drop", "create", "alter", "exec", "execute",
        "union", "script", "iframe", "object", "embed", "form", "input", "textarea", "button",
        "or", "and", "xor", "not", "where", "having", "group", "order", "by", "from", "into",
        "values", "set", "table", "database", "schema", "index", "view", "procedure", "function",
        "trigger", "constraint", "primary", "foreign", "key", "unique", "check", "default",
        "null", "not null", "auto_increment", "identity", "sequence", "serial", "bigserial",
        "smallserial", "money", "smallmoney", "float", "real", "double", "precision", "decimal",
        "numeric", "bit", "tinyint", "smallint", "int", "integer", "bigint", "char", "varchar",
        "nchar", "nvarchar", "text", "ntext", "image", "binary", "varbinary", "timestamp",
        "datetime", "smalldatetime", "date", "time", "year", "interval", "boolean", "bool",
        "cursor", "table", "record", "array", "set", "enum", "json", "jsonb", "xml", "uuid",
        "inet", "cidr", "macaddr", "point", "line", "lseg", "box", "path", "polygon", "circle",
        "pg_lsn", "txid_snapshot", "user_defined", "composite", "range", "multirange",
        "domain", "pseudo", "unknown", "void", "any", "anyelement", "anyarray", "anynonarray",
        "anyenum", "anyrange", "cstring", "internal", "language_handler", "fdw_handler",
        "index_am_handler", "tsm_handler", "table_am_handler", "anycompatible", "anycompatiblearray",
        "anycompatiblenonarray", "anycompatiblerange", "pg_brin_bloom_summary",
        "pg_brin_minmax_multi_summary", "pg_mcv_list", "pg_ndistinct", "pg_dependencies",
        "pg_mcv_list", "pg_ndistinct", "pg_dependencies", "pg_mcv_list", "pg_ndistinct",
        "pg_dependencies", "pg_mcv_list", "pg_ndistinct", "pg_dependencies", "pg_mcv_list",
        "pg_ndistinct", "pg_dependencies", "pg_mcv_list", "pg_ndistinct", "pg_dependencies"
    );
    
    /**
     * 危险的SQL模式
     */
    private static final Pattern[] DANGEROUS_PATTERNS = {
        // 注释
        Pattern.compile("--.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile("/\\*.*?\\*/", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        
        // 联合查询
        Pattern.compile("union\\s+select", Pattern.CASE_INSENSITIVE),
        
        // 子查询
        Pattern.compile("\\(\\s*select\\s+", Pattern.CASE_INSENSITIVE),
        
        // 存储过程
        Pattern.compile("exec\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("execute\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("sp_", Pattern.CASE_INSENSITIVE),
        Pattern.compile("xp_", Pattern.CASE_INSENSITIVE),
        
        // 系统函数
        Pattern.compile("@@", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\$", Pattern.CASE_INSENSITIVE),
        
        // 时间延迟
        Pattern.compile("waitfor\\s+delay", Pattern.CASE_INSENSITIVE),
        Pattern.compile("sleep\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("benchmark\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 信息收集
        Pattern.compile("information_schema", Pattern.CASE_INSENSITIVE),
        Pattern.compile("sys\\.", Pattern.CASE_INSENSITIVE),
        Pattern.compile("pg_", Pattern.CASE_INSENSITIVE),
        
        // 文件操作
        Pattern.compile("load_file\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("into\\s+outfile", Pattern.CASE_INSENSITIVE),
        Pattern.compile("into\\s+dumpfile", Pattern.CASE_INSENSITIVE),
        
        // 布尔盲注
        Pattern.compile("and\\s+\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("or\\s+\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),
        
        // 时间盲注
        Pattern.compile("and\\s+if\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("or\\s+if\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 堆叠查询
        Pattern.compile(";\\s*", Pattern.CASE_INSENSITIVE),
        
        // 十六进制编码
        Pattern.compile("0x[0-9a-f]+", Pattern.CASE_INSENSITIVE),
        
        // 字符编码
        Pattern.compile("char\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("ascii\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("ord\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 字符串函数
        Pattern.compile("substring\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("substr\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("mid\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("left\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("right\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 数据库函数
        Pattern.compile("database\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("user\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("version\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("@@version", Pattern.CASE_INSENSITIVE),
        Pattern.compile("@@user", Pattern.CASE_INSENSITIVE),
        Pattern.compile("@@database", Pattern.CASE_INSENSITIVE),
        
        // 条件语句
        Pattern.compile("case\\s+when", Pattern.CASE_INSENSITIVE),
        Pattern.compile("if\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("iif\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 错误注入
        Pattern.compile("extractvalue\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("updatexml\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("exp\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("pow\\s*\\(", Pattern.CASE_INSENSITIVE),
        
        // 正则表达式
        Pattern.compile("regexp", Pattern.CASE_INSENSITIVE),
        Pattern.compile("rlike", Pattern.CASE_INSENSITIVE),
        
        // 其他危险模式
        Pattern.compile("\\'\\s*or\\s*\\'", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\\"\\s*or\\s*\\\"", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\'\\s*and\\s*\\'", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\\"\\s*and\\s*\\\"", Pattern.CASE_INSENSITIVE)
    };
    
    /**
     * 检测是否包含SQL注入攻击（使用规则文件）
     * @param input 输入内容
     * @return 是否包含SQL注入攻击
     */
    public static boolean containsSqlInjection(String input) {
        if (StrUtil.isBlank(input)) {
            return false;
        }
        
        // 优先使用规则文件检测
        if (SqlInjectionRuleLoader.containsSqlInjection(input)) {
            return true;
        }
        
        // 兼容旧方法：检查默认危险关键词
        String lowerInput = input.toLowerCase();
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }
        
        // 兼容旧方法：检查危险模式
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检测是否包含SQL注入攻击（兼容旧方法）
     * @param input 输入内容
     * @param customKeywords 自定义危险关键词
     * @return 是否包含SQL注入攻击
     */
    public static boolean containsSqlInjection(String input, List<String> customKeywords) {
        if (StrUtil.isBlank(input)) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        
        // 检查自定义关键词
        if (customKeywords != null && !customKeywords.isEmpty()) {
            for (String keyword : customKeywords) {
                if (lowerInput.contains(keyword.toLowerCase())) {
                    return true;
                }
            }
        }
        
        // 使用规则文件检测
        return containsSqlInjection(input);
    }
    
    /**
     * 获取匹配的SQL注入规则
     * @param input 输入内容
     * @return 匹配的规则列表
     */
    public static List<String> getMatchedRules(String input) {
        if (StrUtil.isBlank(input)) {
            return Arrays.asList();
        }
        
        // 优先使用规则文件检测
        List<String> matchedRules = SqlInjectionRuleLoader.getMatchedRules(input);
        if (!matchedRules.isEmpty()) {
            return matchedRules;
        }
        
        // 兼容旧方法：检查默认危险关键词
        List<String> result = new java.util.ArrayList<>();
        String lowerInput = input.toLowerCase();
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                result.add(keyword);
            }
        }
        
        return result;
    }
    
    /**
     * 过滤SQL注入攻击内容（使用规则文件）
     * @param input 输入内容
     * @return 过滤后的内容
     */
    public static String filterSqlInjection(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        // 优先使用规则文件过滤
        String result = SqlInjectionRuleLoader.filterSqlInjection(input);
        if (!result.equals(input)) {
            return result;
        }
        
        // 兼容旧方法：移除注释
        result = input.replaceAll("--.*", "");
        result = result.replaceAll("/\\*.*?\\*/", "");
        
        // 兼容旧方法：移除默认危险关键词
        for (String keyword : DANGEROUS_KEYWORDS) {
            result = result.replaceAll("(?i)\\b" + Pattern.quote(keyword) + "\\b", "");
        }
        
        // 兼容旧方法：移除危险模式
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            result = pattern.matcher(result).replaceAll("");
        }
        
        return result.trim();
    }
    
    /**
     * 过滤SQL注入攻击内容（兼容旧方法）
     * @param input 输入内容
     * @param customKeywords 自定义危险关键词
     * @return 过滤后的内容
     */
    public static String filterSqlInjection(String input, List<String> customKeywords) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        String result = input;
        
        // 移除注释
        result = result.replaceAll("--.*", "");
        result = result.replaceAll("/\\*.*?\\*/", "");
        
        // 移除危险关键词（替换为空）
        if (customKeywords != null && !customKeywords.isEmpty()) {
            for (String keyword : customKeywords) {
                result = result.replaceAll("(?i)\\b" + Pattern.quote(keyword) + "\\b", "");
            }
        }
        
        // 使用规则文件过滤
        return filterSqlInjection(result);
    }
    
    
    /**
     * 转义SQL特殊字符
     * @param input 输入内容
     * @return 转义后的内容
     */
    public static String escapeSql(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        return input
            .replace("'", "''")
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
