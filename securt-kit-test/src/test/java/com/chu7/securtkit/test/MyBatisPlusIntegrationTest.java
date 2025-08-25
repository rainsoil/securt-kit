package com.chu7.securtkit.test;

import com.chu7.securtkit.test.entity.UserEntity;
import com.chu7.securtkit.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import com.chu7.securtkit.test.config.TestConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyBatis Plus集成测试类
 * 测试MyBatis Plus与加密功能的集成
 *
 * @author chu7
 * @date 2025/8/15
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "securt-kit.encrypt.enabled=true",
    "securt-kit.encrypt.patternType=DB",
    "securt-kit.encrypt.key=test-secret-key-32-chars-long"
})
@Import(TestConfig.class)
public class MyBatisPlusIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        // 清理测试数据
        userService.remove(null);
    }
    
    @Test
    void testInsertAndQueryUser() {
        // 创建测试用户
        UserEntity user = new UserEntity();
        user.setUsername("测试用户");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setIdCard("110101199001011234");
        user.setAge("25");
        user.setAddress("北京市朝阳区");
        
        // 保存用户
        boolean saved = userService.createUser(user);
        assertTrue(saved);
        assertNotNull(user.getId());
        
        // 查询用户
        UserEntity queriedUser = userService.getById(user.getId());
        assertNotNull(queriedUser);
        assertEquals("测试用户", queriedUser.getUsername());
        assertEquals("13800138000", queriedUser.getPhone());
        assertEquals("test@example.com", queriedUser.getEmail());
        assertEquals("110101199001011234", queriedUser.getIdCard());
        assertEquals("25", queriedUser.getAge());
        assertEquals("北京市朝阳区", queriedUser.getAddress());
        
        System.out.println("插入和查询用户测试通过");
    }
    
    @Test
    void testQueryByPhone() {
        // 创建测试用户
        UserEntity user = new UserEntity();
        user.setUsername("手机查询测试");
        user.setPhone("13900139000");
        user.setEmail("phone@example.com");
        user.setIdCard("110101199002022345");
        user.setAge("30");
        userService.createUser(user);
        
        // 根据手机号查询
        UserEntity queriedUser = userService.getByPhone("13900139000");
        assertNotNull(queriedUser);
        assertEquals("手机查询测试", queriedUser.getUsername());
        assertEquals("13900139000", queriedUser.getPhone());
        
        System.out.println("根据手机号查询测试通过");
    }
} 