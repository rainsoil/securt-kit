package com.chu7.securtkit.sql.config;

import com.chu7.securtkit.sql.FieldProcessor;
import com.chu7.securtkit.sql.SqlAnalyzer;
import com.chu7.securtkit.sql.UnifiedFieldInterceptor;
import com.chu7.securtkit.sql.impl.DefaultFieldProcessor;
import com.chu7.securtkit.sql.impl.DefaultSqlAnalyzer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SQL解析器自动配置类
 *
 * @author security-kit
 */
@Configuration
public class SqlAnalyzerAutoConfiguration {
    
    /**
     * 配置默认的SQL解析器
     */
    @Bean
    @ConditionalOnMissingBean(SqlAnalyzer.class)
    public SqlAnalyzer sqlAnalyzer() {
        return new DefaultSqlAnalyzer();
    }
    
    /**
     * 配置默认的字段处理器
     */
    @Bean
    @ConditionalOnMissingBean(FieldProcessor.class)
    public FieldProcessor fieldProcessor() {
        return new DefaultFieldProcessor();
    }
    
    /**
     * 配置统一字段拦截器
     */
    @Bean
    @ConditionalOnMissingBean(UnifiedFieldInterceptor.class)
    public UnifiedFieldInterceptor unifiedFieldInterceptor() {
        return new UnifiedFieldInterceptor();
    }
} 