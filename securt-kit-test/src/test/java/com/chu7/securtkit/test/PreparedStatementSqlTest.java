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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预处理SQL加密测试类
 * 测试各种预处理SQL场景下的加密解密功能
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
public class PreparedStatementSqlTest {
    
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
        tableFieldCache.addClassNameToTableName("com.chu7.securtkit.test.entity.UserEntity", "user");
        
        // 配置订单表的加密字段
        Set<String> orderEncryptFields = new HashSet<>(Arrays.asList("customer_name", "customer_phone", "customer_email", "delivery_address"));
        tableFieldCache.addTableEncryptFields("orders", orderEncryptFields);
        tableFieldCache.addClassNameToTableName("com.chu7.securtkit.test.entity.OrderEntity", "orders");
    }
    
    // ==================== 查询SQL测试 ====================
    
    @Test
    void testSimpleSelectWithPreparedStatement() {
        // 测试简单的SELECT查询（使用?占位符）
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
        
        System.out.println("简单SELECT查询原始SQL: " + selectSql);
        System.out.println("简单SELECT查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testSelectWithMultipleConditions() {
        // 测试多条件的SELECT查询
        String selectSql = "SELECT phone, email, username FROM user WHERE phone = ? AND email = ? AND username = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多条件SELECT查询原始SQL: " + selectSql);
        System.out.println("多条件SELECT查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testSelectWithLikeCondition() {
        // 测试带LIKE条件的SELECT查询
        String selectSql = "SELECT phone, email, username FROM user WHERE phone LIKE ? AND email LIKE ?";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("LIKE条件SELECT查询原始SQL: " + selectSql);
        System.out.println("LIKE条件SELECT查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testSelectWithInCondition() {
        // 测试带IN条件的SELECT查询
        String selectSql = "SELECT phone, email, username FROM user WHERE phone IN (?, ?, ?)";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("IN条件SELECT查询原始SQL: " + selectSql);
        System.out.println("IN条件SELECT查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testSelectWithBetweenCondition() {
        // 测试带BETWEEN条件的SELECT查询
        String selectSql = "SELECT phone, email, username FROM user WHERE phone BETWEEN ? AND ?";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("BETWEEN条件SELECT查询原始SQL: " + selectSql);
        System.out.println("BETWEEN条件SELECT查询处理后SQL: " + processedSql);
    }
    
    // ==================== 插入SQL测试 ====================
    
    @Test
    void testSimpleInsertWithPreparedStatement() {
        // 测试简单的INSERT语句（使用?占位符）
        String insertSql = "INSERT INTO user (username, phone, email, id_card, address) VALUES (?, ?, ?, ?, ?)";
        String processedSql = dbEncryptStatementVisitor.processSql(insertSql);
        
        // INSERT语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(insertSql, processedSql);
        
        System.out.println("简单INSERT语句: " + insertSql);
    }
    
    @Test
    void testInsertWithMultipleValues() {
        // 测试多值插入的INSERT语句
        String insertSql = "INSERT INTO user (username, phone, email, id_card, address) VALUES " +
                "(?, ?, ?, ?, ?), " +
                "(?, ?, ?, ?, ?), " +
                "(?, ?, ?, ?, ?)";
        String processedSql = dbEncryptStatementVisitor.processSql(insertSql);
        
        // INSERT语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(insertSql, processedSql);
        
        System.out.println("多值INSERT语句: " + insertSql);
    }
    
    @Test
    void testInsertWithSelect() {
        // 测试INSERT INTO SELECT语句
        String insertSelectSql = "INSERT INTO user_backup (username, phone, email, id_card) " +
                "SELECT username, phone, email, id_card FROM user WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(insertSelectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("INSERT INTO SELECT原始SQL: " + insertSelectSql);
        System.out.println("INSERT INTO SELECT处理后SQL: " + processedSql);
    }
    
    @Test
    void testInsertWithOnDuplicateKeyUpdate() {
        // 测试带ON DUPLICATE KEY UPDATE的INSERT语句
        String insertSql = "INSERT INTO user (username, phone, email, id_card) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE phone = ?, email = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(insertSql);
        
        // INSERT语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(insertSql, processedSql);
        
        System.out.println("ON DUPLICATE KEY UPDATE INSERT语句: " + insertSql);
    }
    
    // ==================== 更新SQL测试 ====================
    
    @Test
    void testSimpleUpdateWithPreparedStatement() {
        // 测试简单的UPDATE语句（使用?占位符）
        String updateSql = "UPDATE user SET phone = ?, email = ?, id_card = ? WHERE id = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(updateSql);
        
        // UPDATE语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(updateSql, processedSql);
        
        System.out.println("简单UPDATE语句: " + updateSql);
    }
    
    @Test
    void testUpdateWithMultipleConditions() {
        // 测试多条件的UPDATE语句
        String updateSql = "UPDATE user SET phone = ?, email = ? WHERE id = ? AND username = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(updateSql);
        
        // UPDATE语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(updateSql, processedSql);
        
        System.out.println("多条件UPDATE语句: " + updateSql);
    }
    
    @Test
    void testUpdateWithJoin() {
        // 测试带JOIN的UPDATE语句
        String updateJoinSql = "UPDATE user u " +
                "SET u.phone = o.customer_phone " +
                "FROM orders o " +
                "WHERE u.id = o.user_id " +
                "AND o.customer_phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(updateJoinSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE JOIN原始SQL: " + updateJoinSql);
        System.out.println("UPDATE JOIN处理后SQL: " + processedSql);
    }
    
    @Test
    void testUpdateWithSubQuery() {
        // 测试带子查询的UPDATE语句
        String updateSubQuerySql = "UPDATE user u " +
                "SET u.phone = (" +
                "    SELECT customer_phone " +
                "    FROM orders " +
                "    WHERE user_id = u.id " +
                "    ORDER BY create_time DESC " +
                "    LIMIT 1" +
                ") " +
                "WHERE u.phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(updateSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE子查询原始SQL: " + updateSubQuerySql);
        System.out.println("UPDATE子查询处理后SQL: " + processedSql);
    }
    
    // ==================== 删除SQL测试 ====================
    
    @Test
    void testSimpleDeleteWithPreparedStatement() {
        // 测试简单的DELETE语句（使用?占位符）
        String deleteSql = "DELETE FROM user WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(deleteSql);
        
        // DELETE语句的WHERE条件需要加密
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("简单DELETE语句原始SQL: " + deleteSql);
        System.out.println("简单DELETE语句处理后SQL: " + processedSql);
    }
    
    @Test
    void testDeleteWithMultipleConditions() {
        // 测试多条件的DELETE语句
        String deleteSql = "DELETE FROM user WHERE phone = ? AND email = ? AND username = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(deleteSql);
        
        // DELETE语句的WHERE条件需要加密
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多条件DELETE语句原始SQL: " + deleteSql);
        System.out.println("多条件DELETE语句处理后SQL: " + processedSql);
    }
    
    @Test
    void testDeleteWithJoin() {
        // 测试带JOIN的DELETE语句
        String deleteJoinSql = "DELETE u " +
                "FROM user u " +
                "INNER JOIN orders o ON u.id = o.user_id " +
                "WHERE o.customer_phone = ? " +
                "AND u.phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(deleteJoinSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE JOIN原始SQL: " + deleteJoinSql);
        System.out.println("DELETE JOIN处理后SQL: " + processedSql);
    }
    
    @Test
    void testDeleteWithSubQuery() {
        // 测试带子查询的DELETE语句
        String deleteSubQuerySql = "DELETE FROM user " +
                "WHERE phone IN (" +
                "    SELECT customer_phone " +
                "    FROM orders " +
                "    WHERE customer_phone = ?" +
                ")";
        String processedSql = dbEncryptStatementVisitor.processSql(deleteSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE子查询原始SQL: " + deleteSubQuerySql);
        System.out.println("DELETE子查询处理后SQL: " + processedSql);
    }
    
    // ==================== 复杂SQL测试 ====================
    
    @Test
    void testComplexSelectWithJoin() {
        // 测试带JOIN的SELECT查询
        String selectSql = "SELECT u.phone, u.email, o.customer_name, o.customer_phone " +
                "FROM user u INNER JOIN orders o ON u.id = o.user_id " +
                "WHERE u.phone = ? AND o.customer_phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(selectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("JOIN查询原始SQL: " + selectSql);
        System.out.println("JOIN查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithAggregateFunctions() {
        // 测试带聚合函数的复杂SELECT查询
        String aggregateSql = "SELECT " +
                "COUNT(*) as total_users, " +
                "MAX(phone) as max_phone, " +
                "MIN(email) as min_email, " +
                "AVG(CAST(SUBSTRING(phone, 1, 3) AS DECIMAL)) as avg_area_code " +
                "FROM user " +
                "WHERE phone LIKE ? AND email LIKE ?";
        String processedSql = dbEncryptStatementVisitor.processSql(aggregateSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("聚合函数查询原始SQL: " + aggregateSql);
        System.out.println("聚合函数查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithWindowFunctions() {
        // 测试带窗口函数的复杂SELECT查询
        String windowSql = "SELECT " +
                "username, " +
                "phone, " +
                "email, " +
                "ROW_NUMBER() OVER (PARTITION BY phone ORDER BY username) as row_num, " +
                "RANK() OVER (ORDER BY phone) as phone_rank " +
                "FROM user " +
                "WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(windowSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("窗口函数查询原始SQL: " + windowSql);
        System.out.println("窗口函数查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithCaseWhen() {
        // 测试带CASE WHEN的复杂SELECT查询
        String caseWhenSql = "SELECT " +
                "username, " +
                "phone, " +
                "email, " +
                "CASE " +
                "    WHEN phone LIKE '138%' THEN '移动' " +
                "    WHEN phone LIKE '139%' THEN '联通' " +
                "    ELSE '其他' " +
                "END as operator " +
                "FROM user " +
                "WHERE phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(caseWhenSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("CASE WHEN查询原始SQL: " + caseWhenSql);
        System.out.println("CASE WHEN查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithExists() {
        // 测试带EXISTS的复杂SELECT查询
        String existsSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email " +
                "FROM user u " +
                "WHERE EXISTS (" +
                "    SELECT 1 " +
                "    FROM orders o " +
                "    WHERE o.user_id = u.id " +
                "    AND o.customer_phone = ?" +
                ") " +
                "AND u.phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(existsSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("EXISTS查询原始SQL: " + existsSql);
        System.out.println("EXISTS查询处理后SQL: " + processedSql);
    }
    
    // ==================== 边界情况测试 ====================
    
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
    
    @Test
    void testMixedEncryptAndNonEncryptFields() {
        // 测试混合加密和非加密字段的查询
        String mixedSql = "SELECT username, phone, email, address FROM user WHERE phone = ? AND username = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(mixedSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("混合字段查询原始SQL: " + mixedSql);
        System.out.println("混合字段查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testMultipleTablesWithDifferentEncryptFields() {
        // 测试多表不同加密字段的查询
        String multiTableSql = "SELECT " +
                "u.username, u.phone, u.email, " +
                "o.order_no, o.customer_name, o.customer_phone " +
                "FROM user u " +
                "INNER JOIN orders o ON u.id = o.user_id " +
                "WHERE u.phone = ? AND o.customer_phone = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(multiTableSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多表不同加密字段查询原始SQL: " + multiTableSql);
        System.out.println("多表不同加密字段查询处理后SQL: " + processedSql);
    }
} 