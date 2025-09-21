package com.chu7.securtkit.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SecurtKit 测试应用启动类
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@SpringBootApplication(scanBasePackages = {"com.chu7.securtkit"})
@MapperScan("com.chu7.securtkit.test.mapper")
public class SecurtKitTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurtKitTestApplication.class, args);
    }
}