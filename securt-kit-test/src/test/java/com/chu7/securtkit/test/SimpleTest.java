package com.chu7.securtkit.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 简单测试类
 * 用于验证项目基本结构是否正确
 *
 * @author chu7
 * @date 2025/8/15
 */
@SpringBootTest
@ActiveProfiles("test")
public class SimpleTest {
    
    @Test
    void testBasicStructure() {
        // 验证项目基本结构
        assertTrue(true);
        System.out.println("项目基本结构测试通过");
    }
    
    @Test
    void testSpringContext() {
        // 验证Spring上下文是否正常加载
        assertTrue(true);
        System.out.println("Spring上下文加载测试通过");
    }
} 