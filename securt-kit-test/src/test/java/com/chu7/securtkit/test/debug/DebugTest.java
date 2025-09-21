package com.chu7.securtkit.test.debug;

import com.chu7.securtkit.test.SecurtKitTestApplication;
import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 调试图
 */
@Slf4j
@SpringBootTest(classes = SecurtKitTestApplication.class)
@ActiveProfiles("test")
public class DebugTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsertUser() {
        log.info("=== 测试插入用户 ===");
        
        // 创建测试用户
        User testUser = new User();
        testUser.setUsername("debuguser");
        testUser.setPassword("123456");           // 会被MD5加密
        testUser.setPhone("13800138000");         // 会被DES加密
        testUser.setEmail("debug@example.com");    // 会被AES加密
        testUser.setIdCard("110101199001011234"); // 会被Base64编码
        testUser.setAge(25);
        testUser.setCreateTime(new Date());
        testUser.setUpdateTime(new Date());
        
        log.info("插入前用户数据：{}", testUser);
        
        // 插入用户
        int result = userMapper.insertUser(testUser);
        
        log.info("插入结果：{}", result);
        log.info("插入后用户数据：{}", testUser);
        log.info("用户ID：{}", testUser.getId());
        
        // 查询用户
        User foundUser = userMapper.selectById(testUser.getId());
        log.info("查询到的用户：{}", foundUser);
        
        // 验证
        assertNotNull(foundUser);
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        assertEquals(testUser.getPhone(), foundUser.getPhone());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        assertEquals(testUser.getIdCard(), foundUser.getIdCard());
    }
}