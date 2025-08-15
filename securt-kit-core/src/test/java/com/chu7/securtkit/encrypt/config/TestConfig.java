package com.chu7.securtkit.encrypt.config;

import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 测试配置类
 *
 * @author chu7
 * @date 2025/8/15
 */
@TestConfiguration
public class TestConfig {
    
    /**
     * 配置测试用的表字段缓存
     */
    @Bean
    @Primary
    public TableFieldCache testTableFieldCache() {
        TableFieldCache cache = new TableFieldCache();
        
        // 配置用户表的加密字段
        Set<String> userEncryptFields = new HashSet<>(Arrays.asList("phone", "email", "id_card"));
        cache.addTableEncryptFields("user", userEncryptFields);
        cache.addClassNameToTableName("com.chu7.securtkit.encrypt.entity.UserEntity", "user");
        
        return cache;
    }
}
