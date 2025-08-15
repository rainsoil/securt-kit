package com.chu7.securtkit.encrypt.strategy;

import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES加密策略实现
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
public class AesEncryptStrategy implements EncryptStrategy {
    
    private static final String ALGORITHM = "AES";
    
    @Override
    public String encrypt(String plainText, String key) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            // 确保密钥长度为16、24或32字节
            String normalizedKey = normalizeKey(key);
            AES aes = new AES(normalizedKey.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = aes.encrypt(plainText);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("AES加密失败: {}", e.getMessage(), e);
            return plainText;
        }
    }
    
    @Override
    public String decrypt(String cipherText, String key) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        
        try {
            // 确保密钥长度为16、24或32字节
            String normalizedKey = normalizeKey(key);
            AES aes = new AES(normalizedKey.getBytes(StandardCharsets.UTF_8));
            byte[] decrypted = aes.decrypt(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败: {}", e.getMessage(), e);
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
     * AES要求密钥长度为16、24或32字节
     */
    private String normalizeKey(String key) {
        if (key == null) {
            key = "default-key-16-chars";
        }
        
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32) {
            return key;
        }
        
        // 如果长度不是16、24、32，则截取或填充到16字节
        byte[] normalizedBytes = new byte[16];
        System.arraycopy(keyBytes, 0, normalizedBytes, 0, Math.min(keyBytes.length, 16));
        
        // 如果原密钥长度不足16字节，用0填充
        for (int i = keyBytes.length; i < 16; i++) {
            normalizedBytes[i] = 0;
        }
        
        return new String(normalizedBytes, StandardCharsets.UTF_8);
    }
}
