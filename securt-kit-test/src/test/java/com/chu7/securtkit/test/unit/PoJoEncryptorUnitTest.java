package com.chu7.securtkit.test.unit;

import com.chu7.securtkit.encryptor.pojo.*;
import com.chu7.securtkit.config.properties.EncryptorProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * POJO加密器单元测试
 * 测试各种加密策略的加密解密功能
 *
 * @author chu7
 * @date 2024/7/9 14:06
 */
@ExtendWith(MockitoExtension.class)
public class PoJoEncryptorUnitTest {

    @Mock
    private EncryptorProperties encryptorProperties;


    private DefaultPoJoFieldEncryptorPattern defaultEncryptor;
    private AesPoJoFieldEncryptorStrategy aesEncryptor;
    private Base64PoJoFieldEncryptorStrategy base64Encryptor;
    private Md5PoJoFieldEncryptorStrategy md5Encryptor;

    @BeforeEach
    public void setUp() {
        // 设置模拟对象
        when(encryptorProperties.getSecretKey()).thenReturn("12345678"); // 8位密钥用于DES

        // 初始化加密器
        defaultEncryptor = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        aesEncryptor = new AesPoJoFieldEncryptorStrategy(encryptorProperties);
        base64Encryptor = new Base64PoJoFieldEncryptorStrategy();
        md5Encryptor = new Md5PoJoFieldEncryptorStrategy();
    }

    /**
     * 测试默认DES加密器
     */
    @Test
    public void testDefaultEncryptor() {
        String plaintext = "Hello World";
        
        // 测试加密
        String ciphertext = defaultEncryptor.encryption(plaintext);
        assertNotNull(ciphertext);
        assertNotEquals(plaintext, ciphertext);
        
        // 测试解密
        String decryptedText = defaultEncryptor.decryption(ciphertext);
        assertEquals(plaintext, decryptedText);
    }

    /**
     * 测试默认DES加密器处理空值
     */
    @Test
    public void testDefaultEncryptorWithNull() {
        // 测试null值
        assertNull(defaultEncryptor.encryption(null));
        assertNull(defaultEncryptor.decryption(null));
        
        // 测试空字符串
        String emptyResult = defaultEncryptor.encryption("");
        assertNotNull(emptyResult);
        assertNotEquals("", emptyResult);
        
        String decryptedEmpty = defaultEncryptor.decryption(emptyResult);
        assertEquals("", decryptedEmpty);
    }

    /**
     * 测试AES加密器
     */
    @Test
    public void testAesEncryptor() {
        // 设置AES密钥（16位）
        when(encryptorProperties.getSecretKey()).thenReturn("1234567890123456");
        
        String plaintext = "Hello AES World";
        
        // 测试加密
        String ciphertext = aesEncryptor.encryption(plaintext);
        assertNotNull(ciphertext);
        assertNotEquals(plaintext, ciphertext);
        
        // 测试解密
        String decryptedText = aesEncryptor.decryption(ciphertext);
        assertEquals(plaintext, decryptedText);
    }

    /**
     * 测试AES加密器处理空值
     */
    @Test
    public void testAesEncryptorWithNull() {
        when(encryptorProperties.getSecretKey()).thenReturn("1234567890123456");
        
        // 测试null值
        assertNull(aesEncryptor.encryption(null));
        assertNull(aesEncryptor.decryption(null));
        
        // 测试空字符串
        String emptyResult = aesEncryptor.encryption("");
        assertNotNull(emptyResult);
        assertNotEquals("", emptyResult);
        
        String decryptedEmpty = aesEncryptor.decryption(emptyResult);
        assertEquals("", decryptedEmpty);
    }

    /**
     * 测试Base64编码器
     */
    @Test
    public void testBase64Encryptor() {
        String plaintext = "Hello Base64 World";
        
        // 测试编码
        String encoded = base64Encryptor.encryption(plaintext);
        assertNotNull(encoded);
        assertNotEquals(plaintext, encoded);
        
        // 测试解码
        String decoded = base64Encryptor.decryption(encoded);
        assertEquals(plaintext, decoded);
    }

    /**
     * 测试Base64编码器处理空值
     */
    @Test
    public void testBase64EncryptorWithNull() {
        // 测试null值
        assertNull(base64Encryptor.encryption(null));
        assertNull(base64Encryptor.decryption(null));
        
        // 测试空字符串
        String emptyResult = base64Encryptor.encryption("");
        assertNotNull(emptyResult);
        assertEquals("", emptyResult);
        
        String decodedEmpty = base64Encryptor.decryption(emptyResult);
        assertEquals("", decodedEmpty);
    }

    /**
     * 测试MD5哈希器
     */
    @Test
    public void testMd5Encryptor() {
        String plaintext = "Hello MD5 World";
        
        // 测试哈希
        String hashed = md5Encryptor.encryption(plaintext);
        assertNotNull(hashed);
        assertNotEquals(plaintext, hashed);
        assertEquals(32, hashed.length()); // MD5哈希长度为32
        
        // 测试解密（MD5是单向哈希，解密应该返回原值）
        String decrypted = md5Encryptor.decryption(hashed);
        assertEquals(hashed, decrypted); // MD5解密返回原哈希值
    }

    /**
     * 测试MD5哈希器处理空值
     */
    @Test
    public void testMd5EncryptorWithNull() {
        // 测试null值
        assertNull(md5Encryptor.encryption(null));
        assertNull(md5Encryptor.decryption(null));
        
        // 测试空字符串
        String emptyResult = md5Encryptor.encryption("");
        assertNotNull(emptyResult);
        assertEquals(32, emptyResult.length());
        
        String decryptedEmpty = md5Encryptor.decryption(emptyResult);
        assertEquals(emptyResult, decryptedEmpty);
    }

    /**
     * 测试特殊字符处理
     */
    @Test
    public void testSpecialCharacters() {
        String[] testCases = {
            "Hello, World!",
            "测试中文",
            "Special chars: !@#$%^&*()",
            "Unicode: \u4E2D\u6587",
            "Numbers: 1234567890",
            "Mixed: Hello123世界!@#"
        };

        for (String testCase : testCases) {
            // 测试默认加密器
            String encrypted = defaultEncryptor.encryption(testCase);
            String decrypted = defaultEncryptor.decryption(encrypted);
            assertEquals(testCase, decrypted);

            // 测试Base64编码器
            String base64Encoded = base64Encryptor.encryption(testCase);
            String base64Decoded = base64Encryptor.decryption(base64Encoded);
            assertEquals(testCase, base64Decoded);

            // 测试MD5哈希器
            String md5Hashed = md5Encryptor.encryption(testCase);
            assertNotNull(md5Hashed);
            assertEquals(32, md5Hashed.length());
        }
    }

    /**
     * 测试长文本处理
     */
    @Test
    public void testLongText() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("This is a long text for testing encryption and decryption. ");
        }
        String plaintext = longText.toString();

        // 测试默认加密器
        String encrypted = defaultEncryptor.encryption(plaintext);
        String decrypted = defaultEncryptor.decryption(encrypted);
        assertEquals(plaintext, decrypted);

        // 测试Base64编码器
        String base64Encoded = base64Encryptor.encryption(plaintext);
        String base64Decoded = base64Encryptor.decryption(base64Encoded);
        assertEquals(plaintext, base64Decoded);
    }

    /**
     * 测试加密结果的一致性
     */
    @Test
    public void testEncryptionConsistency() {
        String plaintext = "Consistency Test";
        
        // 多次加密同一文本，结果应该相同
        String encrypted1 = defaultEncryptor.encryption(plaintext);
        String encrypted2 = defaultEncryptor.encryption(plaintext);
        assertEquals(encrypted1, encrypted2);
        
        // 多次解密同一密文，结果应该相同
        String decrypted1 = defaultEncryptor.decryption(encrypted1);
        String decrypted2 = defaultEncryptor.decryption(encrypted1);
        assertEquals(decrypted1, decrypted2);
        assertEquals(plaintext, decrypted1);
    }
}
