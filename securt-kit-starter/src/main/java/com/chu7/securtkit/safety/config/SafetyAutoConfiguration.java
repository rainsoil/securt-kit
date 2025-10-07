package com.chu7.securtkit.safety.config;

import com.chu7.securtkit.safety.filter.SafetyFilter;
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
    
    /**
     * 注册安全防护过滤器
     */
    @Bean
    public FilterRegistrationBean<SafetyFilter> safetyFilterRegistration(SafetyFilter safetyFilter) {
        FilterRegistrationBean<SafetyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(safetyFilter);
        registration.addUrlPatterns("/*");
        registration.setName("safetyFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
    
    /**
     * 安全防护过滤器Bean
     */
    @Bean
    public SafetyFilter safetyFilter() {
        return new SafetyFilter();
    }
}
