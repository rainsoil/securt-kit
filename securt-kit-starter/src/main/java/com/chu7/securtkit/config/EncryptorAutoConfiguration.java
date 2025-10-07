package com.chu7.securtkit.config;

import com.chu7.securtkit.cache.EncryptorInstanceCache;
import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.config.properties.EncryptorProperties;
import com.chu7.securtkit.config.properties.SecurtKitProperties;
import com.chu7.securtkit.constants.EncryptorPatternTypeConstant;
import com.chu7.securtkit.encryptor.pojo.DefaultPoJoFieldEncryptorPattern;
import com.chu7.securtkit.interceptor.PoJoParamEncryptorInterceptor;
import com.chu7.securtkit.interceptor.PoJoResultDecryptorInterceptor;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 加解密的配置
 *
 * @author chu7
 * @date 2025/5/26 13:47
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({SecurtKitProperties.class})
public class EncryptorAutoConfiguration {

    public EncryptorAutoConfiguration() {
        log.info("【securt-kit】EncryptorAutoConfiguration构造函数被调用");
    }

    /**
     * 缓存当前项目配置的加密算法
     * 可能是db模式的也可能是pojo模式的
     *
     * @author chu7
     * @date 2024/9/19 14:34
     */
    @Bean
    @ConditionalOnProperty(name = "securt-kit.encryptor.enabled", havingValue = "true", matchIfMissing = true)
    public EncryptorInstanceCache encryptorCache(List<FieldEncryptorStrategy<?>> strategies) {
        log.info("【securt-kit】注册EncryptorInstanceCache");
        EncryptorInstanceCache encryptorCache = new EncryptorInstanceCache();
        encryptorCache.init(strategies);
        return encryptorCache;
    }

    // POJO模式拦截器已移至PoJoModeAutoConfiguration中注册

    // 默认的POJO加解密算法已移至PoJoModeAutoConfiguration中注册

    /**
     * 表缓存初始化
     * 
     * @author chu7
     * @date 2025/5/26 14:00
     */
    @Bean
    @ConditionalOnProperty(name = "securt-kit.encryptor.enabled", havingValue = "true", matchIfMissing = true)
    public TableCache tableCache(SecurtKitProperties properties) {
        log.info("【securt-kit】注册EncryptorAutoConfiguration中的TableCache");
        // 通过包扫描初始化表缓存
        TableCache.initByPackages(properties.getScanEntityPackage());
        log.info("【securt-kit】表缓存初始化完成");
        return new TableCache();
    }
}