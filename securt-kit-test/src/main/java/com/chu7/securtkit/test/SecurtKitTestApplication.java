package com.chu7.securtkit.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SecurtKit测试应用主类
 *
 * @author chu7
 * @date 2025/8/15
 */
@SpringBootApplication
@MapperScan("com.chu7.securtkit.test.mapper")
public class SecurtKitTestApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SecurtKitTestApplication.class, args);
    }
} 