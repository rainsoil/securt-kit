package com.chu7.securtkit.test;

import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 混合配置加密演示
 * 展示注解配置 + 配置文件配置的混合使用
 * 
 * @author chu7
 * @date 2025/9/22 23:10
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.chu7.securtkit")
@Slf4j
public class MixedConfigEncryptionDemo implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    public static void main(String[] args) {
        SpringApplication.run(MixedConfigEncryptionDemo.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 混合配置加密演示开始 ===");
        
        try {
            // 清理数据
            userMapper.delete(null);
            log.info("清理测试数据完成");
            
            // 1. 测试注解配置的字段加密
            testAnnotationBasedEncryption();
            
            // 2. 测试配置文件覆盖的字段加密
            testConfigFileOverrideEncryption();
            
            // 3. 测试配置文件新增的字段加密
            testConfigFileNewFieldEncryption();
            
            // 4. 测试查询解密
            testQueryDecryption();
            
            log.info("=== 混合配置加密演示完成 ===");
            
        } catch (Exception e) {
            log.error("混合配置加密演示失败", e);
        }
    }

    /**
     * 测试注解配置的字段加密
     */
    private void testAnnotationBasedEncryption() {
        log.info("--- 测试注解配置的字段加密 ---");
        
        User user = new User();
        user.setUsername("张三");
        user.setPassword("123456");  // 注解配置：MD5加密
        user.setPhone("13800138000");  // 注解配置：DES加密
        user.setEmail("zhangsan@example.com");  // 注解配置：AES加密
        user.setIdCard("110101199001011234");  // 注解配置：Base64编码
        user.setAge(25);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        log.info("插入前用户数据: {}", user);
        userMapper.insert(user);
        log.info("用户插入成功，ID: {}", user.getId());
    }

    /**
     * 测试配置文件覆盖的字段加密
     */
    private void testConfigFileOverrideEncryption() {
        log.info("--- 测试配置文件覆盖的字段加密 ---");
        
        User user = new User();
        user.setUsername("李四");
        user.setPassword("654321");  // 配置文件覆盖：改为AES加密
        user.setPhone("13900139000");  // 注解配置：DES加密
        user.setEmail("lisi@example.com");  // 注解配置：AES加密
        user.setIdCard("110101199002021234");  // 注解配置：Base64编码
        user.setAge(30);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        log.info("插入前用户数据: {}", user);
        userMapper.insert(user);
        log.info("用户插入成功，ID: {}", user.getId());
    }

    /**
     * 测试配置文件新增的字段加密
     */
    private void testConfigFileNewFieldEncryption() {
        log.info("--- 测试配置文件新增的字段加密 ---");
        
        User user = new User();
        user.setUsername("王五");  // 配置文件新增：Base64编码
        user.setPassword("111111");  // 配置文件覆盖：AES加密
        user.setPhone("13700137000");  // 注解配置：DES加密
        user.setEmail("wangwu@example.com");  // 注解配置：AES加密
        user.setIdCard("110101199003031234");  // 注解配置：Base64编码
        user.setAge(35);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        log.info("插入前用户数据: {}", user);
        userMapper.insert(user);
        log.info("用户插入成功，ID: {}", user.getId());
    }

    /**
     * 测试查询解密
     */
    private void testQueryDecryption() {
        log.info("--- 测试查询解密 ---");
        
        // 查询所有用户
        List<User> users = userMapper.selectList(null);
        log.info("查询到 {} 个用户", users.size());
        
        for (User user : users) {
            log.info("用户ID: {}, 用户名: {}, 密码: {}, 手机: {}, 邮箱: {}, 身份证: {}", 
                    user.getId(), user.getUsername(), user.getPassword(), 
                    user.getPhone(), user.getEmail(), user.getIdCard());
        }
        
        // 测试复杂查询
        testComplexQueries();
    }

    /**
     * 测试复杂查询
     */
    private void testComplexQueries() {
        log.info("--- 测试复杂查询 ---");
        
        // 测试带别名的查询
        List<Map<String, Object>> usersWithAlias = userMapper.selectUsersWithAlias();
        log.info("带别名的查询结果: {}", usersWithAlias);
        
        // 测试统计查询
        List<Map<String, Object>> statistics = userMapper.selectUserStatistics();
        log.info("统计查询结果: {}", statistics);
    }
}
