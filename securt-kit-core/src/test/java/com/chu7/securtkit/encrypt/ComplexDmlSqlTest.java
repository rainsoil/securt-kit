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
 * 复杂DML SQL测试类
 * 测试各种复杂的INSERT、UPDATE、DELETE操作
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
public class ComplexDmlSqlTest {
    
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
        
        // 配置订单表的加密字段
        Set<String> orderEncryptFields = new HashSet<>(Arrays.asList("customer_name", "customer_phone", "customer_email", "delivery_address"));
        tableFieldCache.addTableEncryptFields("orders", orderEncryptFields);
        tableFieldCache.addClassNameToTableName("com.chu7.securtkit.encrypt.entity.OrderEntity", "orders");
        
        // 配置支付表的加密字段
        Set<String> paymentEncryptFields = new HashSet<>(Arrays.asList("card_number", "cvv", "account_number"));
        tableFieldCache.addTableEncryptFields("payment", paymentEncryptFields);
        
        // 配置日志表的加密字段
        Set<String> logEncryptFields = new HashSet<>(Arrays.asList("user_phone", "user_email", "operation_data"));
        tableFieldCache.addTableEncryptFields("user_log", logEncryptFields);
    }
    
    @Test
    void testComplexInsertWithSubQuery() {
        // 测试带子查询的INSERT语句
        String insertSubQuerySql = "INSERT INTO user_backup (username, phone, email, id_card) " +
                "SELECT username, phone, email, id_card " +
                "FROM user " +
                "WHERE phone = ? " +
                "AND email = ? " +
                "AND id_card = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(insertSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("INSERT子查询原始SQL: " + insertSubQuerySql);
        System.out.println("INSERT子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexInsertWithMultipleTables() {
        // 测试多表INSERT语句
        String multiTableInsertSql = "INSERT INTO user_archive (username, phone, email, id_card, archive_date) " +
                "SELECT u.username, u.phone, u.email, u.id_card, NOW() " +
                "FROM user u " +
                "INNER JOIN orders o ON u.id = o.user_id " +
                "WHERE u.phone = ? " +
                "AND o.customer_phone = ? " +
                "AND o.amount > 1000";
        
        String processedSql = dbEncryptStatementVisitor.processSql(multiTableInsertSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多表INSERT原始SQL: " + multiTableInsertSql);
        System.out.println("多表INSERT处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexInsertWithUnion() {
        // 测试带UNION的INSERT语句
        String insertUnionSql = "INSERT INTO user_combined (username, phone, email, id_card) " +
                "SELECT username, phone, email, id_card FROM user WHERE phone = ? " +
                "UNION " +
                "SELECT customer_name, customer_phone, customer_email, 'N/A' FROM orders WHERE customer_phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(insertUnionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("INSERT UNION原始SQL: " + insertUnionSql);
        System.out.println("INSERT UNION处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexInsertWithCaseWhen() {
        // 测试带CASE WHEN的INSERT语句
        String insertCaseWhenSql = "INSERT INTO user_processed (username, phone, email, id_card, phone_type) " +
                "SELECT username, phone, email, id_card, " +
                "CASE " +
                "    WHEN phone LIKE '138%' THEN '移动' " +
                "    WHEN phone LIKE '139%' THEN '联通' " +
                "    ELSE '其他' " +
                "END as phone_type " +
                "FROM user " +
                "WHERE phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(insertCaseWhenSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("INSERT CASE WHEN原始SQL: " + insertCaseWhenSql);
        System.out.println("INSERT CASE WHEN处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexUpdateWithJoin() {
        // 测试带JOIN的UPDATE语句
        String updateJoinSql = "UPDATE user u " +
                "SET u.phone = o.customer_phone, " +
                "    u.email = o.customer_email " +
                "FROM orders o " +
                "WHERE u.id = o.user_id " +
                "AND o.customer_phone = ? " +
                "AND o.customer_email = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(updateJoinSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE JOIN原始SQL: " + updateJoinSql);
        System.out.println("UPDATE JOIN处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexUpdateWithSubQuery() {
        // 测试带子查询的UPDATE语句
        String updateSubQuerySql = "UPDATE user " +
                "SET phone = (" +
                "    SELECT customer_phone " +
                "    FROM orders " +
                "    WHERE user_id = user.id " +
                "    ORDER BY create_time DESC " +
                "    LIMIT 1" +
                "), " +
                "email = (" +
                "    SELECT customer_email " +
                "    FROM orders " +
                "    WHERE user_id = user.id " +
                "    ORDER BY create_time DESC " +
                "    LIMIT 1" +
                ") " +
                "WHERE phone = ? " +
                "AND EXISTS (" +
                "    SELECT 1 FROM orders " +
                "    WHERE user_id = user.id " +
                "    AND customer_phone = ?" +
                ")";
        
        String processedSql = dbEncryptStatementVisitor.processSql(updateSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE子查询原始SQL: " + updateSubQuerySql);
        System.out.println("UPDATE子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexUpdateWithCaseWhen() {
        // 测试带CASE WHEN的UPDATE语句
        String updateCaseWhenSql = "UPDATE user " +
                "SET phone = CASE " +
                "    WHEN phone LIKE '138%' THEN CONCAT('139', SUBSTRING(phone, 4)) " +
                "    WHEN phone LIKE '139%' THEN CONCAT('138', SUBSTRING(phone, 4)) " +
                "    ELSE phone " +
                "END, " +
                "email = CASE " +
                "    WHEN email LIKE '%@gmail.com' THEN REPLACE(email, '@gmail.com', '@outlook.com') " +
                "    WHEN email LIKE '%@outlook.com' THEN REPLACE(email, '@outlook.com', '@gmail.com') " +
                "    ELSE email " +
                "END " +
                "WHERE phone = ? " +
                "OR email = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(updateCaseWhenSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE CASE WHEN原始SQL: " + updateCaseWhenSql);
        System.out.println("UPDATE CASE WHEN处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexUpdateWithMultipleTables() {
        // 测试多表UPDATE语句
        String multiTableUpdateSql = "UPDATE user u, orders o " +
                "SET u.phone = o.customer_phone, " +
                "    o.customer_phone = u.phone " +
                "WHERE u.id = o.user_id " +
                "AND u.phone = ? " +
                "AND o.customer_phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(multiTableUpdateSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多表UPDATE原始SQL: " + multiTableUpdateSql);
        System.out.println("多表UPDATE处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexDeleteWithJoin() {
        // 测试带JOIN的DELETE语句
        String deleteJoinSql = "DELETE u " +
                "FROM user u " +
                "INNER JOIN orders o ON u.id = o.user_id " +
                "INNER JOIN payment p ON o.id = p.order_id " +
                "WHERE o.customer_phone = ? " +
                "AND p.card_number = ? " +
                "AND u.phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(deleteJoinSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE JOIN原始SQL: " + deleteJoinSql);
        System.out.println("DELETE JOIN处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexDeleteWithSubQuery() {
        // 测试带子查询的DELETE语句
        String deleteSubQuerySql = "DELETE FROM user " +
                "WHERE phone = ? " +
                "AND id IN (" +
                "    SELECT user_id " +
                "    FROM orders " +
                "    WHERE customer_phone = ? " +
                "    AND amount > 1000" +
                ") " +
                "AND EXISTS (" +
                "    SELECT 1 " +
                "    FROM user_log " +
                "    WHERE user_phone = user.phone " +
                "    AND operation_type = 'DELETE'" +
                ")";
        
        String processedSql = dbEncryptStatementVisitor.processSql(deleteSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE子查询原始SQL: " + deleteSubQuerySql);
        System.out.println("DELETE子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexDeleteWithLimit() {
        // 测试带LIMIT的DELETE语句
        String deleteLimitSql = "DELETE FROM user " +
                "WHERE phone = ? " +
                "ORDER BY id " +
                "LIMIT 10";
        
        String processedSql = dbEncryptStatementVisitor.processSql(deleteLimitSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE LIMIT原始SQL: " + deleteLimitSql);
        System.out.println("DELETE LIMIT处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexDeleteWithMultipleTables() {
        // 测试多表DELETE语句
        String multiTableDeleteSql = "DELETE u, o, p " +
                "FROM user u " +
                "INNER JOIN orders o ON u.id = o.user_id " +
                "INNER JOIN payment p ON o.id = p.order_id " +
                "WHERE u.phone = ? " +
                "AND o.customer_phone = ? " +
                "AND p.card_number = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(multiTableDeleteSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多表DELETE原始SQL: " + multiTableDeleteSql);
        System.out.println("多表DELETE处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexInsertWithOnDuplicateKeyUpdate() {
        // 测试INSERT ... ON DUPLICATE KEY UPDATE语句
        String insertOnDuplicateSql = "INSERT INTO user (username, phone, email, id_card) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "phone = VALUES(phone), " +
                "email = VALUES(email), " +
                "id_card = VALUES(id_card)";
        
        String processedSql = dbEncryptStatementVisitor.processSql(insertOnDuplicateSql);
        
        // INSERT语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(insertOnDuplicateSql, processedSql);
        
        System.out.println("INSERT ON DUPLICATE KEY UPDATE: " + insertOnDuplicateSql);
    }
    
    @Test
    void testComplexInsertWithReplace() {
        // 测试REPLACE INTO语句
        String replaceSql = "REPLACE INTO user (username, phone, email, id_card) " +
                "VALUES (?, ?, ?, ?)";
        
        String processedSql = dbEncryptStatementVisitor.processSql(replaceSql);
        
        // REPLACE语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(replaceSql, processedSql);
        
        System.out.println("REPLACE INTO: " + replaceSql);
    }
    
    @Test
    void testComplexUpdateWithOrderByAndLimit() {
        // 测试带ORDER BY和LIMIT的UPDATE语句
        String updateOrderByLimitSql = "UPDATE user " +
                "SET phone = CONCAT('139', SUBSTRING(phone, 4)) " +
                "WHERE phone LIKE '138%' " +
                "ORDER BY id " +
                "LIMIT 5";
        
        String processedSql = dbEncryptStatementVisitor.processSql(updateOrderByLimitSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE ORDER BY LIMIT原始SQL: " + updateOrderByLimitSql);
        System.out.println("UPDATE ORDER BY LIMIT处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexDeleteWithOrderByAndLimit() {
        // 测试带ORDER BY和LIMIT的DELETE语句
        String deleteOrderByLimitSql = "DELETE FROM user " +
                "WHERE phone LIKE '138%' " +
                "ORDER BY id DESC " +
                "LIMIT 3";
        
        String processedSql = dbEncryptStatementVisitor.processSql(deleteOrderByLimitSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE ORDER BY LIMIT原始SQL: " + deleteOrderByLimitSql);
        System.out.println("DELETE ORDER BY LIMIT处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexInsertWithSelectAndWhere() {
        // 测试带WHERE条件的INSERT INTO SELECT语句
        String insertSelectWhereSql = "INSERT INTO user_backup (username, phone, email, id_card, backup_date) " +
                "SELECT username, phone, email, id_card, NOW() " +
                "FROM user " +
                "WHERE phone = ? " +
                "AND email = ? " +
                "AND id_card = ? " +
                "AND create_time < DATE_SUB(NOW(), INTERVAL 1 YEAR)";
        
        String processedSql = dbEncryptStatementVisitor.processSql(insertSelectWhereSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("INSERT SELECT WHERE原始SQL: " + insertSelectWhereSql);
        System.out.println("INSERT SELECT WHERE处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexUpdateWithAggregateFunction() {
        // 测试带聚合函数的UPDATE语句
        String updateAggregateSql = "UPDATE user u " +
                "SET u.phone = (" +
                "    SELECT customer_phone " +
                "    FROM orders " +
                "    WHERE user_id = u.id " +
                "    GROUP BY user_id " +
                "    HAVING COUNT(*) > 1" +
                ") " +
                "WHERE u.phone = ? " +
                "AND EXISTS (" +
                "    SELECT 1 " +
                "    FROM orders " +
                "    WHERE user_id = u.id " +
                "    GROUP BY user_id " +
                "    HAVING COUNT(*) > 1" +
                ")";
        
        String processedSql = dbEncryptStatementVisitor.processSql(updateAggregateSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE聚合函数原始SQL: " + updateAggregateSql);
        System.out.println("UPDATE聚合函数处理后SQL: " + processedSql);
    }
} 