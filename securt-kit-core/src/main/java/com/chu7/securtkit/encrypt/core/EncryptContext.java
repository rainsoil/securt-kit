package com.chu7.securtkit.encrypt.core;

import com.chu7.securtkit.encrypt.strategy.EncryptStrategy;
import com.chu7.securtkit.encrypt.strategy.KeyManager;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密上下文
 * 管理加密操作的上下文信息
 *
 * @author chu7
 * @date 2025/8/15
 */
@Data
public class EncryptContext {
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 字段名
     */
    private String fieldName;
    
    /**
     * 加密算法
     */
    private String algorithm;
    
    /**
     * 加密策略
     */
    private EncryptStrategy encryptStrategy;
    
    /**
     * 密钥管理器
     */
    private KeyManager keyManager;
    
    /**
     * 是否启用加密
     */
    private boolean enabled = true;
    
    /**
     * 上下文属性
     */
    private Map<String, Object> attributes = new ConcurrentHashMap<>();
    
    public EncryptContext() {}
    
    public EncryptContext(String tableName, String fieldName) {
        this.tableName = tableName;
        this.fieldName = fieldName;
    }
    
    public EncryptContext(String tableName, String fieldName, String algorithm) {
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.algorithm = algorithm;
    }
    
    /**
     * 设置属性
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * 获取属性
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * 获取属性，带默认值
     */
    public Object getAttribute(String key, Object defaultValue) {
        return attributes.getOrDefault(key, defaultValue);
    }
    
    /**
     * 移除属性
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    /**
     * 清空属性
     */
    public void clearAttributes() {
        attributes.clear();
    }
    
    /**
     * 获取密钥
     */
    public String getKey() {
        if (keyManager != null) {
            return keyManager.getKey(tableName, fieldName);
        }
        return null;
    }
    
    /**
     * 加密
     */
    public String encrypt(String plainText) {
        if (!enabled || encryptStrategy == null) {
            return plainText;
        }
        
        String key = getKey();
        if (key == null) {
            return plainText;
        }
        
        return encryptStrategy.encrypt(plainText, key);
    }
    
    /**
     * 解密
     */
    public String decrypt(String cipherText) {
        if (!enabled || encryptStrategy == null) {
            return cipherText;
        }
        
        String key = getKey();
        if (key == null) {
            return cipherText;
        }
        
        return encryptStrategy.decrypt(cipherText, key);
    }
    
    /**
     * 创建构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 构建器
     */
    public static class Builder {
        private EncryptContext context = new EncryptContext();
        
        public Builder tableName(String tableName) {
            context.setTableName(tableName);
            return this;
        }
        
        public Builder fieldName(String fieldName) {
            context.setFieldName(fieldName);
            return this;
        }
        
        public Builder algorithm(String algorithm) {
            context.setAlgorithm(algorithm);
            return this;
        }
        
        public Builder encryptStrategy(EncryptStrategy encryptStrategy) {
            context.setEncryptStrategy(encryptStrategy);
            return this;
        }
        
        public Builder keyManager(KeyManager keyManager) {
            context.setKeyManager(keyManager);
            return this;
        }
        
        public Builder enabled(boolean enabled) {
            context.setEnabled(enabled);
            return this;
        }
        
        public Builder attribute(String key, Object value) {
            context.setAttribute(key, value);
            return this;
        }
        
        public EncryptContext build() {
            return context;
        }
    }
} 