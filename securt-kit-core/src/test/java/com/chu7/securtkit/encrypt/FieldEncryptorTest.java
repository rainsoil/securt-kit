package com.chu7.securtkit.encrypt;

import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.config.EncryptProperties;
import com.chu7.securtkit.encrypt.entity.UserEntity;
import com.chu7.securtkit.encrypt.strategy.AesEncryptStrategy;
import com.chu7.securtkit.encrypt.visitor.DbEncryptStatementVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import com.chu7.securtkit.encrypt.config.TestConfig;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字段加密器测试类
 *
 * @author chu7
 * @date 2025/8/15
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "securt-kit.encrypt.enabled=true",
    "securt-kit.encrypt.patternType=DB",
    "securt-kit.encrypt.key=test-secret-key-16-chars"
})
@Import(TestConfig.class)
public class FieldEncryptorTest {
    
    @Autowired
    private TableFieldCache tableFieldCache;
    
    @Autowired
    private DbEncryptStatementVisitor dbEncryptStatementVisitor;
    
    @Autowired
    private AesEncryptStrategy aesEncryptStrategy;
    
    @Autowired
    private EncryptProperties encryptProperties;
    
    @BeforeEach
    void setUp() {
        // 重置访问者状态
        dbEncryptStatementVisitor.reset();
    }
    
    @Test
    void testTableFieldCache() {
        // 测试表字段缓存
        assertTrue(tableFieldCache.hasEncryptFields("user"));
        
        Set<String> encryptFields = tableFieldCache.getTableEncryptFields("user");
        assertEquals(3, encryptFields.size());
        assertTrue(encryptFields.contains("phone"));
        assertTrue(encryptFields.contains("email"));
        assertTrue(encryptFields.contains("id_card"));
        
        assertTrue(tableFieldCache.isFieldEncrypted("user", "phone"));
        assertFalse(tableFieldCache.isFieldEncrypted("user", "username"));
    }
    
    @Test
    void testAesEncryptStrategy() {
        // 测试AES加密策略
        String plainText = "13800138001";
        String key = "test-secret-key-16-chars";
        
        String encrypted = aesEncryptStrategy.encrypt(plainText, key);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);
        
        String decrypted = aesEncryptStrategy.decrypt(encrypted, key);
        assertEquals(plainText, decrypted);
        
        assertTrue(aesEncryptStrategy.supports("AES"));
        assertFalse(aesEncryptStrategy.supports("DES"));
    }
    
    @Test
    void testDbEncryptStatementVisitor() {
        // 测试数据库加解密语句访问者
        
        // 测试SELECT语句
        String selectSql = "SELECT phone, email, username FROM user WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        assertNotEquals(selectSql, processedSql);
        
        // 验证SELECT字段被正确解密
        assertTrue(processedSql.contains("AES_DECRYPT"));
        assertTrue(processedSql.contains("FROM_BASE64"));
        
        // 验证WHERE条件被正确加密
        assertTrue(processedSql.contains("TO_BASE64"));
        assertTrue(processedSql.contains("AES_ENCRYPT"));
        
        System.out.println("原始SQL: " + selectSql);
        System.out.println("处理后SQL: " + processedSql);
    }
    
    @Test
    void testDbEncryptStatementVisitorWithAlias() {
        // 测试带别名的SQL
        String selectSqlWithAlias = "SELECT u.phone, u.email, u.username FROM user u WHERE u.phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSqlWithAlias);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("带别名原始SQL: " + selectSqlWithAlias);
        System.out.println("带别名处理后SQL: " + processedSql);
    }
    
    @Test
    void testDbEncryptStatementVisitorComplexQuery() {
        // 测试复杂查询
        String complexSql = "SELECT phone, email, username FROM user WHERE phone = ? AND email = ? ORDER BY username";
        String processedSql = dbEncryptStatementVisitor.processSql(complexSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("复杂查询原始SQL: " + complexSql);
        System.out.println("复杂查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testDbEncryptStatementVisitorNoEncryptFields() {
        // 测试不包含加密字段的SQL
        String noEncryptSql = "SELECT username, address FROM user WHERE username = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(noEncryptSql);
        
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(noEncryptSql, processedSql);
    }
    
    @Test
    void testEncryptProperties() {
        // 测试配置属性
        assertTrue(encryptProperties.isEnabled());
        assertEquals("AES", encryptProperties.getAlgorithm());
        assertEquals("test-secret-key-16-chars", encryptProperties.getKey());
        assertEquals("DB", encryptProperties.getPatternType());
        
        java.util.Map<String, List<String>> fields = encryptProperties.getFields();
        assertTrue(fields.containsKey("user"));
        assertEquals(3, fields.get("user").size());
    }
    
    @Test
    void testUserEntity() {
        // 测试用户实体类
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setIdCard("110101199001011234");
        user.setAddress("测试地址");
        
        assertEquals(1L, user.getId());
        assertEquals("测试用户", user.getUsername());
        assertEquals("13800138000", user.getPhone());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
        assertEquals("测试地址", user.getAddress());
    }
}
