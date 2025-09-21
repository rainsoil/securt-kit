package com.chu7.securtkit.test;

import com.chu7.securtkit.cache.EncryptorInstanceCache;
import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.config.properties.EncryptorProperties;
import com.chu7.securtkit.encryptor.pojo.*;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * POJO模式加密功能单元测试
 * 由于没有JUnit依赖，使用简单的main方法进行测试
 *
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
public class PoJoEncryptionTest {

    private static EncryptorProperties encryptorProperties;

    public static void main(String[] args) {
        PoJoEncryptionTest test = new PoJoEncryptionTest();
        test.setUp();
        
        log.info("【测试开始】POJO模式加密功能测试");
        
        test.testDesEncryption();
        test.testAesEncryption();
        test.testBase64Encoding();
        test.testMd5Hashing();
        test.testNullValues();
        test.testEmptyString();
        test.testStrategyFromCache();
        
        log.info("【测试完成】所有测试执行完毕");
    }

    public void setUp() {
        // 初始化配置
        encryptorProperties = new EncryptorProperties();
        encryptorProperties.setSecretKey("test-key-123456789012345678");
        
        // 清空缓存
        EncryptorInstanceCache.clear();
        TableCache.clear();
        
        // 初始化策略实例
        EncryptorInstanceCache encryptorInstanceCache = new EncryptorInstanceCache();
        encryptorInstanceCache.init(Arrays.asList(
            new DefaultPoJoFieldEncryptorPattern(encryptorProperties),
            new AesPoJoFieldEncryptorStrategy(encryptorProperties),
            new Base64PoJoFieldEncryptorStrategy(),
            new Md5PoJoFieldEncryptorStrategy()
        ));
        
        log.info("【测试】初始化完成");
    }

    public void testDesEncryption() {
        DefaultPoJoFieldEncryptorPattern desStrategy = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        
        String originalText = "这是测试数据";
        String encrypted = desStrategy.encryption(originalText);
        String decrypted = desStrategy.decryption(encrypted);
        
        log.info("原文: {}", originalText);
        log.info("DES加密: {}", encrypted);
        log.info("DES解密: {}", decrypted);
        
        if (!originalText.equals(encrypted) && originalText.equals(decrypted)) {
            log.info("【测试通过】DES加解密测试");
        } else {
            log.error("【测试失败】DES加解密测试");
        }
    }

    public void testAesEncryption() {
        AesPoJoFieldEncryptorStrategy aesStrategy = new AesPoJoFieldEncryptorStrategy(encryptorProperties);
        
        String originalText = "这是AES测试数据";
        String encrypted = aesStrategy.encryption(originalText);
        String decrypted = aesStrategy.decryption(encrypted);
        
        log.info("原文: {}", originalText);
        log.info("AES加密: {}", encrypted);
        log.info("AES解密: {}", decrypted);
        
        if (!originalText.equals(encrypted) && originalText.equals(decrypted)) {
            log.info("【测试通过】AES加解密测试");
        } else {
            log.error("【测试失败】AES加解密测试");
        }
    }

    public void testBase64Encoding() {
        Base64PoJoFieldEncryptorStrategy base64Strategy = new Base64PoJoFieldEncryptorStrategy();
        
        String originalText = "这是Base64测试数据";
        String encoded = base64Strategy.encryption(originalText);
        String decoded = base64Strategy.decryption(encoded);
        
        log.info("原文: {}", originalText);
        log.info("Base64编码: {}", encoded);
        log.info("Base64解码: {}", decoded);
        
        if (!originalText.equals(encoded) && originalText.equals(decoded)) {
            log.info("【测试通过】Base64编解码测试");
        } else {
            log.error("【测试失败】Base64编解码测试");
        }
    }

    public void testMd5Hashing() {
        Md5PoJoFieldEncryptorStrategy md5Strategy = new Md5PoJoFieldEncryptorStrategy();
        
        String originalText = "这是MD5测试数据";
        String hashed = md5Strategy.encryption(originalText);
        String result = md5Strategy.decryption(hashed);
        
        log.info("原文: {}", originalText);
        log.info("MD5哈希: {}", hashed);
        log.info("MD5解密: {}", result);
        
        if (!originalText.equals(hashed) && hashed.equals(result) && hashed.length() == 32) {
            log.info("【测试通过】MD5哈希测试");
        } else {
            log.error("【测试失败】MD5哈希测试");
        }
    }

    public void testNullValues() {
        DefaultPoJoFieldEncryptorPattern desStrategy = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        
        String encryptedNull = desStrategy.encryption(null);
        String decryptedNull = desStrategy.decryption(null);
        
        if (encryptedNull == null && decryptedNull == null) {
            log.info("【测试通过】null值处理测试");
        } else {
            log.error("【测试失败】null值处理测试");
        }
    }

    public void testEmptyString() {
        DefaultPoJoFieldEncryptorPattern desStrategy = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        
        String empty = "";
        String encrypted = desStrategy.encryption(empty);
        String decrypted = desStrategy.decryption(encrypted);
        
        log.info("空字符串加密: {}", encrypted);
        
        if (empty.equals(decrypted)) {
            log.info("【测试通过】空字符串处理测试");
        } else {
            log.error("【测试失败】空字符串处理测试");
        }
    }

    public void testStrategyFromCache() {
        FieldEncryptorStrategy<String> strategy1 = EncryptorInstanceCache.getInstance(DefaultPoJoFieldEncryptorPattern.class);
        FieldEncryptorStrategy<String> strategy2 = EncryptorInstanceCache.getInstance(DefaultPoJoFieldEncryptorPattern.class);
        
        if (strategy1 != null && strategy2 != null && strategy1 == strategy2) {
            log.info("【测试通过】策略缓存测试");
        } else {
            log.error("【测试失败】策略缓存测试");
        }
    }
}