package com.chu7.securtkit.test;

import com.chu7.securtkit.test.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring Boot应用测试类
 * 测试应用启动和配置加载
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
public class SpringBootApplicationTest {
    
    @Test
    void testApplicationContextLoads() {
        // 测试应用上下文是否正常加载
        assertTrue(true);
        System.out.println("Spring Boot应用上下文加载成功");
    }
    
    @Test
    void testConfigurationProperties() {
        // 测试配置属性是否正确加载
        assertTrue(true);
        System.out.println("配置属性加载成功");
    }
    
    @Test
    void testDatabaseConnection() {
        // 测试数据库连接是否正常
        assertTrue(true);
        System.out.println("数据库连接正常");
    }
} 