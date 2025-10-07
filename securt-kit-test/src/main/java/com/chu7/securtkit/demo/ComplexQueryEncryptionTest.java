package com.chu7.securtkit.test;

import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Md5PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.service.FieldEncryptionService;
import lombok.Data;
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
 * 复杂查询加密测试类
 * 测试多表关联、表别名、字段别名等复杂场景下的字段加密功能
 * 
 * @author chu7
 * @date 2025/9/22
 */
@Slf4j
@SpringBootApplication
public class ComplexQueryEncryptionTest {

    public static void main(String[] args) {
        SpringApplication.run(ComplexQueryEncryptionTest.class, args);
    }

    @Component
    public static class ComplexQueryTestRunner implements CommandLineRunner {

        @Autowired
        private FieldEncryptionService fieldEncryptionService;

        @Override
        public void run(String... args) throws Exception {
            testMultiTableJoin();
            testAliasFields();
            testSubQuery();
            testAggregationQuery();
            testComplexConditions();
            testMapResultProcessing();
        }

        /**
         * 用户表实体
         */
        @Data
        public static class User {
            private Long id;
            private String username;
            
            @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
            private String password;
            
            @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
            private String phone;
            
            @FieldEncryptor(Md5PoJoFieldEncryptorStrategy.class)
            private String email;
            
            private String address;
            private Integer age;
        }

        /**
         * 用户详情表实体
         */
        @Data
        public static class UserProfile {
            private Long id;
            private Long userId;
            
            @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
            private String realName;
            
            @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
            private String idCard;
            
            private String gender;
            private String occupation;
        }

        /**
         * 订单表实体
         */
        @Data
        public static class Order {
            private Long id;
            private Long userId;
            private String orderNo;
            
            @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
            private String customerName;
            
            @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
            private String customerPhone;
            
            private Double amount;
            private String status;
        }

        /**
         * 测试1：多表关联查询
         * 模拟：SELECT u.*, p.real_name, p.id_card FROM users u LEFT JOIN user_profiles p ON u.id = p.user_id
         */
        public void testMultiTableJoin() {
            log.info("=== 测试多表关联查询 ===");
            
            // 模拟多表关联查询结果
            List<Map<String, Object>> joinResults = createMultiTableJoinData();
            log.info("原始多表关联查询结果: {}", joinResults);
            
            // 定义字段映射关系（模拟SQL解析结果）
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("id_card", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量加密Map结果
            List<Map<String, Object>> encryptedResults = new ArrayList<>();
            for (Map<String, Object> result : joinResults) {
                Map<String, Object> encryptedResult = fieldEncryptionService.encryptMap(result, fieldMappings);
                encryptedResults.add(encryptedResult);
            }
            
            log.info("加密后多表关联查询结果: {}", encryptedResults);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : encryptedResults) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后多表关联查询结果: {}", decryptedResults);
            
            // 验证数据一致性
            for (int i = 0; i < joinResults.size(); i++) {
                Map<String, Object> original = joinResults.get(i);
                Map<String, Object> decrypted = decryptedResults.get(i);
                
                assert original.get("password").equals(decrypted.get("password"));
                assert original.get("phone").equals(decrypted.get("phone"));
                assert original.get("email").equals(decrypted.get("email"));
                assert original.get("real_name").equals(decrypted.get("real_name"));
                assert original.get("id_card").equals(decrypted.get("id_card"));
            }
            
            log.info("多表关联查询测试通过！");
        }

        /**
         * 测试2：表别名和字段别名
         * 模拟：SELECT u.id as user_id, u.username as user_name, u.phone as user_phone, 
         *              p.real_name as profile_name, p.id_card as profile_id_card
         *       FROM users u LEFT JOIN user_profiles p ON u.id = p.user_id
         */
        public void testAliasFields() {
            log.info("=== 测试表别名和字段别名 ===");
            
            // 模拟带别名的查询结果
            List<Map<String, Object>> aliasResults = createAliasQueryData();
            log.info("原始别名查询结果: {}", aliasResults);
            
            // 定义字段映射关系（处理别名）
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("user_phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_id_card", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量加密Map结果
            List<Map<String, Object>> encryptedResults = new ArrayList<>();
            for (Map<String, Object> result : aliasResults) {
                Map<String, Object> encryptedResult = fieldEncryptionService.encryptMap(result, fieldMappings);
                encryptedResults.add(encryptedResult);
            }
            
            log.info("加密后别名查询结果: {}", encryptedResults);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : encryptedResults) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后别名查询结果: {}", decryptedResults);
            
            // 验证数据一致性
            for (int i = 0; i < aliasResults.size(); i++) {
                Map<String, Object> original = aliasResults.get(i);
                Map<String, Object> decrypted = decryptedResults.get(i);
                
                assert original.get("user_phone").equals(decrypted.get("user_phone"));
                assert original.get("profile_name").equals(decrypted.get("profile_name"));
                assert original.get("profile_id_card").equals(decrypted.get("profile_id_card"));
            }
            
            log.info("表别名和字段别名测试通过！");
        }

        /**
         * 测试3：子查询
         * 模拟：SELECT u.*, (SELECT p.real_name FROM user_profiles p WHERE p.user_id = u.id) as profile_name
         *       FROM users u WHERE u.id IN (SELECT user_id FROM orders WHERE amount > 1000)
         */
        public void testSubQuery() {
            log.info("=== 测试子查询 ===");
            
            // 模拟子查询结果
            List<Map<String, Object>> subQueryResults = createSubQueryData();
            log.info("原始子查询结果: {}", subQueryResults);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_name", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量加密Map结果
            List<Map<String, Object>> encryptedResults = new ArrayList<>();
            for (Map<String, Object> result : subQueryResults) {
                Map<String, Object> encryptedResult = fieldEncryptionService.encryptMap(result, fieldMappings);
                encryptedResults.add(encryptedResult);
            }
            
            log.info("加密后子查询结果: {}", encryptedResults);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : encryptedResults) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后子查询结果: {}", decryptedResults);
            
            log.info("子查询测试通过！");
        }

        /**
         * 测试4：聚合查询
         * 模拟：SELECT u.username, COUNT(o.id) as order_count, 
         *              MAX(o.amount) as max_amount, AVG(o.amount) as avg_amount,
         *              GROUP_CONCAT(DISTINCT o.customer_name) as customer_names
         *       FROM users u LEFT JOIN orders o ON u.id = o.user_id
         *       GROUP BY u.id, u.username
         */
        public void testAggregationQuery() {
            log.info("=== 测试聚合查询 ===");
            
            // 模拟聚合查询结果
            List<Map<String, Object>> aggregationResults = createAggregationQueryData();
            log.info("原始聚合查询结果: {}", aggregationResults);
            
            // 定义字段映射关系（聚合字段中的敏感信息）
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("customer_names", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量加密Map结果
            List<Map<String, Object>> encryptedResults = new ArrayList<>();
            for (Map<String, Object> result : aggregationResults) {
                Map<String, Object> encryptedResult = fieldEncryptionService.encryptMap(result, fieldMappings);
                encryptedResults.add(encryptedResult);
            }
            
            log.info("加密后聚合查询结果: {}", encryptedResults);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : encryptedResults) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后聚合查询结果: {}", decryptedResults);
            
            log.info("聚合查询测试通过！");
        }

        /**
         * 测试5：复杂条件查询
         * 模拟：SELECT u.*, p.real_name, o.order_no, o.customer_name
         *       FROM users u 
         *       LEFT JOIN user_profiles p ON u.id = p.user_id
         *       LEFT JOIN orders o ON u.id = o.user_id
         *       WHERE u.age BETWEEN 18 AND 65 
         *       AND (p.gender = 'M' OR p.gender IS NULL)
         *       AND o.amount > 500
         *       ORDER BY u.create_time DESC, o.amount DESC
         *       LIMIT 10
         */
        public void testComplexConditions() {
            log.info("=== 测试复杂条件查询 ===");
            
            // 模拟复杂条件查询结果
            List<Map<String, Object>> complexResults = createComplexConditionData();
            log.info("原始复杂条件查询结果: {}", complexResults);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("customer_name", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量加密Map结果
            List<Map<String, Object>> encryptedResults = new ArrayList<>();
            for (Map<String, Object> result : complexResults) {
                Map<String, Object> encryptedResult = fieldEncryptionService.encryptMap(result, fieldMappings);
                encryptedResults.add(encryptedResult);
            }
            
            log.info("加密后复杂条件查询结果: {}", encryptedResults);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : encryptedResults) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后复杂条件查询结果: {}", decryptedResults);
            
            log.info("复杂条件查询测试通过！");
        }

        /**
         * 测试6：Map结果处理（模拟MyBatis返回的Map结果）
         */
        public void testMapResultProcessing() {
            log.info("=== 测试Map结果处理 ===");
            
            // 模拟MyBatis返回的Map结果（字段名可能被转换为大写）
            List<Map<String, Object>> mapResults = createMapResultData();
            log.info("原始Map结果: {}", mapResults);
            
            // 定义字段映射关系（处理大小写问题）
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("PASSWORD", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("PHONE", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("EMAIL", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("REAL_NAME", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("CUSTOMER_NAME", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量加密Map结果
            List<Map<String, Object>> encryptedResults = new ArrayList<>();
            for (Map<String, Object> result : mapResults) {
                Map<String, Object> encryptedResult = fieldEncryptionService.encryptMap(result, fieldMappings);
                encryptedResults.add(encryptedResult);
            }
            
            log.info("加密后Map结果: {}", encryptedResults);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : encryptedResults) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后Map结果: {}", decryptedResults);
            
            log.info("Map结果处理测试通过！");
        }

        // ==================== 数据创建方法 ====================

        private List<Map<String, Object>> createMultiTableJoinData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("password", "password123");
            result1.put("phone", "13800138001");
            result1.put("email", "user1@example.com");
            result1.put("address", "北京市朝阳区");
            result1.put("age", 25);
            result1.put("real_name", "张三");
            result1.put("id_card", "110101199001011234");
            results.add(result1);
            
            Map<String, Object> result2 = new HashMap<>();
            result2.put("id", 2L);
            result2.put("username", "user2");
            result2.put("password", "password456");
            result2.put("phone", "13800138002");
            result2.put("email", "user2@example.com");
            result2.put("address", "上海市浦东区");
            result2.put("age", 30);
            result2.put("real_name", "李四");
            result2.put("id_card", "310101199002021234");
            results.add(result2);
            
            return results;
        }

        private List<Map<String, Object>> createAliasQueryData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("user_id", 1L);
            result1.put("user_name", "user1");
            result1.put("user_phone", "13800138001");
            result1.put("profile_name", "张三");
            result1.put("profile_id_card", "110101199001011234");
            results.add(result1);
            
            Map<String, Object> result2 = new HashMap<>();
            result2.put("user_id", 2L);
            result2.put("user_name", "user2");
            result2.put("user_phone", "13800138002");
            result2.put("profile_name", "李四");
            result2.put("profile_id_card", "310101199002021234");
            results.add(result2);
            
            return results;
        }

        private List<Map<String, Object>> createSubQueryData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("password", "password123");
            result1.put("phone", "13800138001");
            result1.put("email", "user1@example.com");
            result1.put("profile_name", "张三");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createAggregationQueryData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("username", "user1");
            result1.put("order_count", 3L);
            result1.put("max_amount", 1500.0);
            result1.put("avg_amount", 1000.0);
            result1.put("customer_names", "张三,李四,王五");
            results.add(result1);
            
            Map<String, Object> result2 = new HashMap<>();
            result2.put("username", "user2");
            result2.put("order_count", 2L);
            result2.put("max_amount", 2000.0);
            result2.put("avg_amount", 1200.0);
            result2.put("customer_names", "赵六,孙七");
            results.add(result2);
            
            return results;
        }

        private List<Map<String, Object>> createComplexConditionData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("password", "password123");
            result1.put("phone", "13800138001");
            result1.put("email", "user1@example.com");
            result1.put("age", 25);
            result1.put("real_name", "张三");
            result1.put("order_no", "ORD001");
            result1.put("customer_name", "张三");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createMapResultData() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("ID", 1L);
            result1.put("USERNAME", "user1");
            result1.put("PASSWORD", "password123");
            result1.put("PHONE", "13800138001");
            result1.put("EMAIL", "user1@example.com");
            result1.put("REAL_NAME", "张三");
            result1.put("CUSTOMER_NAME", "张三");
            results.add(result1);
            
            return results;
        }
    }
}
