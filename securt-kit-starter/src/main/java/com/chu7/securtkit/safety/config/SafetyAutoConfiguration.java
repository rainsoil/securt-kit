package com.chu7.securtkit.safety.config;

import com.chu7.securtkit.safety.filter.UnifiedSecurityProtectionFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 安全防护自动配置类
 */
@Configuration
@EnableConfigurationProperties(SafetyConfig.class)
@ConditionalOnProperty(prefix = "securt-kit.safety", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SafetyAutoConfiguration {
    
    // 已统一为统一安全防护过滤器，移除单独的XSS/SQL注入/敏感词过滤器注册与Bean
    
    /**
     * 注册统一安全防护过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "securt-kit.safety", name = "unified-filter", havingValue = "true", matchIfMissing = false)
    public FilterRegistrationBean<UnifiedSecurityProtectionFilter> unifiedSecurityProtectionFilterRegistration(UnifiedSecurityProtectionFilter unifiedSecurityProtectionFilter) {
        FilterRegistrationBean<UnifiedSecurityProtectionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(unifiedSecurityProtectionFilter);
        registration.addUrlPatterns("/*");
        registration.setName("unifiedSecurityProtectionFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 最高优先级
        return registration;
    }
    
    /**
     * 统一安全防护过滤器Bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "securt-kit.safety", name = "unified-filter", havingValue = "true", matchIfMissing = false)
    public UnifiedSecurityProtectionFilter unifiedSecurityProtectionFilter() {
        return new UnifiedSecurityProtectionFilter();
    }
}
