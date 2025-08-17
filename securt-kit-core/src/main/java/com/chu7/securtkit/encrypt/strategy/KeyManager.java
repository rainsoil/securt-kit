package com.chu7.securtkit.encrypt.strategy;

/**
 * 密钥管理器接口
 * 负责密钥的获取、轮换和验证
 *
 * @author chu7
 * @date 2025/8/15
 */
public interface KeyManager {
    
    /**
     * 获取密钥
     *
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 密钥
     */
    String getKey(String tableName, String fieldName);
    
    /**
     * 获取默认密钥
     *
     * @return 默认密钥
     */
    String getDefaultKey();
    
    /**
     * 轮换密钥
     *
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 新密钥
     */
    String rotateKey(String tableName, String fieldName);
    
    /**
     * 验证密钥是否有效
     *
     * @param key 密钥
     * @return 是否有效
     */
    boolean isKeyValid(String key);
    
    /**
     * 生成新密钥
     *
     * @param algorithm 算法
     * @return 新密钥
     */
    String generateKey(String algorithm);
    
    /**
     * 存储密钥
     *
     * @param tableName 表名
     * @param fieldName 字段名
     * @param key 密钥
     */
    void storeKey(String tableName, String fieldName, String key);
} 