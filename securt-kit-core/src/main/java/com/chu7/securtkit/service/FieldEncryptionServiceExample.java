package com.chu7.securtkit.service;

import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Md5PoJoFieldEncryptorStrategy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FieldEncryptionService 使用示例
 * 展示如何在业务代码中使用字段加密服务
 * 
 * @author chu7
 * @date 2025/9/22
 */
@Slf4j
@Service
public class FieldEncryptionServiceExample {

    @Autowired
    private FieldEncryptionService fieldEncryptionService;

    /**
     * 示例用户实体类
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

    /**
     * 示例1：在Service层使用字段加密服务
     * 替换原有的手动加密逻辑
     */
    public User saveUser(User user) {
        log.info("保存用户，原始数据: {}", user);
        
        // 使用字段加密服务自动加密敏感字段
        User encryptedUser = fieldEncryptionService.encryptObject(user);
        
        // 这里可以调用Mapper进行数据库操作
        // userMapper.insert(encryptedUser);
        
        log.info("用户数据已加密，准备保存到数据库");
        return encryptedUser;
    }

    /**
     * 示例2：查询用户时自动解密
     */
    public User getUserById(Long id) {
        // 模拟从数据库查询
        User user = new User();
        user.setId(id);
        user.setUsername("testuser");
        user.setPassword("encrypted_password_from_db");
        user.setPhone("encrypted_phone_from_db");
        user.setEmail("encrypted_email_from_db");
        
        log.info("从数据库查询到用户，加密数据: {}", user);
        
        // 使用字段加密服务自动解密敏感字段
        User decryptedUser = fieldEncryptionService.decryptObject(user);
        
        log.info("用户数据已解密，返回给业务层: {}", decryptedUser);
        return decryptedUser;
    }

    /**
     * 示例3：批量处理用户数据
     */
    public List<User> batchProcessUsers(List<User> users) {
        log.info("批量处理 {} 个用户", users.size());
        
        // 批量加密
        List<User> encryptedUsers = fieldEncryptionService.encryptObjects(users);
        log.info("批量加密完成");
        
        // 这里可以进行批量数据库操作
        // userMapper.batchInsert(encryptedUsers);
        
        // 批量解密（如果需要返回给前端）
        List<User> decryptedUsers = fieldEncryptionService.decryptObjects(encryptedUsers);
        log.info("批量解密完成");
        
        return decryptedUsers;
    }

    /**
     * 示例4：处理Map类型的数据
     */
    public Map<String, Object> processUserMap(Map<String, Object> userMap) {
        log.info("处理用户Map数据: {}", userMap);
        
        // 定义字段映射关系
        Map<String, Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
        fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
        fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
        fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
        
        // 加密Map中的敏感字段
        Map<String, Object> encryptedMap = fieldEncryptionService.encryptMap(userMap, fieldMappings);
        log.info("Map数据加密完成: {}", encryptedMap);
        
        // 这里可以进行数据库操作
        // userMapper.insertFromMap(encryptedMap);
        
        // 解密Map中的敏感字段
        Map<String, Object> decryptedMap = fieldEncryptionService.decryptMap(encryptedMap, fieldMappings);
        log.info("Map数据解密完成: {}", decryptedMap);
        
        return decryptedMap;
    }

    /**
     * 示例5：在拦截器中使用字段加密服务
     * 替换原有的复杂加密逻辑
     */
    public Object processInterceptorData(Object parameterObject) {
        if (parameterObject == null) {
            return null;
        }
        
        log.info("拦截器处理参数对象: {}", parameterObject.getClass().getSimpleName());
        
        // 检查是否有需要加密的字段
        if (fieldEncryptionService.hasEncryptFields(parameterObject.getClass())) {
            int fieldCount = fieldEncryptionService.getEncryptFieldCount(parameterObject.getClass());
            List<String> fieldNames = fieldEncryptionService.getEncryptFieldNames(parameterObject.getClass());
            
            log.info("发现 {} 个需要加密的字段: {}", fieldCount, fieldNames);
            
            // 自动加密
            Object encryptedObject = fieldEncryptionService.encryptObject(parameterObject);
            log.info("参数对象加密完成");
            
            return encryptedObject;
        } else {
            log.debug("参数对象无需加密");
            return parameterObject;
        }
    }

    /**
     * 示例6：处理查询结果
     */
    public Object processQueryResult(Object result) {
        if (result == null) {
            return null;
        }
        
        log.info("处理查询结果: {}", result.getClass().getSimpleName());
        
        // 如果是List类型，批量处理
        if (result instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> resultList = (List<Object>) result;
            
            if (!resultList.isEmpty()) {
                // 检查第一个元素的类型
                Object firstElement = resultList.get(0);
                if (fieldEncryptionService.hasEncryptFields(firstElement.getClass())) {
                    log.info("批量解密查询结果，共 {} 条记录", resultList.size());
                    return fieldEncryptionService.decryptObjects(resultList);
                }
            }
        } else {
            // 单个对象处理
            if (fieldEncryptionService.hasEncryptFields(result.getClass())) {
                log.info("解密单个查询结果");
                return fieldEncryptionService.decryptObject(result);
            }
        }
        
        log.debug("查询结果无需解密");
        return result;
    }

    /**
     * 示例7：配置验证和调试
     */
    public void validateConfiguration() {
        log.info("=== 字段加密配置验证 ===");
        
        // 检查User类的加密配置
        boolean hasEncryptFields = fieldEncryptionService.hasEncryptFields(User.class);
        int fieldCount = fieldEncryptionService.getEncryptFieldCount(User.class);
        List<String> fieldNames = fieldEncryptionService.getEncryptFieldNames(User.class);
        
        log.info("User类加密配置:");
        log.info("  是否有加密字段: {}", hasEncryptFields);
        log.info("  加密字段数量: {}", fieldCount);
        log.info("  加密字段名称: {}", fieldNames);
        
        if (hasEncryptFields) {
            log.info("配置验证通过，发现 {} 个加密字段", fieldCount);
        } else {
            log.warn("配置验证失败，未发现加密字段");
        }
    }

    /**
     * 示例8：支持配置文件模式的实体类
     * 这个实体类没有注解，完全依赖配置文件
     */
    @Data
    public static class ConfigBasedUser {
        private Long id;
        private String username;
        private String password;  // 通过配置文件配置加密
        private String phone;     // 通过配置文件配置加密
        private String email;     // 通过配置文件配置加密
        private String address;   // 不加密
    }

    /**
     * 示例9：配置文件模式的使用
     */
    public ConfigBasedUser processConfigBasedUser(ConfigBasedUser user) {
        log.info("处理基于配置文件的用户: {}", user);
        
        // 即使没有注解，也能自动加密（依赖配置文件）
        ConfigBasedUser encryptedUser = fieldEncryptionService.encryptObject(user);
        log.info("配置文件模式加密完成: {}", encryptedUser);
        
        // 解密
        ConfigBasedUser decryptedUser = fieldEncryptionService.decryptObject(encryptedUser);
        log.info("配置文件模式解密完成: {}", decryptedUser);
        
        return decryptedUser;
    }
}
