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
 * MyBatis拦截器集成测试类
 * 测试FieldEncryptionService在MyBatis拦截器中的使用场景
 * 模拟真实的SQL执行流程
 * 
 * @author chu7
 * @date 2025/9/22
 */
@Slf4j
@SpringBootApplication
public class MyBatisInterceptorIntegrationTest {

    public static void main(String[] args) {
        SpringApplication.run(MyBatisInterceptorIntegrationTest.class, args);
    }

    @Component
    public static class MyBatisInterceptorTestRunner implements CommandLineRunner {

        @Autowired
        private FieldEncryptionService fieldEncryptionService;

        @Override
        public void run(String... args) throws Exception {
            testParameterEncryption();
            testResultDecryption();
            testBatchOperation();
            testComplexQueryResult();
            testMapParameterHandling();
            testWrapperParameterHandling();
        }

        /**
         * 测试1：参数加密（模拟PoJoParamEncryptorInterceptor）
         * 测试各种参数类型的加密处理
         */
        public void testParameterEncryption() {
            log.info("=== 测试参数加密（模拟PoJoParamEncryptorInterceptor）===");
            
            // 测试单个实体对象参数
            testSingleEntityParameter();
            
            // 测试Map参数
            testMapParameter();
            
            // 测试List参数
            testListParameter();
            
            // 测试基本类型参数
            testBasicTypeParameter();
            
            log.info("参数加密测试完成！");
        }

        /**
         * 测试2：结果解密（模拟PoJoResultDecryptorInterceptor）
         * 测试各种结果类型的解密处理
         */
        public void testResultDecryption() {
            log.info("=== 测试结果解密（模拟PoJoResultDecryptorInterceptor）===");
            
            // 测试单个实体对象结果
            testSingleEntityResult();
            
            // 测试List结果
            testListResult();
            
            // 测试Map结果
            testMapResult();
            
            // 测试基本类型结果
            testBasicTypeResult();
            
            log.info("结果解密测试完成！");
        }

        /**
         * 测试3：批量操作
         * 测试批量插入、更新、删除操作的参数处理
         */
        public void testBatchOperation() {
            log.info("=== 测试批量操作 ===");
            
            // 测试批量插入
            testBatchInsert();
            
            // 测试批量更新
            testBatchUpdate();
            
            // 测试批量删除
            testBatchDelete();
            
            log.info("批量操作测试完成！");
        }

        /**
         * 测试4：复杂查询结果
         * 测试复杂SQL查询结果的解密处理
         */
        public void testComplexQueryResult() {
            log.info("=== 测试复杂查询结果 ===");
            
            // 测试多表关联查询结果
            testMultiTableJoinResult();
            
            // 测试带别名的查询结果
            testAliasQueryResult();
            
            // 测试聚合查询结果
            testAggregationQueryResult();
            
            log.info("复杂查询结果测试完成！");
        }

        /**
         * 测试5：Map参数处理
         * 测试MyBatis中Map类型参数的处理
         */
        public void testMapParameterHandling() {
            log.info("=== 测试Map参数处理 ===");
            
            // 测试简单Map参数
            Map<String, Object> simpleMap = new HashMap<>();
            simpleMap.put("username", "testuser");
            simpleMap.put("password", "password123");
            simpleMap.put("phone", "13800138000");
            simpleMap.put("email", "test@example.com");
            
            log.info("原始Map参数: {}", simpleMap);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            
            // 加密Map参数
            Map<String, Object> encryptedMap = fieldEncryptionService.encryptMap(simpleMap, fieldMappings);
            log.info("加密后Map参数: {}", encryptedMap);
            
            // 解密Map参数
            Map<String, Object> decryptedMap = fieldEncryptionService.decryptMap(encryptedMap, fieldMappings);
            log.info("解密后Map参数: {}", decryptedMap);
            
            // 验证数据一致性
            assert simpleMap.get("password").equals(decryptedMap.get("password"));
            assert simpleMap.get("phone").equals(decryptedMap.get("phone"));
            assert simpleMap.get("email").equals(decryptedMap.get("email"));
            
            log.info("Map参数处理测试通过！");
        }

        /**
         * 测试6：Wrapper参数处理
         * 测试MyBatis-Plus Wrapper类型参数的处理
         */
        public void testWrapperParameterHandling() {
            log.info("=== 测试Wrapper参数处理 ===");
            
            // 模拟MyBatis-Plus的Wrapper参数
            Map<String, Object> wrapperMap = new HashMap<>();
            wrapperMap.put("et", createUserEntity()); // 实体对象
            wrapperMap.put("ew", "LambdaUpdateWrapper"); // Wrapper对象
            wrapperMap.put("param1", "value1"); // 其他参数
            
            log.info("原始Wrapper参数: {}", wrapperMap);
            
            // 模拟拦截器中的参数处理逻辑
            Object processedParam = processWrapperParameter(wrapperMap);
            log.info("处理后的Wrapper参数: {}", processedParam);
            
            log.info("Wrapper参数处理测试通过！");
        }

        // ==================== 具体测试方法 ====================

        private void testSingleEntityParameter() {
            log.info("--- 测试单个实体对象参数 ---");
            
            User user = createUserEntity();
            log.info("原始实体参数: {}", user);
            
            // 检查是否有需要加密的字段
            if (fieldEncryptionService.hasEncryptFields(user.getClass())) {
                User encryptedUser = fieldEncryptionService.encryptObject(user);
                log.info("加密后实体参数: {}", encryptedUser);
                
                User decryptedUser = fieldEncryptionService.decryptObject(encryptedUser);
                log.info("解密后实体参数: {}", decryptedUser);
                
                // 验证数据一致性
                assert user.getPassword().equals(decryptedUser.getPassword());
                assert user.getPhone().equals(decryptedUser.getPhone());
                assert user.getEmail().equals(decryptedUser.getEmail());
            }
        }

        private void testMapParameter() {
            log.info("--- 测试Map参数 ---");
            
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("id", 1L);
            paramMap.put("username", "testuser");
            paramMap.put("password", "password123");
            paramMap.put("phone", "13800138000");
            paramMap.put("email", "test@example.com");
            
            log.info("原始Map参数: {}", paramMap);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            
            // 加密Map参数
            Map<String, Object> encryptedMap = fieldEncryptionService.encryptMap(paramMap, fieldMappings);
            log.info("加密后Map参数: {}", encryptedMap);
            
            // 解密Map参数
            Map<String, Object> decryptedMap = fieldEncryptionService.decryptMap(encryptedMap, fieldMappings);
            log.info("解密后Map参数: {}", decryptedMap);
        }

        private void testListParameter() {
            log.info("--- 测试List参数 ---");
            
            List<User> users = createUserList();
            log.info("原始List参数: {}", users);
            
            // 检查第一个元素是否有需要加密的字段
            if (!users.isEmpty() && fieldEncryptionService.hasEncryptFields(users.get(0).getClass())) {
                List<User> encryptedUsers = fieldEncryptionService.encryptObjects(users);
                log.info("加密后List参数: {}", encryptedUsers);
                
                List<User> decryptedUsers = fieldEncryptionService.decryptObjects(encryptedUsers);
                log.info("解密后List参数: {}", decryptedUsers);
            }
        }

        private void testBasicTypeParameter() {
            log.info("--- 测试基本类型参数 ---");
            
            String username = "testuser";
            Integer age = 25;
            Long id = 1L;
            
            log.info("基本类型参数: username={}, age={}, id={}", username, age, id);
            
            // 基本类型参数不需要加密
            log.info("基本类型参数无需加密处理");
        }

        private void testSingleEntityResult() {
            log.info("--- 测试单个实体对象结果 ---");
            
            User user = createUserEntity();
            log.info("原始实体结果: {}", user);
            
            // 检查是否有需要解密的字段
            if (fieldEncryptionService.hasEncryptFields(user.getClass())) {
                User decryptedUser = fieldEncryptionService.decryptObject(user);
                log.info("解密后实体结果: {}", decryptedUser);
            }
        }

        private void testListResult() {
            log.info("--- 测试List结果 ---");
            
            List<User> users = createUserList();
            log.info("原始List结果: {}", users);
            
            // 检查第一个元素是否有需要解密的字段
            if (!users.isEmpty() && fieldEncryptionService.hasEncryptFields(users.get(0).getClass())) {
                List<User> decryptedUsers = fieldEncryptionService.decryptObjects(users);
                log.info("解密后List结果: {}", decryptedUsers);
            }
        }

        private void testMapResult() {
            log.info("--- 测试Map结果 ---");
            
            Map<String, Object> resultMap = createMapResult();
            log.info("原始Map结果: {}", resultMap);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            
            // 解密Map结果
            Map<String, Object> decryptedMap = fieldEncryptionService.decryptMap(resultMap, fieldMappings);
            log.info("解密后Map结果: {}", decryptedMap);
        }

        private void testBasicTypeResult() {
            log.info("--- 测试基本类型结果 ---");
            
            String username = "testuser";
            Integer count = 10;
            
            log.info("基本类型结果: username={}, count={}", username, count);
            log.info("基本类型结果无需解密处理");
        }

        private void testBatchInsert() {
            log.info("--- 测试批量插入 ---");
            
            List<User> users = createUserList();
            log.info("原始批量插入数据: {}", users);
            
            // 批量加密
            List<User> encryptedUsers = fieldEncryptionService.encryptObjects(users);
            log.info("加密后批量插入数据: {}", encryptedUsers);
            
            // 模拟批量插入后的解密（用于返回给前端）
            List<User> decryptedUsers = fieldEncryptionService.decryptObjects(encryptedUsers);
            log.info("解密后批量插入数据: {}", decryptedUsers);
        }

        private void testBatchUpdate() {
            log.info("--- 测试批量更新 ---");
            
            List<User> users = createUserList();
            log.info("原始批量更新数据: {}", users);
            
            // 批量加密
            List<User> encryptedUsers = fieldEncryptionService.encryptObjects(users);
            log.info("加密后批量更新数据: {}", encryptedUsers);
        }

        private void testBatchDelete() {
            log.info("--- 测试批量删除 ---");
            
            List<Long> ids = new ArrayList<>();
            ids.add(1L);
            ids.add(2L);
            ids.add(3L);
            log.info("批量删除ID: {}", ids);
            log.info("批量删除操作无需加密处理");
        }

        private void testMultiTableJoinResult() {
            log.info("--- 测试多表关联查询结果 ---");
            
            List<Map<String, Object>> results = createMultiTableJoinResult();
            log.info("原始多表关联查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("real_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("id_card", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : results) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后多表关联查询结果: {}", decryptedResults);
        }

        private void testAliasQueryResult() {
            log.info("--- 测试带别名的查询结果 ---");
            
            List<Map<String, Object>> results = createAliasQueryResult();
            log.info("原始带别名查询结果: {}", results);
            
            // 定义字段映射关系（处理别名）
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("user_phone", Base64PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("user_email", Md5PoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_name", AesPoJoFieldEncryptorStrategy.class);
            fieldMappings.put("profile_id_card", Base64PoJoFieldEncryptorStrategy.class);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : results) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后带别名查询结果: {}", decryptedResults);
        }

        private void testAggregationQueryResult() {
            log.info("--- 测试聚合查询结果 ---");
            
            List<Map<String, Object>> results = createAggregationQueryResult();
            log.info("原始聚合查询结果: {}", results);
            
            // 定义字段映射关系
            Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
            fieldMappings.put("customer_names", AesPoJoFieldEncryptorStrategy.class);
            
            // 批量解密Map结果
            List<Map<String, Object>> decryptedResults = new ArrayList<>();
            for (Map<String, Object> result : results) {
                Map<String, Object> decryptedResult = fieldEncryptionService.decryptMap(result, fieldMappings);
                decryptedResults.add(decryptedResult);
            }
            
            log.info("解密后聚合查询结果: {}", decryptedResults);
        }

        /**
         * 处理Wrapper参数（模拟拦截器逻辑）
         */
        private Object processWrapperParameter(Map<String, Object> paramMap) {
            if (paramMap == null) {
                return null;
            }
            
            // 查找实体对象
            Object entityObject = null;
            for (Object value : paramMap.values()) {
                if (value != null && !isWrapperClass(value.getClass()) && 
                    fieldEncryptionService.hasEncryptFields(value.getClass())) {
                    entityObject = value;
                    break;
                }
            }
            
            if (entityObject != null) {
                // 加密实体对象
                return fieldEncryptionService.encryptObject(entityObject);
            }
            
            return paramMap;
        }

        /**
         * 判断是否为Wrapper类
         */
        private boolean isWrapperClass(Class<?> clazz) {
            String className = clazz.getSimpleName();
            return className.contains("Wrapper") || className.contains("Query") || className.contains("Update");
        }

        // ==================== 数据创建方法 ====================

        private User createUserEntity() {
            User user = new User();
            user.setId(1L);
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setPhone("13800138000");
            user.setEmail("test@example.com");
            user.setAddress("北京市朝阳区");
            user.setAge(25);
            return user;
        }

        private List<User> createUserList() {
            List<User> users = new ArrayList<>();
            
            User user1 = new User();
            user1.setId(1L);
            user1.setUsername("user1");
            user1.setPassword("password123");
            user1.setPhone("13800138001");
            user1.setEmail("user1@example.com");
            user1.setAddress("北京市朝阳区");
            user1.setAge(25);
            users.add(user1);
            
            User user2 = new User();
            user2.setId(2L);
            user2.setUsername("user2");
            user2.setPassword("password456");
            user2.setPhone("13800138002");
            user2.setEmail("user2@example.com");
            user2.setAddress("上海市浦东区");
            user2.setAge(30);
            users.add(user2);
            
            return users;
        }

        private Map<String, Object> createMapResult() {
            Map<String, Object> result = new HashMap<>();
            result.put("id", 1L);
            result.put("username", "testuser");
            result.put("password", "password123");
            result.put("phone", "13800138000");
            result.put("email", "test@example.com");
            result.put("address", "北京市朝阳区");
            result.put("age", 25);
            return result;
        }

        private List<Map<String, Object>> createMultiTableJoinResult() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("username", "user1");
            result1.put("password", "password123");
            result1.put("phone", "13800138001");
            result1.put("email", "user1@example.com");
            result1.put("real_name", "张三");
            result1.put("id_card", "110101199001011234");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createAliasQueryResult() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("user_id", 1L);
            result1.put("user_name", "user1");
            result1.put("user_phone", "13800138001");
            result1.put("user_email", "user1@example.com");
            result1.put("profile_name", "张三");
            result1.put("profile_id_card", "110101199001011234");
            results.add(result1);
            
            return results;
        }

        private List<Map<String, Object>> createAggregationQueryResult() {
            List<Map<String, Object>> results = new ArrayList<>();
            
            Map<String, Object> result1 = new HashMap<>();
            result1.put("username", "user1");
            result1.put("order_count", 3L);
            result1.put("customer_names", "张三,李四,王五");
            results.add(result1);
            
            return results;
        }

        /**
         * 用户实体类
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
    }
}
