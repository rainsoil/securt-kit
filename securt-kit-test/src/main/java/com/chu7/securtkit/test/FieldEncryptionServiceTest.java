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
 * FieldEncryptionService 测试类
 * 
 * @author chu7
 * @date 2025/9/22
 */
@Slf4j
@SpringBootApplication
public class FieldEncryptionServiceTest {

    public static void main(String[] args) {
        SpringApplication.run(FieldEncryptionServiceTest.class, args);
    }

    @Component
    public static class FieldEncryptionTestRunner implements CommandLineRunner {

        @Autowired
        private FieldEncryptionService fieldEncryptionService;

    /**
     * 测试用户实体类
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
    }

        @Override
        public void run(String... args) throws Exception {
            testBasicEncryption();
            testBatchEncryption();
            testMapEncryption();
            testFieldInfoQuery();
            testNullHandling();
            testTableCacheIntegration();
        }

        /**
         * 测试基本加密功能
         */
        public void testBasicEncryption() {
        log.info("=== 测试基本加密功能 ===");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setAddress("北京市朝阳区");
        
        log.info("原始用户信息: {}", user);
        
        // 加密
        User encryptedUser = fieldEncryptionService.encryptObject(user);
        log.info("加密后用户信息: {}", encryptedUser);
        
        // 解密
        User decryptedUser = fieldEncryptionService.decryptObject(encryptedUser);
        log.info("解密后用户信息: {}", decryptedUser);
        
        // 验证
        assert "123456".equals(decryptedUser.getPassword());
        assert "13800138000".equals(decryptedUser.getPhone());
        assert "test@example.com".equals(decryptedUser.getEmail());
        
        log.info("基本加密功能测试通过！");
    }

        /**
         * 测试批量加密功能
         */
        public void testBatchEncryption() {
        log.info("=== 测试批量加密功能 ===");
        
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setPassword("password" + i);
            user.setPhone("1380013800" + i);
            user.setEmail("user" + i + "@example.com");
            users.add(user);
        }
        
        log.info("原始用户列表: {}", users);
        
        // 批量加密
        List<User> encryptedUsers = fieldEncryptionService.encryptObjects(users);
        log.info("加密后用户列表: {}", encryptedUsers);
        
        // 批量解密
        List<User> decryptedUsers = fieldEncryptionService.decryptObjects(encryptedUsers);
        log.info("解密后用户列表: {}", decryptedUsers);
        
        // 验证
        for (int i = 0; i < users.size(); i++) {
            assert users.get(i).getPassword().equals(decryptedUsers.get(i).getPassword());
            assert users.get(i).getPhone().equals(decryptedUsers.get(i).getPhone());
            assert users.get(i).getEmail().equals(decryptedUsers.get(i).getEmail());
        }
        
        log.info("批量加密功能测试通过！");
    }

        /**
         * 测试Map加密功能
         */
        public void testMapEncryption() {
        log.info("=== 测试Map加密功能 ===");
        
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", 1L);
        userMap.put("username", "testuser");
        userMap.put("password", "123456");
        userMap.put("phone", "13800138000");
        userMap.put("email", "test@example.com");
        
        Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
        fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
        fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
        fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
        
        log.info("原始Map: {}", userMap);
        
        // 加密Map
        Map<String, Object> encryptedMap = fieldEncryptionService.encryptMap(userMap, fieldMappings);
        log.info("加密后Map: {}", encryptedMap);
        
        // 解密Map
        Map<String, Object> decryptedMap = fieldEncryptionService.decryptMap(encryptedMap, fieldMappings);
        log.info("解密后Map: {}", decryptedMap);
        
        // 验证
        assert "123456".equals(decryptedMap.get("password"));
        assert "13800138000".equals(decryptedMap.get("phone"));
        assert "test@example.com".equals(decryptedMap.get("email"));
        
        log.info("Map加密功能测试通过！");
    }

        /**
         * 测试字段信息查询功能
         */
        public void testFieldInfoQuery() {
        log.info("=== 测试字段信息查询功能 ===");
        
        boolean hasEncryptFields = fieldEncryptionService.hasEncryptFields(User.class);
        int fieldCount = fieldEncryptionService.getEncryptFieldCount(User.class);
        List<String> fieldNames = fieldEncryptionService.getEncryptFieldNames(User.class);
        
        log.info("是否有加密字段: {}", hasEncryptFields);
        log.info("加密字段数量: {}", fieldCount);
        log.info("加密字段名称: {}", fieldNames);
        
        assert hasEncryptFields;
        assert fieldCount == 3;
        assert fieldNames.contains("password");
        assert fieldNames.contains("phone");
        assert fieldNames.contains("email");
        
        log.info("字段信息查询功能测试通过！");
    }

        /**
         * 测试空值处理
         */
        public void testNullHandling() {
        log.info("=== 测试空值处理 ===");
        
        // 测试null对象
        User nullUser = fieldEncryptionService.encryptObject(null);
        assert nullUser == null;
        
        // 测试空列表
        List<User> emptyList = fieldEncryptionService.encryptObjects(new ArrayList<>());
        assert emptyList.isEmpty();
        
        // 测试空Map
        Map<String, Object> emptyMap = fieldEncryptionService.encryptMap(new HashMap<>(), new HashMap<>());
        assert emptyMap.isEmpty();
        
        // 测试部分字段为null的对象
        User userWithNullFields = new User();
        userWithNullFields.setId(1L);
        userWithNullFields.setUsername("testuser");
        // password, phone, email 都为null
        
        User encryptedUser = fieldEncryptionService.encryptObject(userWithNullFields);
        assert encryptedUser.getPassword() == null;
        assert encryptedUser.getPhone() == null;
        assert encryptedUser.getEmail() == null;
        
        log.info("空值处理测试通过！");
        }

        /**
         * 测试TableCache集成功能
         */
        public void testTableCacheIntegration() {
            log.info("=== 测试TableCache集成功能 ===");
            
            // 测试配置查询功能
            boolean hasEncryptFields = fieldEncryptionService.hasEncryptFields(User.class);
            int fieldCount = fieldEncryptionService.getEncryptFieldCount(User.class);
            List<String> fieldNames = fieldEncryptionService.getEncryptFieldNames(User.class);
            
            log.info("User类加密配置:");
            log.info("  是否有加密字段: {}", hasEncryptFields);
            log.info("  加密字段数量: {}", fieldCount);
            log.info("  加密字段名称: {}", fieldNames);
            
            // 验证配置是否正确
            assert hasEncryptFields : "应该有加密字段";
            assert fieldCount >= 3 : "加密字段数量应该至少为3";
            assert fieldNames.contains("password") : "应该包含password字段";
            assert fieldNames.contains("phone") : "应该包含phone字段";
            assert fieldNames.contains("email") : "应该包含email字段";
            
            log.info("TableCache集成功能测试通过！");
        }
    }
}
