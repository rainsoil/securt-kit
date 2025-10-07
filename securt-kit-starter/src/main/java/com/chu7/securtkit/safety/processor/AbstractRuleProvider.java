package com.chu7.securtkit.safety.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用规则提供基类：负责关键词的加载、匹配与替换。
 */
public abstract class AbstractRuleProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Set<String> keywordCache = ConcurrentHashMap.newKeySet();

    /**
     * 加载内置与外部规则文件
     */
    protected void load(String builtinFile, List<String> externalFiles) {
        keywordCache.clear();
        if (builtinFile != null && !builtinFile.trim().isEmpty()) {
            keywordCache.addAll(readLines(builtinFile));
        }
        if (externalFiles != null) {
            for (String f : externalFiles) {
                keywordCache.addAll(readLines(f));
            }
        }
        log.info("loaded keywords: {}", keywordCache.size());
    }

    /** contains */
    protected boolean containsAny(String text) {
        if (text == null || text.isEmpty()) return false;
        String lc = text.toLowerCase();
        for (String k : keywordCache) {
            if (lc.contains(k.toLowerCase())) return true;
        }
        return false;
    }

    /** replace */
    protected String replaceAll(String text, String replaceChar) {
        if (text == null || text.isEmpty()) return text;
        String result = text;
        for (String k : keywordCache) {
            result = result.replaceAll("(?i)" + java.util.regex.Pattern.quote(k), replaceChar);
        }
        return result;
    }

    protected List<String> readLines(String path) {
        try {
            Resource r = new ClassPathResource(path);
            if (!r.exists()) r = new FileSystemResource(path);
            if (!r.exists()) return Collections.emptyList();
            List<String> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) list.add(line);
                }
            }
            return list;
        } catch (Exception e) {
            log.warn("read keyword file failed: {}", path, e);
            return Collections.emptyList();
        }
    }
}


