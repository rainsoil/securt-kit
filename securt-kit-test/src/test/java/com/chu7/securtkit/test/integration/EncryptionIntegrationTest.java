package com.chu7.securtkit.test.integration;

import com.chu7.securtkit.test.SecurtKitTestApplication;
import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.mapper.UserMapper;
import com.chu7.securtkit.test.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SecurtKitTestApplication.class)
@ActiveProfiles("test")
public class EncryptionIntegrationTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserEncryption() {
        // 创建用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setIdCard("110101199001011234");
        user.setAge(25);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        // 保存用户
        User savedUser = userService.createUser(user.getUsername(), user.getPassword(), user.getPhone(), user.getEmail(), user.getIdCard(), user.getAge());
        assertNotNull(savedUser.getId());
        
        // 从数据库直接查询验证加密
        User dbUser = userMapper.selectById(savedUser.getId());
        assertNotNull(dbUser);
        
        // 验证字段是否被加密
        assertNotEquals("13800138000", dbUser.getPhone(), "手机号应该被加密");
        assertNotEquals("test@example.com", dbUser.getEmail(), "邮箱应该被加密");
        assertNotEquals("110101199001011234", dbUser.getIdCard(), "身份证号应该被加密");
        assertNotEquals("password123", dbUser.getPassword(), "密码应该被加密");
        
        System.out.println("Phone (encrypted): " + dbUser.getPhone());
        System.out.println("Email (encrypted): " + dbUser.getEmail());
        System.out.println("ID Card (encrypted): " + dbUser.getIdCard());
        System.out.println("Password (encrypted): " + dbUser.getPassword());
        
        // 验证查询功能
        User foundUser = userService.findByPhone("13800138000");
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("13800138000", foundUser.getPhone()); // 解密后应该是原文
    }
}