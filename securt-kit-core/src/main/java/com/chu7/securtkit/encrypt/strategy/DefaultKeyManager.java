package com.chu7.securtkit.encrypt.strategy;

import com.chu7.securtkit.encrypt.config.EncryptProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认密钥管理器实现
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@Component
public class DefaultKeyManager implements KeyManager {
    
    @Autowired
    private EncryptProperties encryptProperties;
    
    /**
     * 密钥缓存：表名.字段名 -> 密钥
     */
    private final ConcurrentHashMap<String, String> keyCache = new ConcurrentHashMap<>();
    
    /**
     * 安全随机数生成器
     */
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Override
    public String getKey(String tableName, String fieldName) {
        if (tableName == null || fieldName == null) {
            return getDefaultKey();
        }
        
        String cacheKey = tableName + "." + fieldName;
        return keyCache.computeIfAbsent(cacheKey, k -> getDefaultKey());
    }
    
    @Override
    public String getDefaultKey() {
        String key = encryptProperties.getKey();
        if (key == null || key.trim().isEmpty()) {
            log.warn("未配置加密密钥，使用默认密钥");
            key = "default-secret-key-32-chars-long";
        }
        return key;
    }
    
    @Override
    public String rotateKey(String tableName, String fieldName) {
        String newKey = generateKey(encryptProperties.getAlgorithm());
        storeKey(tableName, fieldName, newKey);
        log.info("轮换密钥成功: {}.{}", tableName, fieldName);
        return newKey;
    }
    
    @Override
    public boolean isKeyValid(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        // 检查密钥长度
        int minLength = 16;
        int maxLength = 64;
        
        if (key.length() < minLength || key.length() > maxLength) {
            return false;
        }
        
        // 检查密钥是否包含有效字符
        return key.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$");
    }
    
    @Override
    public String generateKey(String algorithm) {
        int keyLength;
        
        switch (algorithm.toUpperCase()) {
            case "AES":
                keyLength = 32; // AES-256
                break;
            case "DES":
                keyLength = 8;  // DES
                break;
            default:
                keyLength = 32; // 默认32字节
                break;
        }
        
        byte[] keyBytes = new byte[keyLength];
        secureRandom.nextBytes(keyBytes);
        
        // 使用Base64编码，确保密钥只包含可打印字符
        String key = Base64.getEncoder().encodeToString(keyBytes);
        
        // 截取到指定长度
        if (key.length() > keyLength) {
            key = key.substring(0, keyLength);
        }
        
        log.debug("生成新密钥: 算法={}, 长度={}", algorithm, key.length());
        return key;
    }
    
    @Override
    public void storeKey(String tableName, String fieldName, String key) {
        if (tableName == null || fieldName == null || key == null) {
            log.warn("存储密钥参数无效");
            return;
        }
        
        if (!isKeyValid(key)) {
            log.warn("密钥格式无效，无法存储");
            return;
        }
        
        String cacheKey = tableName + "." + fieldName;
        keyCache.put(cacheKey, key);
        log.debug("存储密钥: {}.{}", tableName, fieldName);
    }
    
    /**
     * 清除密钥缓存
     */
    public void clearKeyCache() {
        keyCache.clear();
        log.info("清除密钥缓存");
    }
    
    /**
     * 获取密钥缓存统计信息
     */
    public int getKeyCacheSize() {
        return keyCache.size();
    }
} 