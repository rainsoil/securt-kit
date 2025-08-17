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
 * 复杂SQL加密测试类
 * 测试各种复杂SQL场景下的加密解密功能
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
public class ComplexSqlEncryptTest {
    
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
    }
    
    @Test
    void testMultiTableJoinWithAliases() {
        // 测试多表关联查询（带表别名和字段别名）
        String complexJoinSql = "SELECT " +
                "u.id as user_id, " +
                "u.username as user_name, " +
                "u.phone as user_phone, " +
                "u.email as user_email, " +
                "o.id as order_id, " +
                "o.order_no as order_number, " +
                "o.customer_name as client_name, " +
                "o.customer_phone as client_phone, " +
                "o.amount as order_amount " +
                "FROM user u " +
                "INNER JOIN orders o ON u.id = o.user_id " +
                "WHERE u.phone = ? AND o.customer_phone = ? " +
                "ORDER BY u.username, o.order_no";
        
        String processedSql = dbEncryptStatementVisitor.processSql(complexJoinSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        // 验证SELECT字段被正确解密
        assertTrue(processedSql.contains("AES_DECRYPT"));
        assertTrue(processedSql.contains("FROM_BASE64"));
        
        // 验证WHERE条件被正确加密
        assertTrue(processedSql.contains("TO_BASE64"));
        assertTrue(processedSql.contains("AES_ENCRYPT"));
        
        System.out.println("多表关联查询原始SQL: " + complexJoinSql);
        System.out.println("多表关联查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testLeftJoinWithSubQuery() {
        // 测试左连接包含子查询
        String leftJoinSubQuerySql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "o.order_count " +
                "FROM user u " +
                "LEFT JOIN (" +
                "    SELECT user_id, COUNT(*) as order_count " +
                "    FROM orders " +
                "    WHERE customer_phone = ? " +
                "    GROUP BY user_id" +
                ") o ON u.id = o.user_id " +
                "WHERE u.phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(leftJoinSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("左连接子查询原始SQL: " + leftJoinSubQuerySql);
        System.out.println("左连接子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexNestedSubQuery() {
        // 测试复杂嵌套子查询
        String nestedSubQuerySql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email " +
                "FROM user u " +
                "WHERE u.id IN (" +
                "    SELECT DISTINCT user_id " +
                "    FROM orders " +
                "    WHERE customer_phone IN (" +
                "        SELECT phone " +
                "        FROM user " +
                "        WHERE email = ?" +
                "    )" +
                ") " +
                "AND u.phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(nestedSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("复杂嵌套子查询原始SQL: " + nestedSubQuerySql);
        System.out.println("复杂嵌套子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testUnionQueryWithEncryptFields() {
        // 测试UNION查询
        String unionSql = "SELECT phone, email, username FROM user WHERE phone = ? " +
                "UNION " +
                "SELECT customer_phone, customer_email, customer_name FROM orders WHERE customer_phone = ? " +
                "ORDER BY username";
        
        String processedSql = dbEncryptStatementVisitor.processSql(unionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UNION查询原始SQL: " + unionSql);
        System.out.println("UNION查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testUnionAllQuery() {
        // 测试UNION ALL查询
        String unionAllSql = "SELECT phone, email, username FROM user WHERE phone = ? " +
                "UNION ALL " +
                "SELECT customer_phone, customer_email, customer_name FROM orders WHERE customer_phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(unionAllSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UNION ALL查询原始SQL: " + unionAllSql);
        System.out.println("UNION ALL查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testAggregateFunctionWithEncryptFields() {
        // 测试聚合函数查询
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
    void testWindowFunctionQuery() {
        // 测试窗口函数查询
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
    void testCaseWhenQuery() {
        // 测试CASE WHEN查询
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
    void testExistsSubQuery() {
        // 测试EXISTS子查询
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
        
        System.out.println("EXISTS子查询原始SQL: " + existsSql);
        System.out.println("EXISTS子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testNotExistsSubQuery() {
        // 测试NOT EXISTS子查询
        String notExistsSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email " +
                "FROM user u " +
                "WHERE NOT EXISTS (" +
                "    SELECT 1 " +
                "    FROM orders o " +
                "    WHERE o.user_id = u.id " +
                "    AND o.customer_phone = ?" +
                ") " +
                "AND u.phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(notExistsSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("NOT EXISTS子查询原始SQL: " + notExistsSql);
        System.out.println("NOT EXISTS子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testInsertStatement() {
        // 测试INSERT语句
        String insertSql = "INSERT INTO user (username, phone, email, id_card, address) VALUES (?, ?, ?, ?, ?)";
        String processedSql = dbEncryptStatementVisitor.processSql(insertSql);
        
        // INSERT语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(insertSql, processedSql);
        
        System.out.println("INSERT语句: " + insertSql);
    }
    
    @Test
    void testUpdateStatement() {
        // 测试UPDATE语句
        String updateSql = "UPDATE user SET phone = ?, email = ?, id_card = ? WHERE id = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(updateSql);
        
        // UPDATE语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(updateSql, processedSql);
        
        System.out.println("UPDATE语句: " + updateSql);
    }
    
    @Test
    void testDeleteStatement() {
        // 测试DELETE语句
        String deleteSql = "DELETE FROM user WHERE phone = ? AND email = ?";
        String processedSql = dbEncryptStatementVisitor.processSql(deleteSql);
        
        // DELETE语句的WHERE条件需要加密
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("DELETE语句原始SQL: " + deleteSql);
        System.out.println("DELETE语句处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexInsertWithSelect() {
        // 测试INSERT INTO SELECT语句
        String insertSelectSql = "INSERT INTO user_backup (username, phone, email, id_card) " +
                "SELECT username, phone, email, id_card " +
                "FROM user " +
                "WHERE phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(insertSelectSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("INSERT INTO SELECT原始SQL: " + insertSelectSql);
        System.out.println("INSERT INTO SELECT处理后SQL: " + processedSql);
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
    void testComplexThreeTableJoin() {
        // 测试三表关联查询
        String threeTableJoinSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "o.order_no, " +
                "o.customer_name, " +
                "o.customer_phone, " +
                "p.card_number, " +
                "p.amount " +
                "FROM user u " +
                "LEFT JOIN orders o ON u.id = o.user_id " +
                "LEFT JOIN payment p ON o.id = p.order_id " +
                "WHERE u.phone = ? " +
                "AND o.customer_phone = ? " +
                "AND p.card_number = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(threeTableJoinSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("三表关联查询原始SQL: " + threeTableJoinSql);
        System.out.println("三表关联查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testGroupByWithHaving() {
        // 测试GROUP BY和HAVING
        String groupBySql = "SELECT " +
                "SUBSTRING(phone, 1, 3) as area_code, " +
                "COUNT(*) as user_count " +
                "FROM user " +
                "WHERE phone LIKE ? " +
                "GROUP BY SUBSTRING(phone, 1, 3) " +
                "HAVING COUNT(*) > 1";
        
        String processedSql = dbEncryptStatementVisitor.processSql(groupBySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("GROUP BY HAVING原始SQL: " + groupBySql);
        System.out.println("GROUP BY HAVING处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexCrossJoin() {
        // 测试交叉连接查询
        String crossJoinSql = "SELECT " +
                "u1.username as user1_name, " +
                "u1.phone as user1_phone, " +
                "u2.username as user2_name, " +
                "u2.phone as user2_phone " +
                "FROM user u1 " +
                "CROSS JOIN user u2 " +
                "WHERE u1.phone = ? AND u2.phone = ? " +
                "AND u1.id != u2.id";
        
        String processedSql = dbEncryptStatementVisitor.processSql(crossJoinSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("交叉连接查询原始SQL: " + crossJoinSql);
        System.out.println("交叉连接查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testMultipleSubQueriesInSelect() {
        // 测试SELECT中包含多个子查询
        String multipleSubQuerySql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "(SELECT COUNT(*) FROM orders WHERE customer_phone = u.phone) as order_count, " +
                "(SELECT MAX(amount) FROM orders WHERE customer_phone = u.phone) as max_amount, " +
                "(SELECT customer_name FROM orders WHERE customer_phone = ? LIMIT 1) as latest_customer " +
                "FROM user u " +
                "WHERE u.phone = ?";
        
        String processedSql = dbEncryptStatementVisitor.processSql(multipleSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多子查询SELECT原始SQL: " + multipleSubQuerySql);
        System.out.println("多子查询SELECT处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexWithClause() {
        // 测试WITH子句（CTE）
        String withClauseSql = "WITH user_orders AS (" +
                "    SELECT u.id, u.username, u.phone, u.email, " +
                "           COUNT(o.id) as order_count " +
                "    FROM user u " +
                "    LEFT JOIN orders o ON u.id = o.user_id " +
                "    WHERE u.phone = ? " +
                "    GROUP BY u.id, u.username, u.phone, u.email" +
                "), " +
                "high_value_orders AS (" +
                "    SELECT user_id, customer_phone, customer_email " +
                "    FROM orders " +
                "    WHERE customer_phone = ? " +
                "    AND amount > 1000" +
                ") " +
                "SELECT uo.*, hvo.customer_phone, hvo.customer_email " +
                "FROM user_orders uo " +
                "LEFT JOIN high_value_orders hvo ON uo.id = hvo.user_id";
        
        String processedSql = dbEncryptStatementVisitor.processSql(withClauseSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("WITH子句查询原始SQL: " + withClauseSql);
        System.out.println("WITH子句查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexInsertWithMultipleValues() {
        // 测试复杂的INSERT语句（多值插入）
        String complexInsertSql = "INSERT INTO user (username, phone, email, id_card, address) VALUES " +
                "(?, ?, ?, ?, ?), " +
                "(?, ?, ?, ?, ?), " +
                "(?, ?, ?, ?, ?)";
        
        String processedSql = dbEncryptStatementVisitor.processSql(complexInsertSql);
        
        // INSERT语句通常不需要修改，因为参数会在应用层加密
        assertFalse(dbEncryptStatementVisitor.hasChanges());
        assertEquals(complexInsertSql, processedSql);
        
        System.out.println("复杂INSERT语句: " + complexInsertSql);
    }
    
    @Test
    void testComplexUpdateWithSubQuery() {
        // 测试带子查询的UPDATE语句
        String updateSubQuerySql = "UPDATE user u " +
                "SET u.phone = (" +
                "    SELECT customer_phone " +
                "    FROM orders " +
                "    WHERE user_id = u.id " +
                "    ORDER BY create_time DESC " +
                "    LIMIT 1" +
                ") " +
                "WHERE u.phone = ? " +
                "AND EXISTS (" +
                "    SELECT 1 FROM orders " +
                "    WHERE user_id = u.id " +
                "    AND customer_phone = ?" +
                ")";
        
        String processedSql = dbEncryptStatementVisitor.processSql(updateSubQuerySql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("UPDATE子查询原始SQL: " + updateSubQuerySql);
        System.out.println("UPDATE子查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexDeleteWithMultipleTables() {
        // 测试多表DELETE语句
        String multiTableDeleteSql = "DELETE u, o " +
                "FROM user u " +
                "INNER JOIN orders o ON u.id = o.user_id " +
                "WHERE u.phone = ? " +
                "AND o.customer_phone = ? " +
                "AND o.amount < 100";
        
        String processedSql = dbEncryptStatementVisitor.processSql(multiTableDeleteSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("多表DELETE原始SQL: " + multiTableDeleteSql);
        System.out.println("多表DELETE处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithJsonFunctions() {
        // 测试包含JSON函数的复杂查询
        String jsonFunctionSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "JSON_OBJECT('phone', u.phone, 'email', u.email) as user_info, " +
                "JSON_EXTRACT(JSON_OBJECT('phone', u.phone), '$.phone') as extracted_phone " +
                "FROM user u " +
                "WHERE u.phone = ? " +
                "AND JSON_CONTAINS(JSON_OBJECT('phone', u.phone), JSON_OBJECT('phone', ?))";
        
        String processedSql = dbEncryptStatementVisitor.processSql(jsonFunctionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("JSON函数查询原始SQL: " + jsonFunctionSql);
        System.out.println("JSON函数查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithStringFunctions() {
        // 测试包含字符串函数的复杂查询
        String stringFunctionSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "CONCAT(LEFT(u.phone, 3), '****', RIGHT(u.phone, 4)) as masked_phone, " +
                "REPLACE(u.email, '@', '[AT]') as masked_email, " +
                "SUBSTRING_INDEX(u.email, '@', 1) as email_prefix " +
                "FROM user u " +
                "WHERE u.phone = ? " +
                "AND u.email LIKE ? " +
                "AND LENGTH(u.phone) = 11";
        
        String processedSql = dbEncryptStatementVisitor.processSql(stringFunctionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("字符串函数查询原始SQL: " + stringFunctionSql);
        System.out.println("字符串函数查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithDateFunctions() {
        // 测试包含日期函数的复杂查询
        String dateFunctionSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "DATE_FORMAT(NOW(), '%Y-%m-%d') as current_date, " +
                "DATEDIFF(NOW(), DATE_SUB(NOW(), INTERVAL 30 DAY)) as days_diff " +
                "FROM user u " +
                "WHERE u.phone = ? " +
                "AND u.create_time BETWEEN DATE_SUB(NOW(), INTERVAL 30 DAY) AND NOW()";
        
        String processedSql = dbEncryptStatementVisitor.processSql(dateFunctionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("日期函数查询原始SQL: " + dateFunctionSql);
        System.out.println("日期函数查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithMathematicalFunctions() {
        // 测试包含数学函数的复杂查询
        String mathFunctionSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "ROUND(CAST(SUBSTRING(u.phone, 1, 3) AS DECIMAL) / 100, 2) as area_ratio, " +
                "POWER(CAST(SUBSTRING(u.phone, 1, 3) AS DECIMAL), 2) as area_square " +
                "FROM user u " +
                "WHERE u.phone = ? " +
                "AND CAST(SUBSTRING(u.phone, 1, 3) AS DECIMAL) > 100";
        
        String processedSql = dbEncryptStatementVisitor.processSql(mathFunctionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("数学函数查询原始SQL: " + mathFunctionSql);
        System.out.println("数学函数查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithConditionalLogic() {
        // 测试包含复杂条件逻辑的查询
        String conditionalLogicSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "CASE " +
                "    WHEN u.phone LIKE '138%' THEN '移动' " +
                "    WHEN u.phone LIKE '139%' THEN '联通' " +
                "    WHEN u.phone LIKE '186%' THEN '电信' " +
                "    ELSE '其他' " +
                "END as operator, " +
                "IF(u.email LIKE '%@gmail.com', 'Gmail', 'Other') as email_provider " +
                "FROM user u " +
                "WHERE (u.phone = ? OR u.email = ?) " +
                "AND (u.phone LIKE '138%' OR u.phone LIKE '139%') " +
                "AND u.email IS NOT NULL";
        
        String processedSql = dbEncryptStatementVisitor.processSql(conditionalLogicSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("条件逻辑查询原始SQL: " + conditionalLogicSql);
        System.out.println("条件逻辑查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithAnalyticalFunctions() {
        // 测试包含分析函数的复杂查询
        String analyticalFunctionSql = "SELECT " +
                "u.username, " +
                "u.phone, " +
                "u.email, " +
                "LAG(u.phone) OVER (ORDER BY u.username) as prev_phone, " +
                "LEAD(u.phone) OVER (ORDER BY u.username) as next_phone, " +
                "FIRST_VALUE(u.phone) OVER (PARTITION BY SUBSTRING(u.phone, 1, 3) ORDER BY u.username) as first_phone_in_area, " +
                "LAST_VALUE(u.phone) OVER (PARTITION BY SUBSTRING(u.phone, 1, 3) ORDER BY u.username ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) as last_phone_in_area " +
                "FROM user u " +
                "WHERE u.phone = ? " +
                "ORDER BY u.username";
        
        String processedSql = dbEncryptStatementVisitor.processSql(analyticalFunctionSql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("分析函数查询原始SQL: " + analyticalFunctionSql);
        System.out.println("分析函数查询处理后SQL: " + processedSql);
    }
    
    @Test
    void testComplexSelectWithRecursiveCTE() {
        // 测试递归CTE查询
        String recursiveCTESql = "WITH RECURSIVE user_hierarchy AS (" +
                "    SELECT id, username, phone, email, parent_id, 1 as level " +
                "    FROM user " +
                "    WHERE phone = ? " +
                "    UNION ALL " +
                "    SELECT u.id, u.username, u.phone, u.email, u.parent_id, uh.level + 1 " +
                "    FROM user u " +
                "    INNER JOIN user_hierarchy uh ON u.parent_id = uh.id " +
                "    WHERE u.phone = ? " +
                "    AND uh.level < 5" +
                ") " +
                "SELECT * FROM user_hierarchy";
        
        String processedSql = dbEncryptStatementVisitor.processSql(recursiveCTESql);
        
        assertTrue(dbEncryptStatementVisitor.hasChanges());
        assertNotNull(processedSql);
        
        System.out.println("递归CTE查询原始SQL: " + recursiveCTESql);
        System.out.println("递归CTE查询处理后SQL: " + processedSql);
    }
} 