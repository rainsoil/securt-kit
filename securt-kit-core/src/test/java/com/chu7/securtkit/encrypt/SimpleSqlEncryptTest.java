package com.chu7.securtkit.encrypt;

import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.config.EncryptProperties;
import com.chu7.securtkit.encrypt.visitor.DbEncryptStatementVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import com.chu7.securtkit.encrypt.config.TestConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单SQL加密测试类
 * 测试基本的SQL处理功能
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
public class SimpleSqlEncryptTest {
    
    @Autowired
    private TableFieldCache tableFieldCache;
    
    @Autowired
    private DbEncryptStatementVisitor dbEncryptStatementVisitor;
    
    @Autowired
    private EncryptProperties encryptProperties;
    
    @BeforeEach
    void setUp() {
        // 重置访问者状态
        dbEncryptStatementVisitor.reset();
        
        // 配置测试用的表字段缓存
        Set<String> userEncryptFields = new HashSet<>(Arrays.asList("phone", "email", "id_card"));
        tableFieldCache.addTableEncryptFields("user", userEncryptFields);
        tableFieldCache.addClassNameToTableName("com.chu7.securtkit.encrypt.entity.UserEntity", "user");
    }
    
    @Test
    void testSimpleSelectQuery() {
        // 测试简单的SELECT查询
        String simpleSql = "SELECT phone, email, username FROM user WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(simpleSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        assertNotEquals(simpleSql, processedSql);
        
        // 验证SELECT字段被正确解密
        assertTrue(processedSql.contains("AES_DECRYPT"));
        assertTrue(processedSql.contains("FROM_BASE64"));
        
        // 验证WHERE条件被正确加密
        assertTrue(processedSql.contains("TO_BASE64"));
        assertTrue(processedSql.contains("AES_ENCRYPT"));
        
        System.out.println("简单SELECT查询原始SQL: " + simpleSql);
        System.out.println("简单SELECT查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testSelectWithAlias() {
        // 测试带别名的SELECT查询
        String aliasSql = "SELECT u.phone, u.email, u.username FROM user u WHERE u.phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(aliasSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("带别名SELECT查询原始SQL: " + aliasSql);
        System.out.println("带别名SELECT查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testSelectWithMultipleConditions() {
        // 测试多条件的SELECT查询
        String multiConditionSql = "SELECT phone, email, username FROM user WHERE phone = ? AND email = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(multiConditionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多条件SELECT查询原始SQL: " + multiConditionSql);
        System.out.println("多条件SELECT查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testDeleteQuery() {
        // 测试DELETE查询
        String deleteSql = "DELETE FROM user WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(deleteSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE查询原始SQL: " + deleteSql);
        System.out.println("DELETE查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testInsertQuery() {
        // 测试INSERT查询
        String insertSql = "INSERT INTO user (username, phone, email, id_card, address) VALUES (?, ?, ?, ?, ?)";
        String processedSql = dbEncryptStatementVisitor.processSql(insertSql);
        
        // INSERT语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(insertSql, processedSql);
        
        System.out.println("INSERT查询: " + insertSql);
    }
    
    @Test
    void testUpdateQuery() {
        // 测试UPDATE查询
        String updateSql = "UPDATE user SET phone = ?, email = ?, id_card = ? WHERE id = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(updateSql);
        
        // UPDATE语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(updateSql, processedSql);
        
        System.out.println("UPDATE查询: " + updateSql);
    }
    
    @Test
    void testNoEncryptFieldsQuery() {
        // 测试不包含加密字段的查询
        String noEncryptSql = "SELECT username, address FROM user WHERE username = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(noEncryptSql);
        
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(noEncryptSql, processedSql);
        
        System.out.println("无加密字段查询: " + noEncryptSql);
    }
    
    @Test
    void testNoEncryptTableQuery() {
        // 测试不包含加密表的查询
        String noEncryptTableSql = "SELECT * FROM system_config WHERE config_key = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(noEncryptTableSql);
        
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(noEncryptTableSql, processedSql);
        
        System.out.println("无加密表查询: " + noEncryptTableSql);
    }
} 