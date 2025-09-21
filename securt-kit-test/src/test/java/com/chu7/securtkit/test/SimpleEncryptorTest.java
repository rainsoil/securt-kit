package com.chu7.securtkit.test;

import com.chu7.securtkit.config.EncryptorAutoConfiguration;
import com.chu7.securtkit.config.properties.SecurtKitProperties;
import com.chu7.securtkit.encryptor.pojo.DefaultPoJoFieldEncryptorPattern;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单加密器测试
 * 
 * @author chu7
 * @date 2025/1/1 10:00
 */
@SpringBootTest
@ContextConfiguration(classes = {EncryptorAutoConfiguration.class})
public class SimpleEncryptorTest {

    @Test
    public void testDefaultPoJoEncryption() {
        // 创建配置
        SecurtKitProperties properties = new SecurtKitProperties();
        properties.getEncryptor().setSecretKey("TEST_SECRET_KEY");
        
        // 创建加密器
        FieldEncryptorStrategy<String> encryptor = new DefaultPoJoFieldEncryptorPattern(properties.getEncryptor());
        
        // 测试数据
        String originalText = "13812345678";
        
        // 加密
        String encryptedText = encryptor.encryption(originalText);
        assertNotNull(encryptedText);
        assertNotEquals(originalText, encryptedText);
        System.out.println("原文: " + originalText);
        System.out.println("密文: " + encryptedText);
        
        // 解密
        String decryptedText = encryptor.decryption(encryptedText);
        assertNotNull(decryptedText);
        assertEquals(originalText, decryptedText);
        System.out.println("解密后: " + decryptedText);
    }
    
    @Test
    public void testNullHandling() {
        SecurtKitProperties properties = new SecurtKitProperties();
        properties.getEncryptor().setSecretKey("TEST_SECRET_KEY");
        
        FieldEncryptorStrategy<String> encryptor = new DefaultPoJoFieldEncryptorPattern(properties.getEncryptor());
        
        // 测试null值处理
        String encryptedNull = encryptor.encryption(null);
        assertNull(encryptedNull);
        
        String decryptedNull = encryptor.decryption(null);
        assertNull(decryptedNull);
    }
    
    @Test
    public void testEmptyStringHandling() {
        SecurtKitProperties properties = new SecurtKitProperties();
        properties.getEncryptor().setSecretKey("TEST_SECRET_KEY");
        
        FieldEncryptorStrategy<String> encryptor = new DefaultPoJoFieldEncryptorPattern(properties.getEncryptor());
        
        // 测试空字符串处理
        String emptyString = "";
        String encryptedEmpty = encryptor.encryption(emptyString);
        assertNotNull(encryptedEmpty);
        assertNotEquals(emptyString, encryptedEmpty);
        
        String decryptedEmpty = encryptor.decryption(encryptedEmpty);
        assertEquals(emptyString, decryptedEmpty);
    }
}
