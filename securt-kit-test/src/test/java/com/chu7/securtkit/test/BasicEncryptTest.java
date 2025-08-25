package com.chu7.securtkit.test;

import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.config.EncryptProperties;
import com.chu7.securtkit.encrypt.visitor.DbEncryptStatementVisitor;
import com.chu7.securtkit.test.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基本加密测试类
 * 测试基本的加密解密功能
 *
 * @author chu7
 * @date 2025/8/15
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "securt-kit.encrypt.enabled=true",
    "securt-kit.encrypt.patternType=DB",
    "securt-kit.encrypt.key=test-secret-key-32-chars-long"
})
@Import(TestConfig.class)
public class BasicEncryptTest {
    
    @Autowired
    private TableFieldCache tableFieldCache;
    
    @Autowired
    private DbEncryptStatementVisitor dbEncryptStatementVisitor;
    
    @Autowired
    private EncryptProperties encryptProperties;
    
    @BeforeEach
    void setUp() {
        dbEncryptStatementVisitor.reset();
    }
    
    @Test
    void testBasicSelectEncryption() {
        String selectSql = "SELECT phone, email FROM user WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        assertNotEquals(selectSql, processedSql);
        
        System.out.println("原始SQL: " + selectSql);
        System.out.println("处理后SQL: " + processedSql);
    }
    
    @Test
    void testBasicInsertEncryption() {
        String insertSql = "INSERT INTO user (id, age) VALUES (?, ?)";
        String processedSql = dbEncryptStatementVisitor.processSql(insertSql);
        
        // INSERT语句通常不需要修改
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(insertSql, processedSql);
        
        System.out.println("INSERT语句: " + insertSql);
    }
    
    @Test
    void testConfiguration() {
        assertTrue(encryptProperties.isEnabled());
        assertEquals("DB", encryptProperties.getPatternType());
        assertNotNull(encryptProperties.getKey());
        
        System.out.println("配置测试通过");
    }
} 