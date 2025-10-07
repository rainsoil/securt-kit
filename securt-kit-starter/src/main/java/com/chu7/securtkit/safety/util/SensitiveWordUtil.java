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

/**
 * 敏感词工具类
 * 支持从classpath下的多个文件读取敏感词，支持内置敏感词
 */
public class SensitiveWordUtil {
    
    private static final Logger log = LoggerFactory.getLogger(SensitiveWordUtil.class);
    
    /**
     * 敏感词缓存
     */
    private static final Map<String, Set<String>> SENSITIVE_WORD_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 内置敏感词文件路径
     */
    private static final List<String> BUILTIN_WORD_FILES = Arrays.asList(
        "sensitive-words/builtin/political.txt",
        "sensitive-words/builtin/violence.txt",
        "sensitive-words/builtin/pornography.txt"
    );
    
    /**
     * 初始化敏感词库
     * @param wordFiles 敏感词文件路径列表
     * @param enableBuiltin 是否启用内置敏感词
     */
    public static void initSensitiveWords(List<String> wordFiles, boolean enableBuiltin) {
        // 清空缓存
        SENSITIVE_WORD_CACHE.clear();
        
        // 加载内置敏感词
        if (enableBuiltin) {
            for (String builtinFile : BUILTIN_WORD_FILES) {
                try {
                    loadSensitiveWordsFromFile(builtinFile);
                } catch (Exception e) {
                    log.warn("加载内置敏感词文件失败: {}", builtinFile, e);
                }
            }
            log.info("内置敏感词库加载完成，共加载 {} 个文件", BUILTIN_WORD_FILES.size());
        }
        
        // 加载自定义敏感词文件
        if (wordFiles != null && !wordFiles.isEmpty()) {
            for (String filePath : wordFiles) {
                try {
                    loadSensitiveWordsFromFile(filePath);
                } catch (Exception e) {
                    log.error("加载敏感词文件失败: {}", filePath, e);
                }
            }
            log.info("自定义敏感词库加载完成，共加载 {} 个文件", wordFiles.size());
        }
        
        log.info("敏感词库初始化完成，总缓存文件数: {}", SENSITIVE_WORD_CACHE.size());
    }
    
    /**
     * 初始化敏感词库（兼容旧方法）
     * @param wordFiles 敏感词文件路径列表
     */
    public static void initSensitiveWords(List<String> wordFiles) {
        initSensitiveWords(wordFiles, true);
    }
    
    /**
     * 从文件加载敏感词
     */
    private static void loadSensitiveWordsFromFile(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        if (!resource.exists()) {
            log.warn("敏感词文件不存在: {}", filePath);
            return;
        }
        
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StrUtil.isNotBlank(line) && !line.startsWith("#")) {
                    words.add(line.toLowerCase());
                }
            }
        }
        
        SENSITIVE_WORD_CACHE.put(filePath, words);
        log.info("从文件 {} 加载了 {} 个敏感词", filePath, words.size());
    }
    
    /**
     * 检查文本是否包含敏感词
     * @param text 待检查的文本
     * @return 包含的敏感词列表
     */
    public static List<String> findSensitiveWords(String text) {
        if (StrUtil.isBlank(text)) {
            return Collections.emptyList();
        }
        
        List<String> foundWords = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (Set<String> wordSet : SENSITIVE_WORD_CACHE.values()) {
            for (String word : wordSet) {
                if (lowerText.contains(word)) {
                    foundWords.add(word);
                }
            }
        }
        
        return foundWords;
    }
    
    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @param replaceChar 替换字符
     * @return 过滤后的文本
     */
    public static String filterSensitiveWords(String text, String replaceChar) {
        if (StrUtil.isBlank(text)) {
            return text;
        }
        
        String result = text;
        String lowerText = text.toLowerCase();
        
        for (Set<String> wordSet : SENSITIVE_WORD_CACHE.values()) {
            for (String word : wordSet) {
                if (lowerText.contains(word)) {
                    result = result.replaceAll("(?i)" + word, 
                        StrUtil.repeat(replaceChar, word.length()));
                }
            }
        }
        
        return result;
    }
    
    /**
     * 检查是否包含敏感词
     * @param text 待检查的文本
     * @return 是否包含敏感词
     */
    public static boolean containsSensitiveWords(String text) {
        return !findSensitiveWords(text).isEmpty();
    }
    
    /**
     * 清空缓存
     */
    public static void clearCache() {
        SENSITIVE_WORD_CACHE.clear();
        log.info("敏感词缓存已清空");
    }
    
    /**
     * 获取缓存统计信息
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : SENSITIVE_WORD_CACHE.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }
}
