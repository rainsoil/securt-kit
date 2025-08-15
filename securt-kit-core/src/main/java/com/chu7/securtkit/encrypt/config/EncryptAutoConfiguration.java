package com.chu7.securtkit.encrypt.config;

import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.interceptor.DbFieldEncryptorInterceptor;
import com.chu7.securtkit.encrypt.interceptor.PojoParamEncryptorInterceptor;
import com.chu7.securtkit.encrypt.interceptor.PojoResultDecryptorInterceptor;
import com.chu7.securtkit.encrypt.strategy.AesEncryptStrategy;
import com.chu7.securtkit.encrypt.strategy.EncryptStrategy;
import com.chu7.securtkit.encrypt.visitor.DbEncryptStatementVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 加密自动配置类
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(EncryptProperties.class)
public class EncryptAutoConfiguration {
    
    /**
     * 注册表字段缓存
     */
    @Bean
    @ConditionalOnMissingBean
    public TableFieldCache tableFieldCache() {
        return new TableFieldCache();
    }
    
    /**
     * 注册数据库加解密语句访问者
     */
    @Bean
    @ConditionalOnMissingBean
    public DbEncryptStatementVisitor dbEncryptStatementVisitor() {
        return new DbEncryptStatementVisitor();
    }
    
    /**
     * 注册AES加密策略
     */
    @Bean
    @ConditionalOnMissingBean
    public AesEncryptStrategy aesEncryptStrategy() {
        return new AesEncryptStrategy();
    }
    
    /**
     * 注册数据库模式加解密拦截器
     */
    @Bean
    @ConditionalOnProperty(name = "securt-kit.encrypt.patternType", havingValue = "DB")
    public DbFieldEncryptorInterceptor dbFieldEncryptorInterceptor() {
        log.info("启用数据库模式字段加密拦截器");
        return new DbFieldEncryptorInterceptor();
    }
    
    /**
     * 注册POJO模式参数加密拦截器
     */
    @Bean
    @ConditionalOnProperty(name = "securt-kit.encrypt.patternType", havingValue = "POJO")
    public PojoParamEncryptorInterceptor pojoParamEncryptorInterceptor() {
        log.info("启用POJO模式参数加密拦截器");
        return new PojoParamEncryptorInterceptor();
    }
    
    /**
     * 注册POJO模式结果解密拦截器
     */
    @Bean
    @ConditionalOnProperty(name = "securt-kit.encrypt.patternType", havingValue = "POJO")
    public PojoResultDecryptorInterceptor pojoResultDecryptorInterceptor() {
        log.info("启用POJO模式结果解密拦截器");
        return new PojoResultDecryptorInterceptor();
    }
}
