package com.chu7.securtkit.config;

import com.chu7.securtkit.cache.EncryptorInstanceCache;
import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.config.properties.EncryptorProperties;
import com.chu7.securtkit.encryptor.pojo.*;
import com.chu7.securtkit.interceptor.PoJoParamEncryptorInterceptor;
import com.chu7.securtkit.interceptor.PoJoResultDecryptorInterceptor;
import com.chu7.securtkit.config.properties.SecurtKitProperties;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO模式自动配置类
 * 使用Java加解密算法，在MyBatis拦截器层面对入参和结果进行加解密
 *
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Configuration
@EnableConfigurationProperties({SecurtKitProperties.class, EncryptorProperties.class})
@ConditionalOnProperty(prefix = "securt-kit.encryptor", name = "pattern-type", havingValue = "pojo")
@Slf4j
public class PoJoModeAutoConfiguration {

    @Autowired
    private SecurtKitProperties securtKitProperties;

    @Autowired
    private EncryptorProperties encryptorProperties;

    // 移除对fieldEncryptorStrategies的依赖，避免循环依赖

    public PoJoModeAutoConfiguration() {
        log.info("【securt-kit】PoJoModeAutoConfiguration构造函数被调用");
        System.out.println("【securt-kit】PoJoModeAutoConfiguration构造函数被调用");
    }

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void initCache() {
        log.info("【securt-kit】开始初始化POJO模式缓存");
        System.out.println("【securt-kit】开始初始化POJO模式缓存");
        
        // 1. 初始化加密策略缓存 - 直接创建策略实例避免循环依赖
        List<FieldEncryptorStrategy> strategies = createEncryptorStrategies();
        if (strategies != null && !strategies.isEmpty()) {
            EncryptorInstanceCache encryptorInstanceCache = new EncryptorInstanceCache();
            encryptorInstanceCache.init(strategies);
            log.info("【securt-kit】初始化加密策略缓存完成，策略数量: {}", strategies.size());
        } else {
            log.warn("【securt-kit】未找到加密策略或策略为空");
        }
        
        // 2. 初始化表字段缓存 - 修复属性名
        log.info("【securt-kit】准备初始化表字段缓存");
        if (securtKitProperties != null) {
            log.info("【securt-kit】securtKitProperties不为空");
            List<String> scanPackages = securtKitProperties.getScanEntityPackage();
            log.info("【securt-kit】scanPackages: {}", scanPackages);
            if (scanPackages != null && !scanPackages.isEmpty()) {
                TableCache.initByPackages(scanPackages);
                log.info("【securt-kit】初始化表字段缓存完成，扫描包: {}", scanPackages);
            } else {
                log.warn("【securt-kit】未配置扫描包路径，跳过表字段缓存初始化");
            }
        } else {
            log.warn("【securt-kit】securtKitProperties为空");
        }
        
        log.info("【securt-kit】POJO模式缓存初始化完成");
    }

    /**
     * 创建加密策略实例列表，避免循环依赖
     */
    private List<FieldEncryptorStrategy> createEncryptorStrategies() {
        List<FieldEncryptorStrategy> strategies = new ArrayList<>();
        
        // 直接创建策略实例，避免循环依赖
        strategies.add(new DefaultPoJoFieldEncryptorPattern(encryptorProperties));
        strategies.add(new AesPoJoFieldEncryptorStrategy(encryptorProperties));
        strategies.add(new Base64PoJoFieldEncryptorStrategy());
        strategies.add(new Md5PoJoFieldEncryptorStrategy());
        
        log.info("【securt-kit】创建加密策略实例完成，策略数量: {}", strategies.size());
        return strategies;
    }

    /**
     * 注册参数加密拦截器
     */
    @Bean
    @ConditionalOnProperty(prefix = "securt-kit", name = "param-encrypt-enabled", havingValue = "true", matchIfMissing = true)
    public PoJoParamEncryptorInterceptor poJoParamEncryptorInterceptor() {
        log.info("【securt-kit】注册POJO参数加密拦截器");
        return new PoJoParamEncryptorInterceptor();
    }

    /**
     * 注册结果解密拦截器
     */
    @Bean
    @ConditionalOnProperty(prefix = "securt-kit", name = "result-decrypt-enabled", havingValue = "true", matchIfMissing = true)
    public PoJoResultDecryptorInterceptor poJoResultDecryptorInterceptor() {
        log.info("【securt-kit】注册POJO结果解密拦截器");
        return new PoJoResultDecryptorInterceptor();
    }

    /**
     * 默认DES加密策略
     */
    @Bean
    @ConditionalOnProperty(prefix = "securt-kit.encryptor", name = "algorithm", havingValue = "DES", matchIfMissing = true)
    public DefaultPoJoFieldEncryptorPattern defaultPoJoFieldEncryptorPattern() {
        log.info("【securt-kit】注册默认DES加密策略");
        return new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
    }

    /**
     * AES加密策略
     */
    @Bean
    @ConditionalOnProperty(prefix = "securt-kit.encryptor", name = "algorithm", havingValue = "AES")
    public AesPoJoFieldEncryptorStrategy aesPoJoFieldEncryptorStrategy() {
        log.info("【securt-kit】注册AES加密策略");
        return new AesPoJoFieldEncryptorStrategy(encryptorProperties);
    }

    /**
     * Base64编码策略
     */
    @Bean
    public Base64PoJoFieldEncryptorStrategy base64PoJoFieldEncryptorStrategy() {
        log.info("【securt-kit】注册Base64编码策略");
        return new Base64PoJoFieldEncryptorStrategy();
    }

    /**
     * MD5哈希策略
     */
    @Bean
    public Md5PoJoFieldEncryptorStrategy md5PoJoFieldEncryptorStrategy() {
        log.info("【securt-kit】注册MD5哈希策略");
        return new Md5PoJoFieldEncryptorStrategy();
    }
}