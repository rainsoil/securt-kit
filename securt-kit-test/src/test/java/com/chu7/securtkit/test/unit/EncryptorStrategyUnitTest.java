package com.chu7.securtkit.test.unit;

import com.chu7.securtkit.config.properties.EncryptorProperties;
import com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.DefaultPoJoFieldEncryptorPattern;
import com.chu7.securtkit.encryptor.pojo.Md5PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 加密算法单元测试
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
public class EncryptorStrategyUnitTest {

    private EncryptorProperties encryptorProperties;

    @BeforeEach
    public void setUp() {
        encryptorProperties = new EncryptorProperties();
        encryptorProperties.setSecretKey("securt-kit-test-key-123456789");
    }

    @Test
    public void testDesEncryption() {
        log.info("=== 测试DES加解密 ===");
        
        FieldEncryptorStrategy<String> strategy = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        
        String originalText = "13800138000";
        String encrypted = strategy.encryption(originalText);
        String decrypted = strategy.decryption(encrypted);
        
        log.info("原文: {}", originalText);
        log.info("DES加密: {}", encrypted);
        log.info("DES解密: {}", decrypted);
        
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
        log.info("✅ DES加解密测试通过");
    }

    @Test
    public void testAesEncryption() {
        log.info("=== 测试AES加解密 ===");
        
        FieldEncryptorStrategy<String> strategy = new AesPoJoFieldEncryptorStrategy(encryptorProperties);
        
        String originalText = "test@example.com";
        String encrypted = strategy.encryption(originalText);
        String decrypted = strategy.decryption(encrypted);
        
        log.info("原文: {}", originalText);
        log.info("AES加密: {}", encrypted);
        log.info("AES解密: {}", decrypted);
        
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
        log.info("✅ AES加解密测试通过");
    }

    @Test
    public void testBase64Encoding() {
        log.info("=== 测试Base64编解码 ===");
        
        FieldEncryptorStrategy<String> strategy = new Base64PoJoFieldEncryptorStrategy();
        
        String originalText = "110101199001011234";
        String encoded = strategy.encryption(originalText);
        String decoded = strategy.decryption(encoded);
        
        log.info("原文: {}", originalText);
        log.info("Base64编码: {}", encoded);
        log.info("Base64解码: {}", decoded);
        
        assertNotEquals(originalText, encoded);
        assertEquals(originalText, decoded);
        log.info("✅ Base64编解码测试通过");
    }

    @Test
    public void testMd5Hashing() {
        log.info("=== 测试MD5哈希 ===");
        
        FieldEncryptorStrategy<String> strategy = new Md5PoJoFieldEncryptorStrategy();
        
        String originalText = "password123";
        String hashed = strategy.encryption(originalText);
        String result = strategy.decryption(hashed);
        
        log.info("原文: {}", originalText);
        log.info("MD5哈希: {}", hashed);
        log.info("MD5解密尝试: {}", result);
        
        assertNotEquals(originalText, hashed);
        assertEquals(hashed, result); // MD5无法解密，返回原密文
        assertEquals(32, hashed.length()); // MD5哈希长度固定为32位
        log.info("✅ MD5哈希测试通过");
    }

    @Test
    public void testNullAndEmptyValues() {
        log.info("=== 测试null和空值处理 ===");
        
        FieldEncryptorStrategy<String> strategy = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        
        // 测试null值
        assertNull(strategy.encryption(null));
        assertNull(strategy.decryption(null));
        
        // 测试空字符串
        String empty = "";
        String encryptedEmpty = strategy.encryption(empty);
        String decryptedEmpty = strategy.decryption(encryptedEmpty);
        assertEquals(empty, decryptedEmpty);
        
        log.info("✅ null和空值处理测试通过");
    }

    @Test
    public void testLongText() {
        log.info("=== 测试长文本加解密 ===");
        
        FieldEncryptorStrategy<String> strategy = new AesPoJoFieldEncryptorStrategy(encryptorProperties);
        
        String longText = "这是一个很长的测试文本，用来验证加密算法对长文本的处理能力。" +
                         "包含中文、English、数字123、特殊字符!@#$%^&*()等各种字符类型，" +
                         "确保加密算法能够正确处理各种编码和字符集。";
        
        String encrypted = strategy.encryption(longText);
        String decrypted = strategy.decryption(encrypted);
        
        log.info("原文长度: {}", longText.length());
        log.info("密文长度: {}", encrypted.length());
        log.info("解密后文本: {}", decrypted);
        
        assertNotEquals(longText, encrypted);
        assertEquals(longText, decrypted);
        log.info("✅ 长文本加解密测试通过");
    }

    @Test
    public void testSpecialCharacters() {
        log.info("=== 测试特殊字符处理 ===");
        
        FieldEncryptorStrategy<String> strategy = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        
        String specialText = "!@#$%^&*()_+-={}[]|\\:;\"'<>?,./~`";
        String encrypted = strategy.encryption(specialText);
        String decrypted = strategy.decryption(encrypted);
        
        log.info("特殊字符原文: {}", specialText);
        log.info("特殊字符密文: {}", encrypted);
        log.info("特殊字符解密: {}", decrypted);
        
        assertNotEquals(specialText, encrypted);
        assertEquals(specialText, decrypted);
        log.info("✅ 特殊字符处理测试通过");
    }
}