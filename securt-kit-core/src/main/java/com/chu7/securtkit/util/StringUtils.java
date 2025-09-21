package com.chu7.securtkit.util;

import com.chu7.securtkit.constants.FieldConstant;
import com.chu7.securtkit.constants.SymbolConstant;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
public class StringUtils {
    
    private static final AtomicInteger PLACEHOLDER_COUNTER = new AtomicInteger(0);
    
    /**
     * 判断字符串是否为空
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 判断字符串是否为空（包括null和空字符串）
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 检查文本中是否不包含指定集合中的任何字符串
     */
    public static boolean notExist(String text, Collection<String> collection) {
        if (isBlank(text) || CollectionUtils.isEmpty(collection)) {
            return true;
        }
        
        String lowerText = text.toLowerCase();
        return collection.stream()
                .filter(StringUtils::isNotBlank)
                .noneMatch(lowerText::contains);
    }
    
    /**
     * 将SQL中的问号占位符替换为自定义占位符
     */
    public static String question2Placeholder(String sql) {
        if (isBlank(sql)) {
            return sql;
        }
        
        // 重置计数器
        PLACEHOLDER_COUNTER.set(0);
        
        // 使用正则表达式替换问号，但要避免替换字符串字面量中的问号
        Pattern pattern = Pattern.compile("\\?");
        Matcher matcher = pattern.matcher(sql);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement = FieldConstant.PLACEHOLDER + PLACEHOLDER_COUNTER.getAndIncrement();
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    /**
     * 将自定义占位符替换回问号
     */
    public static String placeholder2Question(String sql) {
        if (isBlank(sql)) {
            return sql;
        }
        
        Pattern pattern = Pattern.compile(FieldConstant.PLACEHOLDER + "\\d+");
        return pattern.matcher(sql).replaceAll(SymbolConstant.QUESTION_MARK);
    }
    
    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String str) {
        if (isBlank(str)) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        
        for (char c : str.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 去除字符串开头和结尾的指定字符
     *
     * @author liutangqi
     * @date 2025/5/30 11:24
     * @Param [str, c]
     **/
    public static String trim(String str, String c) {
        if (str == null || c == null || c.isEmpty() || str.isEmpty()) {
            return str;
        }

        int str1Len = str.length();
        int str2Len = c.length();

        // 如果 str2 比 str1 长，不可能匹配
        if (str2Len > str1Len) {
            return str;
        }

        int start = 0;
        int end = str1Len;

        // 处理开头的 str2 重复匹配
        while (start <= end - str2Len && str.startsWith(c, start)) {
            start += str2Len;
        }

        // 处理结尾的 str2 重复匹配
        while (end >= start + str2Len && str.startsWith(c, end - str2Len)) {
            end -= str2Len;
        }

        return (start > 0 || end < str1Len) ? str.substring(start, end) : str;
    }

    /**
     * 忽略大小写比较两个字符串是否相等
     */
    public static boolean equalCaseInsensitive(String a, String b) {
        if (isBlank(a) || isBlank(b)) {
            return false;
        }
        return a.toLowerCase().equals(b.toLowerCase());
    }

    /**
     * 忽略大小写，忽略开头结尾的 `  " 判断两个字段是否相等
     */
    public static boolean equalIgnoreFieldSymbol(String a, String b) {
        if (isBlank(a) || isBlank(b)) {
            return false;
        }
        //去掉首尾的 ` 、 "
        String clearA = trim(trim(a, SymbolConstant.FLOAT), SymbolConstant.DOUBLE_QUOTES);
        String clearB = trim(trim(b, SymbolConstant.FLOAT), SymbolConstant.DOUBLE_QUOTES);
        return equalCaseInsensitive(clearA, clearB);
    }
}