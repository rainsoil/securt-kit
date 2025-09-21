package com.chu7.securtkit.test;

import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.mapper.UserMapper;
import com.chu7.securtkit.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简化的MyBatis-Plus测试类
 * 使用现有的UserService和UserMapper进行测试
 * 
 * @author chu7
 * @date 2025-09-21
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class SimpleMyBatisPlusTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // 清理测试数据
        userMapper.deleteById(999L);
        userMapper.deleteById(998L);
        userMapper.deleteById(997L);
    }

    /**
     * 测试基本插入和查询操作
     */
    @Test
    public void testBasicInsertAndSelect() {
        log.info("=== 测试基本插入和查询操作 ===");
        
        // 1. 使用Mapper直接插入
        User user = new User();
        user.setId(999L);
        user.setUsername("测试用户999");
        user.setPassword("123456");
        user.setPhone("13800138999");
        user.setEmail("test999@example.com");
        user.setIdCard("110101199001019999");
        user.setAge(25);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        int insertResult = userMapper.insert(user);
        log.info("插入结果: {}, 用户ID: {}", insertResult, user.getId());
        assertEquals(1, insertResult, "插入应该成功");
        
        // 2. 根据ID查询
        User savedUser = userMapper.selectById(999L);
        assertNotNull(savedUser, "查询用户不应为空");
        log.info("查询结果: {}", savedUser);
        
        // 3. 验证加密字段
        assertNotEquals("123456", savedUser.getPassword(), "密码应该被加密");
        assertNotEquals("13800138999", savedUser.getPhone(), "手机号应该被加密");
        assertNotEquals("test999@example.com", savedUser.getEmail(), "邮箱应该被加密");
        assertNotEquals("110101199001019999", savedUser.getIdCard(), "身份证应该被加密");
        
        log.info("基本插入和查询测试通过 ✓");
    }

    /**
     * 测试Service层操作
     */
    @Test
    public void testServiceOperations() {
        log.info("=== 测试Service层操作 ===");
        
        // 1. 创建用户
        User user = userService.createUser("Service测试用户", "123456", "13800138998", 
                "service@example.com", "110101199001019998", 26);
        assertNotNull(user, "创建用户不应为空");
        assertNotNull(user.getId(), "用户ID不应为空");
        log.info("创建用户结果: {}", user);
        
        // 2. 根据手机号查询
        User foundUser = userService.findByPhone("13800138998");
        assertNotNull(foundUser, "根据手机号查询用户不应为空");
        log.info("根据手机号查询结果: {}", foundUser);
        
        // 3. 根据邮箱查询
        User foundByEmail = userService.findByEmail("service@example.com");
        assertNotNull(foundByEmail, "根据邮箱查询用户不应为空");
        log.info("根据邮箱查询结果: {}", foundByEmail);
        
        // 4. 更新用户信息
        User updatedUser = userService.updateUser(user.getId(), "13900139998", 
                "updated@example.com", "110101199001019997");
        assertNotNull(updatedUser, "更新用户不应为空");
        log.info("更新用户结果: {}", updatedUser);
        
        // 5. 获取所有用户
        List<User> allUsers = userService.getAllUsers();
        assertFalse(allUsers.isEmpty(), "用户列表不应为空");
        log.info("所有用户数量: {}", allUsers.size());
        
        // 6. 删除用户
        boolean deleteResult = userService.deleteUser(user.getId());
        assertTrue(deleteResult, "删除用户应该成功");
        log.info("删除用户结果: {}", deleteResult);
        
        log.info("Service层操作测试通过 ✓");
    }

    /**
     * 测试加密字段的完整流程
     */
    @Test
    public void testEncryptionFlow() {
        log.info("=== 测试加密字段的完整流程 ===");
        
        // 1. 创建用户（应该加密敏感字段）
        User user = userService.createUser("加密流程测试", "123456", "13800138997", 
                "encryption@example.com", "110101199001019997", 25);
        
        log.info("创建后用户信息: {}", user);
        
        // 2. 验证插入时的加密
        assertNotEquals("123456", user.getPassword(), "密码应该被加密");
        assertNotEquals("13800138997", user.getPhone(), "手机号应该被加密");
        assertNotEquals("encryption@example.com", user.getEmail(), "邮箱应该被加密");
        assertNotEquals("110101199001019997", user.getIdCard(), "身份证应该被加密");
        
        // 3. 查询用户（应该解密敏感字段）
        User savedUser = userService.findByPhone("13800138997");
        assertNotNull(savedUser, "查询用户不应为空");
        
        log.info("查询后用户信息: {}", savedUser);
        
        // 4. 验证查询时的解密
        assertEquals("13800138997", savedUser.getPhone(), "查询时手机号应该被解密");
        assertEquals("encryption@example.com", savedUser.getEmail(), "查询时邮箱应该被解密");
        assertEquals("110101199001019997", savedUser.getIdCard(), "查询时身份证应该被解密");
        
        // 5. 更新用户信息（应该重新加密）
        User updatedUser = userService.updateUser(user.getId(), "13900139997", 
                "updated_encryption@example.com", "110101199001019996");
        
        log.info("更新后用户信息: {}", updatedUser);
        
        // 6. 验证更新后的加密
        assertNotEquals("13900139997", updatedUser.getPhone(), "更新后手机号应该被加密");
        assertNotEquals("updated_encryption@example.com", updatedUser.getEmail(), "更新后邮箱应该被加密");
        assertNotEquals("110101199001019996", updatedUser.getIdCard(), "更新后身份证应该被加密");
        
        // 7. 再次查询验证解密
        User finalUser = userService.findByPhone("13900139997");
        assertNotNull(finalUser, "最终查询用户不应为空");
        
        log.info("最终查询用户信息: {}", finalUser);
        
        // 8. 验证最终解密结果
        assertEquals("13900139997", finalUser.getPhone(), "最终查询时手机号应该被解密");
        assertEquals("updated_encryption@example.com", finalUser.getEmail(), "最终查询时邮箱应该被解密");
        assertEquals("110101199001019996", finalUser.getIdCard(), "最终查询时身份证应该被解密");
        
        log.info("加密字段完整流程测试通过 ✓");
    }

    /**
     * 测试不同加密策略
     */
    @Test
    public void testDifferentEncryptionStrategies() {
        log.info("=== 测试不同加密策略 ===");
        
        User user = userService.createUser("加密策略测试", "123456", "13800138996", 
                "strategy@example.com", "110101199001019996", 25);
        
        log.info("用户信息: {}", user);
        
        // 验证不同字段的加密结果
        String password = user.getPassword();
        String phone = user.getPhone();
        String email = user.getEmail();
        String idCard = user.getIdCard();
        
        // 密码使用MD5加密（32位十六进制字符串）
        assertEquals(32, password.length(), "MD5加密后密码长度应该是32位");
        assertTrue(password.matches("[a-f0-9]+"), "MD5加密后密码应该只包含十六进制字符");
        
        // 手机号使用DES加密（Base64编码）
        assertNotEquals("13800138996", phone, "手机号应该被DES加密");
        assertTrue(phone.length() > 10, "DES加密后手机号长度应该大于10");
        
        // 邮箱使用AES加密（十六进制编码）
        assertNotEquals("strategy@example.com", email, "邮箱应该被AES加密");
        assertTrue(email.length() > 20, "AES加密后邮箱长度应该大于20");
        
        // 身份证使用Base64加密
        assertNotEquals("110101199001019996", idCard, "身份证应该被Base64加密");
        assertTrue(idCard.length() > 15, "Base64加密后身份证长度应该大于15");
        
        log.info("不同加密策略测试通过 ✓");
    }

    /**
     * 测试批量操作
     */
    @Test
    public void testBatchOperations() {
        log.info("=== 测试批量操作 ===");
        
        // 1. 批量创建用户
        User user1 = userService.createUser("批量用户1", "123456", "13800138001", 
                "batch1@example.com", "110101199001010001", 25);
        User user2 = userService.createUser("批量用户2", "123456", "13800138002", 
                "batch2@example.com", "110101199001010002", 26);
        User user3 = userService.createUser("批量用户3", "123456", "13800138003", 
                "batch3@example.com", "110101199001010003", 27);
        
        // 2. 验证所有用户都被正确加密
        List<User> allUsers = userService.getAllUsers();
        long batchUserCount = allUsers.stream()
                .filter(u -> u.getUsername().startsWith("批量用户"))
                .count();
        
        assertEquals(3, batchUserCount, "应该创建3个批量用户");
        
        // 3. 验证每个用户的字段都被加密
        allUsers.stream()
                .filter(u -> u.getUsername().startsWith("批量用户"))
                .forEach(user -> {
                    assertNotEquals("123456", user.getPassword(), "批量用户密码应该被加密");
                    assertNotEquals("13800138001", user.getPhone(), "批量用户手机号应该被加密");
                    assertNotEquals("batch1@example.com", user.getEmail(), "批量用户邮箱应该被加密");
                    assertNotEquals("110101199001010001", user.getIdCard(), "批量用户身份证应该被加密");
                });
        
        log.info("批量操作测试通过 ✓");
    }

    /**
     * 测试数据一致性
     */
    @Test
    public void testDataConsistency() {
        log.info("=== 测试数据一致性 ===");
        
        // 1. 创建用户
        User user = userService.createUser("一致性测试", "123456", "13800138995", 
                "consistency@example.com", "110101199001019995", 25);
        
        // 2. 多次查询验证数据一致性
        for (int i = 0; i < 3; i++) {
            User queriedUser = userService.findByPhone("13800138995");
            assertNotNull(queriedUser, "第" + (i + 1) + "次查询用户不应为空");
            
            // 验证解密后的数据一致性
            assertEquals("13800138995", queriedUser.getPhone(), "第" + (i + 1) + "次查询手机号应该一致");
            assertEquals("consistency@example.com", queriedUser.getEmail(), "第" + (i + 1) + "次查询邮箱应该一致");
            assertEquals("110101199001019995", queriedUser.getIdCard(), "第" + (i + 1) + "次查询身份证应该一致");
            
            log.info("第{}次查询结果: {}", i + 1, queriedUser);
        }
        
        log.info("数据一致性测试通过 ✓");
    }
}
