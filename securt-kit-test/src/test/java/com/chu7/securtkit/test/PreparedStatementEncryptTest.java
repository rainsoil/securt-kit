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

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预处理SQL加密测试类
 * 专门测试预处理SQL的加密解密功能
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
public class PreparedStatementEncryptTest {
    
    @Autowired
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService.remove(null);
    }
    
    @Test
    void testInsertWithPreparedStatement() {
        UserEntity user = new UserEntity();
        user.setUsername("预处理SQL测试用户");
        user.setPhone("13900139000");
        user.setEmail("prepared@example.com");
        user.setIdCard("110101199005055678");
        user.setAge("26");
        
        boolean saved = userService.createUser(user);
        assertTrue(saved);
        assertNotNull(user.getId());
        
        System.out.println("预处理SQL插入测试通过");
    }
    
    @Test
    void testQueryWithPreparedStatement() {
        UserEntity user = new UserEntity();
        user.setUsername("查询测试用户");
        user.setPhone("13900139001");
        user.setEmail("query@example.com");
        user.setIdCard("110101199006066789");
        user.setAge("27");
        userService.createUser(user);
        
        UserEntity queriedUser = userService.getByPhone("13900139001");
        assertNotNull(queriedUser);
        assertEquals("查询测试用户", queriedUser.getUsername());
        assertEquals("13900139001", queriedUser.getPhone());
        
        System.out.println("预处理SQL查询测试通过");
    }
} 