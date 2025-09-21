package com.chu7.securtkit.test;

import com.chu7.securtkit.test.entity.User;
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
 * 简化的加密字段功能测试
 * 使用现有的UserService方法进行测试
 * 
 * @author chu7
 * @date 2025-09-21
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class SimpleEncryptionTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // 清理测试数据
        try {
            List<User> allUsers = userService.getAllUsers();
            for (User user : allUsers) {
                if (user.getUsername().contains("加密测试") || 
                    user.getUsername().contains("批量测试") ||
                    user.getUsername().contains("更新测试")) {
                    userService.deleteUser(user.getId());
                }
            }
        } catch (Exception e) {
            // 忽略清理错误
        }
    }

    /**
     * 测试密码字段MD5加密
     */
    @Test
    public void testPasswordEncryption() {
        log.info("=== 测试密码字段MD5加密 ===");
        
        String originalPassword = "123456";
        
        // 插入用户
        User savedUser = userService.createUser("加密测试用户1", originalPassword, 
                "13800138001", "test1@example.com", "110101199001010001", 25);
        assertNotNull(savedUser, "用户插入应该成功");
        
        // 验证密码是否被加密（MD5加密后长度应该是32位）
        String encryptedPassword = savedUser.getPassword();
        assertNotEquals(originalPassword, encryptedPassword, "密码应该被加密");
        assertEquals(32, encryptedPassword.length(), "MD5加密后密码长度应该是32位");
        
        log.info("原始密码: {}", originalPassword);
        log.info("加密后密码: {}", encryptedPassword);
        log.info("密码加密测试通过 ✓");
    }

    /**
     * 测试手机号字段DES加密
     */
    @Test
    public void testPhoneEncryption() {
        log.info("=== 测试手机号字段DES加密 ===");
        
        String originalPhone = "13800138002";
        
        // 插入用户
        User savedUser = userService.createUser("加密测试用户2", "123456", 
                originalPhone, "test2@example.com", "110101199001010002", 25);
        assertNotNull(savedUser, "用户插入应该成功");
        
        // 验证手机号是否被加密
        String encryptedPhone = savedUser.getPhone();
        assertNotEquals(originalPhone, encryptedPhone, "手机号应该被加密");
        
        log.info("原始手机号: {}", originalPhone);
        log.info("加密后手机号: {}", encryptedPhone);
        log.info("手机号加密测试通过 ✓");
    }

    /**
     * 测试邮箱字段AES加密
     */
    @Test
    public void testEmailEncryption() {
        log.info("=== 测试邮箱字段AES加密 ===");
        
        String originalEmail = "test3@example.com";
        
        // 插入用户
        User savedUser = userService.createUser("加密测试用户3", "123456", 
                "13800138003", originalEmail, "110101199001010003", 25);
        assertNotNull(savedUser, "用户插入应该成功");
        
        // 验证邮箱是否被加密
        String encryptedEmail = savedUser.getEmail();
        assertNotEquals(originalEmail, encryptedEmail, "邮箱应该被加密");
        
        log.info("原始邮箱: {}", originalEmail);
        log.info("加密后邮箱: {}", encryptedEmail);
        log.info("邮箱加密测试通过 ✓");
    }

    /**
     * 测试身份证字段Base64加密
     */
    @Test
    public void testIdCardEncryption() {
        log.info("=== 测试身份证字段Base64加密 ===");
        
        String originalIdCard = "110101199001010004";
        
        // 插入用户
        User savedUser = userService.createUser("加密测试用户4", "123456", 
                "13800138004", "test4@example.com", originalIdCard, 25);
        assertNotNull(savedUser, "用户插入应该成功");
        
        // 验证身份证是否被加密
        String encryptedIdCard = savedUser.getIdCard();
        assertNotEquals(originalIdCard, encryptedIdCard, "身份证应该被加密");
        
        log.info("原始身份证: {}", originalIdCard);
        log.info("加密后身份证: {}", encryptedIdCard);
        log.info("身份证加密测试通过 ✓");
    }

    /**
     * 测试更新操作中的字段加密
     */
    @Test
    public void testUpdateEncryption() {
        log.info("=== 测试更新操作中的字段加密 ===");
        
        // 1. 插入用户
        User user = userService.createUser("更新测试用户", "123456", 
                "13800138005", "update@example.com", "110101199001010005", 25);
        assertNotNull(user, "用户插入应该成功");
        
        log.info("更新前用户信息: {}", user);
        
        // 2. 更新用户信息
        User updatedUser = userService.updateUser(user.getId(), "13900139005", 
                "updated@example.com", "110101199001019999");
        assertNotNull(updatedUser, "用户更新应该成功");
        
        log.info("更新后用户信息: {}", updatedUser);
        
        // 3. 验证字段是否被重新加密
        assertNotEquals("13900139005", updatedUser.getPhone(), "更新后手机号应该被加密");
        assertNotEquals("updated@example.com", updatedUser.getEmail(), "更新后邮箱应该被加密");
        assertNotEquals("110101199001019999", updatedUser.getIdCard(), "更新后身份证应该被加密");
        
        log.info("更新操作加密测试通过 ✓");
    }

    /**
     * 测试查询操作中的字段解密
     */
    @Test
    public void testQueryDecryption() {
        log.info("=== 测试查询操作中的字段解密 ===");
        
        // 1. 插入用户
        User user = userService.createUser("查询测试用户", "123456", 
                "13800138006", "query@example.com", "110101199001010006", 25);
        assertNotNull(user, "用户插入应该成功");
        
        // 2. 根据手机号查询
        User foundByPhone = userService.findByPhone("13800138006");
        assertNotNull(foundByPhone, "根据手机号查询用户不应为空");
        
        // 3. 验证查询结果中的字段被正确解密
        assertEquals("13800138006", foundByPhone.getPhone(), "查询时手机号应该被解密");
        assertEquals("query@example.com", foundByPhone.getEmail(), "查询时邮箱应该被解密");
        assertEquals("110101199001010006", foundByPhone.getIdCard(), "查询时身份证应该被解密");
        
        log.info("查询解密测试通过 ✓");
    }

    /**
     * 测试批量操作中的字段加密
     */
    @Test
    public void testBatchEncryption() {
        log.info("=== 测试批量操作中的字段加密 ===");
        
        // 1. 创建多个用户
        User user1 = userService.createUser("批量测试用户1", "123456", 
                "13800138010", "batch1@example.com", "110101199001010010", 25);
        User user2 = userService.createUser("批量测试用户2", "123456", 
                "13800138011", "batch2@example.com", "110101199001010011", 26);
        User user3 = userService.createUser("批量测试用户3", "123456", 
                "13800138012", "batch3@example.com", "110101199001010012", 27);
        
        assertNotNull(user1, "用户1创建应该成功");
        assertNotNull(user2, "用户2创建应该成功");
        assertNotNull(user3, "用户3创建应该成功");
        
        // 2. 验证每个用户的字段都被加密
        assertNotEquals("123456", user1.getPassword(), "用户1密码应该被加密");
        assertNotEquals("13800138010", user1.getPhone(), "用户1手机号应该被加密");
        assertNotEquals("batch1@example.com", user1.getEmail(), "用户1邮箱应该被加密");
        assertNotEquals("110101199001010010", user1.getIdCard(), "用户1身份证应该被加密");
        
        assertNotEquals("123456", user2.getPassword(), "用户2密码应该被加密");
        assertNotEquals("13800138011", user2.getPhone(), "用户2手机号应该被加密");
        assertNotEquals("batch2@example.com", user2.getEmail(), "用户2邮箱应该被加密");
        assertNotEquals("110101199001010011", user2.getIdCard(), "用户2身份证应该被加密");
        
        assertNotEquals("123456", user3.getPassword(), "用户3密码应该被加密");
        assertNotEquals("13800138012", user3.getPhone(), "用户3手机号应该被加密");
        assertNotEquals("batch3@example.com", user3.getEmail(), "用户3邮箱应该被加密");
        assertNotEquals("110101199001010012", user3.getIdCard(), "用户3身份证应该被加密");
        
        log.info("批量操作加密测试通过 ✓");
    }

    /**
     * 测试数据一致性
     */
    @Test
    public void testDataConsistency() {
        log.info("=== 测试数据一致性 ===");
        
        // 1. 创建用户
        User user = userService.createUser("一致性测试用户", "123456", 
                "13800138020", "consistency@example.com", "110101199001010020", 25);
        assertNotNull(user, "用户创建应该成功");
        
        // 2. 多次查询验证数据一致性
        for (int i = 1; i <= 3; i++) {
            User queriedUser = userService.findByPhone("13800138020");
            assertNotNull(queriedUser, "第" + i + "次查询用户不应为空");
            
            // 验证解密后的数据一致性
            assertEquals("13800138020", queriedUser.getPhone(), "第" + i + "次查询手机号应该一致");
            assertEquals("consistency@example.com", queriedUser.getEmail(), "第" + i + "次查询邮箱应该一致");
            assertEquals("110101199001010020", queriedUser.getIdCard(), "第" + i + "次查询身份证应该一致");
            
            log.info("第{}次查询结果: 手机号={}, 邮箱={}, 身份证={}", 
                    i, queriedUser.getPhone(), queriedUser.getEmail(), queriedUser.getIdCard());
        }
        
        // 3. 通过不同方式查询验证一致性
        User byPhone = userService.findByPhone("13800138020");
        User byEmail = userService.findByEmail("consistency@example.com");
        
        assertNotNull(byPhone, "通过手机号查询用户不应为空");
        assertNotNull(byEmail, "通过邮箱查询用户不应为空");
        
        // 验证两种查询方式的结果一致性
        assertEquals(byPhone.getPhone(), byEmail.getPhone(), "两种查询方式手机号应该一致");
        assertEquals(byPhone.getEmail(), byEmail.getEmail(), "两种查询方式邮箱应该一致");
        assertEquals(byPhone.getIdCard(), byEmail.getIdCard(), "两种查询方式身份证应该一致");
        
        log.info("数据一致性测试通过 ✓");
    }

    /**
     * 测试加密策略分析
     */
    @Test
    public void testEncryptionStrategyAnalysis() {
        log.info("=== 测试加密策略分析 ===");
        
        User user = userService.createUser("策略分析用户", "123456", 
                "13800138030", "strategy@example.com", "110101199001010030", 25);
        assertNotNull(user, "用户创建应该成功");
        
        log.info("用户信息: {}", user);
        
        // 分析不同字段的加密结果
        String password = user.getPassword();
        String phone = user.getPhone();
        String email = user.getEmail();
        String idCard = user.getIdCard();
        
        log.info("密码加密分析:");
        log.info("  原始: 123456");
        log.info("  加密后: {}", password);
        log.info("  长度: {} (MD5加密后应该是32位)", password.length());
        log.info("  格式: {} (应该是十六进制)", password.matches("[a-f0-9]+") ? "正确" : "错误");
        
        log.info("手机号加密分析:");
        log.info("  原始: 13800138030");
        log.info("  加密后: {}", phone);
        log.info("  长度: {} (DES加密后长度会变化)", phone.length());
        log.info("  是否不同: {}", !phone.equals("13800138030") ? "是" : "否");
        
        log.info("邮箱加密分析:");
        log.info("  原始: strategy@example.com");
        log.info("  加密后: {}", email);
        log.info("  长度: {} (AES加密后长度会变化)", email.length());
        log.info("  是否不同: {}", !email.equals("strategy@example.com") ? "是" : "否");
        
        log.info("身份证加密分析:");
        log.info("  原始: 110101199001010030");
        log.info("  加密后: {}", idCard);
        log.info("  长度: {} (Base64加密后长度会变化)", idCard.length());
        log.info("  是否不同: {}", !idCard.equals("110101199001010030") ? "是" : "否");
        
        log.info("加密策略分析测试通过 ✓");
    }
}

