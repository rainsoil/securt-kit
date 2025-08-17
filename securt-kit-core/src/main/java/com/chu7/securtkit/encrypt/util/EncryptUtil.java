package com.chu7.securtkit.encrypt.util;

import com.chu7.securtkit.encrypt.annotation.EncryptField;
import com.chu7.securtkit.encrypt.core.EncryptContext;
import com.chu7.securtkit.encrypt.strategy.EncryptStrategy;
import com.chu7.securtkit.encrypt.strategy.KeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 加密工具类
 * 提供便捷的加密解密方法
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@Component
public class EncryptUtil {
    
    @Autowired
    private List<EncryptStrategy> encryptStrategies;
    
    @Autowired
    private KeyManager keyManager;
    
    /**
     * 加密字符串
     *
     * @param plainText 明文
     * @param algorithm 算法
     * @return 密文
     */
    public String encrypt(String plainText, String algorithm) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        EncryptStrategy strategy = findEncryptStrategy(algorithm);
        if (strategy == null) {
            log.warn("未找到加密策略: {}", algorithm);
            return plainText;
        }
        
        try {
            String key = keyManager.getDefaultKey();
            return strategy.encrypt(plainText, key);
        } catch (Exception e) {
            log.error("加密失败: {}", e.getMessage(), e);
            return plainText;
        }
    }
    
    /**
     * 解密字符串
     *
     * @param cipherText 密文
     * @param algorithm 算法
     * @return 明文
     */
    public String decrypt(String cipherText, String algorithm) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        
        EncryptStrategy strategy = findEncryptStrategy(algorithm);
        if (strategy == null) {
            log.warn("未找到解密策略: {}", algorithm);
            return cipherText;
        }
        
        try {
            String key = keyManager.getDefaultKey();
            return strategy.decrypt(cipherText, key);
        } catch (Exception e) {
            log.error("解密失败: {}", e.getMessage(), e);
            return cipherText;
        }
    }
    
    /**
     * 加密对象字段
     *
     * @param obj 对象
     * @param tableName 表名
     */
    public void encryptObject(Object obj, String tableName) {
        if (obj == null || tableName == null) {
            return;
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            EncryptField encryptField = field.getAnnotation(EncryptField.class);
            if (encryptField != null && encryptField.enabled()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    
                    if (value != null && value instanceof String) {
                        String algorithm = encryptField.algorithm();
                        String encryptedValue = encrypt((String) value, algorithm);
                        field.set(obj, encryptedValue);
                        log.debug("加密对象字段: {}.{} -> {}", clazz.getSimpleName(), field.getName(), encryptedValue);
                    }
                } catch (Exception e) {
                    log.error("加密对象字段失败: {}.{}", clazz.getSimpleName(), field.getName(), e);
                }
            }
        }
    }
    
    /**
     * 解密对象字段
     *
     * @param obj 对象
     * @param tableName 表名
     */
    public void decryptObject(Object obj, String tableName) {
        if (obj == null || tableName == null) {
            return;
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            EncryptField encryptField = field.getAnnotation(EncryptField.class);
            if (encryptField != null && encryptField.enabled()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    
                    if (value != null && value instanceof String) {
                        String algorithm = encryptField.algorithm();
                        String decryptedValue = decrypt((String) value, algorithm);
                        field.set(obj, decryptedValue);
                        log.debug("解密对象字段: {}.{} -> {}", clazz.getSimpleName(), field.getName(), decryptedValue);
                    }
                } catch (Exception e) {
                    log.error("解密对象字段失败: {}.{}", clazz.getSimpleName(), field.getName(), e);
                }
            }
        }
    }
    
    /**
     * 创建加密上下文
     *
     * @param tableName 表名
     * @param fieldName 字段名
     * @param algorithm 算法
     * @return 加密上下文
     */
    public EncryptContext createContext(String tableName, String fieldName, String algorithm) {
        EncryptStrategy strategy = findEncryptStrategy(algorithm);
        
        return EncryptContext.builder()
                .tableName(tableName)
                .fieldName(fieldName)
                .algorithm(algorithm)
                .encryptStrategy(strategy)
                .keyManager(keyManager)
                .build();
    }
    
    /**
     * 查找加密策略
     *
     * @param algorithm 算法名称
     * @return 加密策略
     */
    private EncryptStrategy findEncryptStrategy(String algorithm) {
        final String finalAlgorithm = (algorithm == null || algorithm.trim().isEmpty()) ? "AES" : algorithm;
        
        return encryptStrategies.stream()
                .filter(strategy -> strategy.supports(finalAlgorithm))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 验证加密字段
     *
     * @param field 字段
     * @return 是否有效
     */
    public boolean isValidEncryptField(Field field) {
        if (field == null) {
            return false;
        }
        
        EncryptField encryptField = field.getAnnotation(EncryptField.class);
        if (encryptField == null || !encryptField.enabled()) {
            return false;
        }
        
        // 检查字段类型是否为String
        return String.class.equals(field.getType());
    }
    
    /**
     * 获取字段的加密算法
     *
     * @param field 字段
     * @return 算法名称
     */
    public String getFieldAlgorithm(Field field) {
        if (field == null) {
            return "AES";
        }
        
        EncryptField encryptField = field.getAnnotation(EncryptField.class);
        if (encryptField == null) {
            return "AES";
        }
        
        return encryptField.algorithm();
    }
} 