package com.chu7.securtkit.encrypt.strategy;

import cn.hutool.crypto.symmetric.DES;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * DES加密策略实现
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
public class DesEncryptStrategy implements EncryptStrategy {
    
    private static final String ALGORITHM = "DES";
    
    @Override
    public String encrypt(String plainText, String key) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            // 确保密钥长度为8字节
            String normalizedKey = normalizeKey(key);
            DES des = new DES(normalizedKey.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = des.encrypt(plainText);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("DES加密失败: {}", e.getMessage(), e);
            return plainText;
        }
    }
    
    @Override
    public String decrypt(String cipherText, String key) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        
        try {
            // 确保密钥长度为8字节
            String normalizedKey = normalizeKey(key);
            DES des = new DES(normalizedKey.getBytes(StandardCharsets.UTF_8));
            byte[] decrypted = des.decrypt(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("DES解密失败: {}", e.getMessage(), e);
            return cipherText;
        }
    }
    
    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }
    
    @Override
    public boolean supports(String algorithm) {
        return ALGORITHM.equalsIgnoreCase(algorithm);
    }
    
    /**
     * 标准化密钥长度
     * DES要求密钥长度为8字节
     */
    private String normalizeKey(String key) {
        if (key == null) {
            key = "default8";
        }
        
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length == 8) {
            return key;
        }
        
        // 如果长度不是8，则截取或填充到8字节
        byte[] normalizedBytes = new byte[8];
        System.arraycopy(keyBytes, 0, normalizedBytes, 0, Math.min(keyBytes.length, 8));
        
        // 如果原密钥长度不足8字节，用0填充
        for (int i = keyBytes.length; i < 8; i++) {
            normalizedBytes[i] = 0;
        }
        
        return new String(normalizedBytes, StandardCharsets.UTF_8);
    }
} 