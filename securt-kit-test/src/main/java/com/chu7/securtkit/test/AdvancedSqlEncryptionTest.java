package com.chu7.securtkit.test;

import com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Md5PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.service.FieldEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 高级SQL加密测试类
 * 测试复杂的SQL场景，包括：
 * - 多表JOIN查询
 * - 复杂的字段别名
 * - CASE WHEN语句
 * - 聚合函数
 * - 子查询
 * - 窗口函数
 * - 动态SQL
 * 
 * @author chu7
 * @date 2025/9/22
 */
@Slf4j
@SpringBootApplication
public class AdvancedSqlEncryptionTest {

    public static void main(String[] args) {
        SpringApplication.run(AdvancedSqlEncryptionTest.class, args);
    }

    @Component
    public static class AdvancedSqlTestRunner implements CommandLineRunner {

        @Autowired
        private FieldEncryptionService fieldEncryptionService;

        @Override
        public void run(String... args) throws Exception {
            testComplexJoinWithAliases();
            testCaseWhenStatement();
            testWindowFunction();
            testDynamicSql();
            testNestedSubQuery();
            testUnionQuery();
            testGroupByWithHaving();
            testOrderByWithLimit();
        }

        /**
         * 测试1：复杂的多表JOIN查询with别名
         * 模拟SQL：
         * SELECT 
         *     u.id as user_id,
         *     u.username as user_name,
         *     u.phone as user_phone,
         *     u.email as user_email,
         *     p.real_name as profile_real_name,
         *     p.id_card as profile_id_card,
         *     o.order_no as order_number,
         *     o.customer_name as order_customer_name,
         *     o.customer_phone as order_customer_phone
         * FROM users u
         * LEFT JOIN user_profiles p ON u.id = p.user_id
         * LEFT JOIN orders o ON u.id = o.user_id
         * WHERE u.status = 'ACTIVE'
         * ORDER BY u.create_time DESC
         */
        public void testComplexJoinWithAliases() {
            log.info("=== 测试复杂多表JOIN查询with别名 ===");
            
            List<Map<String, Object>> results = createComplexJoinData();
            log.info("原始复杂JOIN查询结果: {}", results);
            
            // 定义字段映射关系（处理所有别名）
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("user_phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("user_email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_id_card", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("order_customer_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("order_customer_phone", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(results, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(results, decryptedResults);
            
            log.info("复杂多表JOIN查询with别名测试通过！");
        }

        /**
         * 测试2：CASE WHEN语句
         * 模拟SQL：
         * SELECT 
         *     u.id,
         *     u.username,
         *     CASE 
         *         WHEN u.age < 18 THEN '未成年'
         *         WHEN u.age BETWEEN 18 AND 65 THEN '成年'
         *         ELSE '老年'
         *     END as age_group,
         *     CASE 
         *         WHEN p.gender = 'M' THEN '男'
         *         WHEN p.gender = 'F' THEN '女'
         *         ELSE '未知'
         *     END as gender_desc,
         *     p.real_name,
         *     p.id_card
         * FROM users u
         * LEFT JOIN user_profiles p ON u.id = p.user_id
         */
        public void testCaseWhenStatement() {
            log.info("=== 测试CASE WHEN语句 ===");
            
            List<Map<String, Object>> results = createCaseWhenData();
            log.info("原始CASE WHEN查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("id_card", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(results, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(results, decryptedResults);
            
            log.info("CASE WHEN语句测试通过！");
        }

        /**
         * 测试3：窗口函数
         * 模拟SQL：
         * SELECT 
         *     u.id,
         *     u.username,
         *     u.phone,
         *     u.email,
         *     ROW_NUMBER() OVER (PARTITION BY u.department ORDER BY u.create_time DESC) as row_num,
         *     RANK() OVER (ORDER BY u.salary DESC) as salary_rank,
         *     LAG(u.phone, 1) OVER (ORDER BY u.id) as prev_phone,
         *     LEAD(u.email, 1) OVER (ORDER BY u.id) as next_email
         * FROM users u
         * WHERE u.status = 'ACTIVE'
         */
        public void testWindowFunction() {
            log.info("=== 测试窗口函数 ===");
            
            List<Map<String, Object>> results = createWindowFunctionData();
            log.info("原始窗口函数查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("prev_phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("next_email", Md5PoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(results, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(results, decryptedResults);
            
            log.info("窗口函数测试通过！");
        }

        /**
         * 测试4：动态SQL
         * 模拟动态构建的SQL，字段名和表名都是动态的
         */
        public void testDynamicSql() {
            log.info("=== 测试动态SQL ===");
            
            // 模拟动态SQL的不同场景
            testDynamicSqlScenario("users", "user_", createUserData());
            testDynamicSqlScenario("user_profiles", "profile_", createProfileData());
            testDynamicSqlScenario("orders", "order_", createOrderData());
            
            log.info("动态SQL测试通过！");
        }

        /**
         * 测试5：嵌套子查询
         * 模拟SQL：
         * SELECT 
         *     u.id,
         *     u.username,
         *     u.phone,
         *     (SELECT p.real_name FROM user_profiles p WHERE p.user_id = u.id) as profile_name,
         *     (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) as order_count,
         *     (SELECT o.customer_name FROM orders o WHERE o.user_id = u.id ORDER BY o.create_time DESC LIMIT 1) as latest_customer_name
         * FROM users u
         * WHERE u.id IN (
         *     SELECT DISTINCT user_id FROM orders WHERE amount > 1000
         * )
         */
        public void testNestedSubQuery() {
            log.info("=== 测试嵌套子查询 ===");
            
            List<Map<String, Object>> results = createNestedSubQueryData();
            log.info("原始嵌套子查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("latest_customer_name", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(results, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(results, decryptedResults);
            
            log.info("嵌套子查询测试通过！");
        }

        /**
         * 测试6：UNION查询
         * 模拟SQL：
         * SELECT 'user' as source_type, id, username, phone, email, NULL as real_name, NULL as id_card
         * FROM users WHERE status = 'ACTIVE'
         * UNION ALL
         * SELECT 'profile' as source_type, user_id as id, NULL as username, NULL as phone, NULL as email, real_name, id_card
         * FROM user_profiles WHERE status = 'ACTIVE'
         */
        public void testUnionQuery() {
            log.info("=== 测试UNION查询 ===");
            
            List<Map<String, Object>> results = createUnionQueryData();
            log.info("原始UNION查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("id_card", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(results, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(results, decryptedResults);
            
            log.info("UNION查询测试通过！");
        }

        /**
         * 测试7：GROUP BY with HAVING
         * 模拟SQL：
         * SELECT 
         *     u.department,
         *     COUNT(*) as user_count,
         *     AVG(u.salary) as avg_salary,
         *     MAX(u.salary) as max_salary,
         *     GROUP_CONCAT(DISTINCT p.real_name) as employee_names,
         *     GROUP_CONCAT(DISTINCT p.id_card) as employee_id_cards
         * FROM users u
         * LEFT JOIN user_profiles p ON u.id = p.user_id
         * GROUP BY u.department
         * HAVING COUNT(*) > 5
         * ORDER BY user_count DESC
         */
        public void testGroupByWithHaving() {
            log.info("=== 测试GROUP BY with HAVING ===");
            
            List<Map<String, Object>> results = createGroupByHavingData();
            log.info("原始GROUP BY with HAVING查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("employee_names", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("employee_id_cards", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(results, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(results, decryptedResults);
            
            log.info("GROUP BY with HAVING测试通过！");
        }

        /**
         * 测试8：ORDER BY with LIMIT
         * 模拟SQL：
         * SELECT 
         *     u.id,
         *     u.username,
         *     u.phone,
         *     u.email,
         *     p.real_name,
         *     o.order_no,
         *     o.customer_name,
         *     o.amount
         * FROM users u
         * LEFT JOIN user_profiles p ON u.id = p.user_id
         * LEFT JOIN orders o ON u.id = o.user_id
         * ORDER BY u.create_time DESC, o.amount DESC, u.username ASC
         * LIMIT 10 OFFSET 0
         */
        public void testOrderByWithLimit() {
            log.info("=== 测试ORDER BY with LIMIT ===");
            
            List<Map<String, Object>> results = createOrderByLimitData();
            log.info("原始ORDER BY with LIMIT查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("customer_name", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(results, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(results, decryptedResults);
            
            log.info("ORDER BY with LIMIT测试通过！");
        }

        // ==================== 辅助方法 ====================

        /**
         * 处理Map结果列表
         */
        private List<Map<String, Object>> processMapResults(List<Map<String, Object>> results, 
                Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings, 
                String operation) {
            List<Map<String, Object>> processedResults = new ArrayList<>();
            
            for (Map<String, Object> result : results) {
                Map<String, Object> processedResult;
                if ("加密".equals(operation)) {
                    processedResult = fieldEncryptionService.encryptMap(result, fieldMappings);
                } else {
                    processedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                }
                processedResults.add(processedResult);
            }
            
            log.info("{}后查询结果: {}", operation, processedResults);
            return processedResults;
        }

        /**
         * 验证数据一致性
         */
        private void validateDataConsistency(List<Map<String, Object>> original, List<Map<String, Object>> processed) {
            for (int i = 0; i < original.size(); i++) {
                Map<String, Object> orig = original.get(i);
                Map<String, Object> proc = processed.get(i);
                
                // 验证所有加密字段的数据一致性
                for (String key : orig.keySet()) {
                    if (orig.get(key) instanceof String && proc.containsKey(key)) {
                        assert orig.get(key).equals(proc.get(key)) : 
                            String.format("字段 %s 数据不一致: 原始=%s, 处理=%s", key, orig.get(key), proc.get(key));
                    }
                }
            }
        }

        /**
         * 测试动态SQL场景
         */
        private void testDynamicSqlScenario(String tableName, String prefix, List<Map<String, Object>> data) {
            log.info("测试动态SQL场景: 表={}, 前缀={}", tableName, prefix);
            
            // 动态构建字段映射
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put(prefix + "phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put(prefix + "email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put(prefix + "real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put(prefix + "id_card", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put(prefix + "customer_name", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量加密
            List<Map<String, Object>> encryptedResults = processMapResults(data, fieldMappings, "加密");
            List<Map<String, Object>> decryptedResults = processMapResults(encryptedResults, fieldMappings, "解密");
            
            // 验证数据一致性
            validateDataConsistency(data, decryptedResults);
        }

        // ==================== 数据创建方法 ====================

        private List<Map<String, Object>> createComplexJoinData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("user_id", 1L);
            result1.put("user_name", "user1");
            result1.put("user_phone", "13800138001");
            result1.put("user_email", "user1@example.com");
            result1.put("profile_real_name", "张三");
            result1.put("profile_id_card", "110101199001011234");
            result1.put("order_number", "ORD001");
            result1.put("order_customer_name", "张三");
            result1.put("order_customer_phone", "13800138001");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createCaseWhenData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("age_group", "成年");
            result1.put("gender_desc", "男");
            result1.put("real_name", "张三");
            result1.put("id_card", "110101199001011234");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createWindowFunctionData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("phone", "13800138001");
            result1.put("email", "user1@example.com");
            result1.put("row_num", 1L);
            result1.put("salary_rank", 1L);
            result1.put("prev_phone", null);
            result1.put("next_email", "user2@example.com");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createUserData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("user_phone", "13800138001");
            result1.put("user_email", "user1@example.com");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createProfileData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("profile_real_name", "张三");
            result1.put("profile_id_card", "110101199001011234");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createOrderData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("order_customer_name", "张三");
            result1.put("order_customer_phone", "13800138001");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createNestedSubQueryData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("phone", "13800138001");
            result1.put("profile_name", "张三");
            result1.put("order_count", 3L);
            result1.put("latest_customer_name", "张三");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createUnionQueryData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("source_type", "user");
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("phone", "13800138001");
            result1.put("email", "user1@example.com");
            result1.put("real_name", null);
            result1.put("id_card", null);
            results.add(result1);
            
            Map<String, Object> result2 = new HashMap<>();
            result2.put("source_type", "profile");
            result2.put("id", 1L);
            result2.put("username", null);
            result2.put("phone", null);
            result2.put("email", null);
            result2.put("real_name", "张三");
            result2.put("id_card", "110101199001011234");
            results.add(result2);
            
            return results;
        }

        private List<Map<String, Object>> createGroupByHavingData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("department", "技术部");
            result1.put("user_count", 8L);
            result1.put("avg_salary", 15000.0);
            result1.put("max_salary", 25000.0);
            result1.put("employee_names", "张三,李四,王五,赵六");
            result1.put("employee_id_cards", "110101199001011234,110101199002021234,110101199003031234,110101199004041234");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createOrderByLimitData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("phone", "13800138001");
            result1.put("email", "user1@example.com");
            result1.put("real_name", "张三");
            result1.put("order_no", "ORD001");
            result1.put("customer_name", "张三");
            result1.put("amount", 1500.0);
            results.add(result1);
            
            return results;
        }
    }
}
