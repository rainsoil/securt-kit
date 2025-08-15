package com.chu7.securtkit.encrypt.strategy;

/**
 * 加密策略接口
 *
 * @author chu7
 * @date 2025/8/15
 */
public interface EncryptStrategy {
    
    /**
     * 加密
     *
     * @param plainText 明文
     * @param key 密钥
     * @return 密文
     */
    String encrypt(String plainText, String key);
    
    /**
     * 解密
     *
     * @param cipherText 密文
     * @param key 密钥
     * @return 明文
     */
    String decrypt(String cipherText, String key);
    
    /**
     * 获取算法名称
     *
     * @return 算法名称
     */
    String getAlgorithm();
    
    /**
     * 是否支持该算法
     *
     * @param algorithm 算法名称
     * @return 是否支持
     */
    boolean supports(String algorithm);
}
