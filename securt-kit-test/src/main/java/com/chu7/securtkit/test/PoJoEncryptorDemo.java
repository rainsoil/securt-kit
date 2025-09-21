//package com.chu7.securtkit.test;
//
//import com.chu7.securtkit.cache.EncryptorInstanceCache;
//import com.chu7.securtkit.cache.TableCache;
//import com.chu7.securtkit.config.properties.EncryptorProperties;
//import com.chu7.securtkit.encryptor.pojo.*;
//import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Arrays;
//
///**
// * POJO模式加解密功能演示程序
// * 直接运行此类可以测试各种加密算法的功能
// *
// * @author chu7
// * @date 2024/7/9 14:06
// */
//@Slf4j
//public class PoJoEncryptorDemo {
//
//    public static void main(String[] args) {
//        log.info("=== SecurtKit POJO模式加解密功能演示 ===");
//
//        // 1. 初始化配置
//        EncryptorProperties encryptorProperties = new EncryptorProperties();
//        encryptorProperties.setSecretKey("securt-kit-test-key-123456789");
//
//        // 2. 初始化加密策略缓存
//        EncryptorInstanceCache.clear();
//        EncryptorInstanceCache encryptorInstanceCache = new EncryptorInstanceCache();
//        encryptorInstanceCache.init(Arrays.asList(
//            new DefaultPoJoFieldEncryptorPattern(encryptorProperties),
//            new AesPoJoFieldEncryptorStrategy(encryptorProperties),
//            new Base64PoJoFieldEncryptorStrategy(),
//            new Md5PoJoFieldEncryptorStrategy()
//        ));
//
//        log.info("✅ 加密策略初始化完成");
//
//        // 3. 测试各种加密算法
//        testDesEncryption(encryptorProperties);
//        testAesEncryption(encryptorProperties);
//        testBase64Encoding();
//        testMd5Hashing();
//
//        log.info("=== 演示完成 ===");
//    }
//
//    /**
//     * 测试DES加密
//     */
//    private static void testDesEncryption(EncryptorProperties encryptorProperties) {
//        log.info("\n=== 测试DES加解密 ===");
//
//        FieldEncryptorStrategy<String> strategy = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
//
//        String originalText = "13800138000";
//        String encrypted = strategy.encryption(originalText);
//        String decrypted = strategy.decryption(encrypted);
//
//        log.info("原文: {}", originalText);
//        log.info("DES加密: {}", encrypted);
//        log.info("DES解密: {}", decrypted);
//
//        if (!originalText.equals(encrypted) && originalText.equals(decrypted)) {
//            log.info("✅ DES加解密测试通过");
//        } else {
//            log.error("❌ DES加解密测试失败");
//        }
//    }
//
//    /**
//     * 测试AES加密
//     */
//    private static void testAesEncryption(EncryptorProperties encryptorProperties) {
//        log.info("\n=== 测试AES加解密 ===");
//
//        FieldEncryptorStrategy<String> strategy = new AesPoJoFieldEncryptorStrategy(encryptorProperties);
//
//        String originalText = "test@example.com";
//        String encrypted = strategy.encryption(originalText);
//        String decrypted = strategy.decryption(encrypted);
//
//        log.info("原文: {}", originalText);
//        log.info("AES加密: {}", encrypted);
//        log.info("AES解密: {}", decrypted);
//
//        if (!originalText.equals(encrypted) && originalText.equals(decrypted)) {
//            log.info("✅ AES加解密测试通过");
//        } else {
//            log.error("❌ AES加解密测试失败");
//        }
//    }
//
//    /**
//     * 测试Base64编码
//     */
//    private static void testBase64Encoding() {
//        log.info("\n=== 测试Base64编解码 ===");
//
//        FieldEncryptorStrategy<String> strategy = new Base64PoJoFieldEncryptorStrategy();
//
//        String originalText = "110101199001011234";
//        String encoded = strategy.encryption(originalText);
//        String decoded = strategy.decryption(encoded);
//
//        log.info("原文: {}", originalText);
//        log.info("Base64编码: {}", encoded);
//        log.info("Base64解码: {}", decoded);
//
//        if (!originalText.equals(encoded) && originalText.equals(decoded)) {
//            log.info("✅ Base64编解码测试通过");
//        } else {
//            log.error("❌ Base64编解码测试失败");
//        }
//    }
//
//    /**
//     * 测试MD5哈希
//     */
//    private static void testMd5Hashing() {
//        log.info("\n=== 测试MD5哈希 ===");
//
//        FieldEncryptorStrategy<String> strategy = new Md5PoJoFieldEncryptorStrategy();
//
//        String originalText = "password123";
//        String hashed = strategy.encryption(originalText);
//        String result = strategy.decryption(hashed);
//
//        log.info("原文: {}", originalText);
//        log.info("MD5哈希: {}", hashed);
//        log.info("MD5解密尝试: {}", result);
//
//        if (!originalText.equals(hashed) && hashed.equals(result) && hashed.length() == 32) {
//            log.info("✅ MD5哈希测试通过");
//        } else {
//            log.error("❌ MD5哈希测试失败");
//        }
//    }
//}