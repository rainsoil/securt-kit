package com.chu7.securtkit.test.service;

import com.chu7.securtkit.test.SecurtKitTestApplication;
import com.chu7.securtkit.test.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
@SpringBootTest(classes = SecurtKitTestApplication.class)
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreateAndFindUser() {
        log.info("=== 测试创建和查询用户 ===");
        
        // 创建用户
        User user = userService.createUser(
            "张三",
            "123456",
            "13800138000",
            "zhangsan@example.com",
            "110101199001011234",
            28
        );
        
        assertNotNull(user);
        assertNotNull(user.getId());
        log.info("创建的用户：{}", user);
        
        // 根据手机号查询
        User foundByPhone = userService.findByPhone("13800138000");
        assertNotNull(foundByPhone);
        assertEquals("张三", foundByPhone.getUsername());
        assertEquals("13800138000", foundByPhone.getPhone());
        log.info("根据手机号查询到的用户：{}", foundByPhone);
        
        // 根据邮箱查询
        User foundByEmail = userService.findByEmail("zhangsan@example.com");
        assertNotNull(foundByEmail);
        assertEquals("张三", foundByEmail.getUsername());
        assertEquals("zhangsan@example.com", foundByEmail.getEmail());
        log.info("根据邮箱查询到的用户：{}", foundByEmail);
        
        log.info("✅ 创建和查询用户测试通过");
    }

    @Test
    public void testUpdateUser() {
        log.info("=== 测试更新用户 ===");
        
        // 先创建用户
        User user = userService.createUser(
            "李四",
            "654321",
            "13900139000",
            "lisi@example.com",
            "110101199002022345",
            30
        );
        
        // 更新用户信息
        User updatedUser = userService.updateUser(
            user.getId(),
            "13700137000",
            "lisi_new@example.com",
            "110101199003033456"
        );
        
        assertNotNull(updatedUser);
        assertEquals("13700137000", updatedUser.getPhone());
        assertEquals("lisi_new@example.com", updatedUser.getEmail());
        assertEquals("110101199003033456", updatedUser.getIdCard());
        
        log.info("更新后的用户：{}", updatedUser);
        log.info("✅ 更新用户测试通过");
    }

    @Test
    public void testGetAllUsers() {
        log.info("=== 测试获取所有用户 ===");
        
        // 创建多个用户
        userService.createUser("用户1", "pass1", "13000000001", "user1@test.com", "110000000000000001", 25);
        userService.createUser("用户2", "pass2", "13000000002", "user2@test.com", "110000000000000002", 26);
        userService.createUser("用户3", "pass3", "13000000003", "user3@test.com", "110000000000000003", 27);
        
        List<User> allUsers = userService.getAllUsers();
        assertNotNull(allUsers);
        assertTrue(allUsers.size() >= 3);
        
        log.info("查询到 {} 个用户", allUsers.size());
        for (User user : allUsers) {
            log.info("用户：{}", user);
        }
        
        log.info("✅ 获取所有用户测试通过");
    }

    @Test
    public void testDeleteUser() {
        log.info("=== 测试删除用户 ===");
        
        // 先创建用户
        User user = userService.createUser(
            "待删除用户",
            "password",
            "13800000000",
            "delete@test.com",
            "110000000000000000",
            25
        );
        
        // 删除用户
        boolean deleted = userService.deleteUser(user.getId());
        assertTrue(deleted);
        
        // 验证用户已被删除
        User foundUser = userService.findByPhone("13800000000");
        assertNull(foundUser);
        
        log.info("✅ 删除用户测试通过");
    }

    @Test
    public void testComplexQuery() {
        log.info("=== 测试复杂查询 ===");
        
        // 创建用户
        userService.createUser(
            "复杂查询用户",
            "password",
            "13888888888",
            "complex@test.com",
            "110101199001011111",
            35
        );
        
        // 根据手机号和邮箱查询
        User user = userService.findByPhoneAndEmail("13888888888", "complex@test.com");
        assertNotNull(user);
        assertEquals("复杂查询用户", user.getUsername());
        assertEquals("13888888888", user.getPhone());
        assertEquals("complex@test.com", user.getEmail());
        
        log.info("复杂查询结果：{}", user);
        log.info("✅ 复杂查询测试通过");
    }

    @Test
    public void testUserLogin() {
        log.info("=== 测试用户登录 ===");
        
        // 创建用户
        userService.createUser(
            "登录测试用户",
            "loginpass",
            "13999999999",
            "login@test.com",
            "110101199001012222",
            28
        );
        
        // 测试登录
        boolean loginResult = userService.validateLogin("13999999999", "loginpass");
        assertTrue(loginResult);
        
        log.info("✅ 用户登录测试通过");
    }
}