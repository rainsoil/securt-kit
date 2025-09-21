package com.chu7.securtkit.test.simple;

import com.chu7.securtkit.test.SecurtKitTestApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 简单功能测试
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
@SpringBootTest(classes = SecurtKitTestApplication.class)
@ActiveProfiles("test")
public class SimpleFunctionalityTest {

    @Test
    public void testApplicationStartup() {
        log.info("=== 应用启动测试 ===");
        log.info("✅ 应用启动成功");
    }
}