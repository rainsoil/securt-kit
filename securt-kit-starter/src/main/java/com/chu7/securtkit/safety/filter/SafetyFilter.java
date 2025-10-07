package com.chu7.securtkit.safety.filter;

import cn.hutool.core.util.StrUtil;
import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.rule.SecurityRule;
import com.chu7.securtkit.safety.rule.SecurityRuleLoader;
import com.chu7.securtkit.safety.rule.SecurityRuleProcessor;
import com.chu7.securtkit.safety.rule.SecurityRuleProcessor.SecurityRuleResult;
import com.chu7.securtkit.safety.util.SensitiveWordUtil;
import com.chu7.securtkit.safety.util.SqlInjectionRuleLoader;
import com.chu7.securtkit.safety.util.SqlInjectionUtil;
import com.chu7.securtkit.safety.util.XssRuleLoader;
import com.chu7.securtkit.safety.util.XssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 安全防护过滤器
 * 集成XSS防护、SQL注入防护、敏感词过滤
 */
@Component
public class SafetyFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(SafetyFilter.class);
    
    @Autowired
    private SafetyConfig safetyConfig;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SecurityRuleProcessor ruleProcessor = new SecurityRuleProcessor();
    
    private List<SecurityRule> xssRules;
    private List<SecurityRule> sqlInjectionRules;
    private List<SecurityRule> sensitiveWordRules;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("安全防护过滤器初始化完成");
        
        // 加载规则
        loadRules();
        
        // 初始化敏感词库
        if (safetyConfig.getSensitiveWord().isEnabled()) {
            SensitiveWordUtil.initSensitiveWords(
                safetyConfig.getSensitiveWord().getWordFiles(),
                safetyConfig.getSensitiveWord().isEnableBuiltin()
            );
        }
        
        // 初始化SQL注入规则库
        if (safetyConfig.getSqlInjection().isEnabled()) {
            SqlInjectionRuleLoader.initSqlInjectionRules(
                safetyConfig.getSqlInjection().getRuleFiles(),
                safetyConfig.getSqlInjection().isEnableBuiltin()
            );
        }
        
        // 初始化XSS规则库
        if (safetyConfig.getXss().isEnabled()) {
            XssRuleLoader.initXssRules(
                safetyConfig.getXss().getRuleFiles(),
                safetyConfig.getXss().isEnableBuiltin()
            );
        }
    }
    
    /**
     * 加载安全规则
     */
    private void loadRules() {
        try {
            // 加载规则集
            List<com.chu7.securtkit.safety.rule.SecurityRuleSet> ruleSets = SecurityRuleLoader.loadRules(
                safetyConfig.getRules().getRuleFiles(),
                safetyConfig.getRules().isEnableBuiltin()
            );
            
            // 按类型获取规则
            xssRules = SecurityRuleLoader.getRulesByType(ruleSets, "XSS");
            sqlInjectionRules = SecurityRuleLoader.getRulesByType(ruleSets, "SQL_INJECTION");
            sensitiveWordRules = SecurityRuleLoader.getRulesByType(ruleSets, "SENSITIVE_WORD");
            
            log.info("Loaded rules - XSS: {}, SQL Injection: {}, Sensitive Word: {}", 
                xssRules.size(), sqlInjectionRules.size(), sensitiveWordRules.size());
                
        } catch (Exception e) {
            log.error("Failed to load security rules", e);
            // 使用空规则列表
            xssRules = new java.util.ArrayList<>();
            sqlInjectionRules = new java.util.ArrayList<>();
            sensitiveWordRules = new java.util.ArrayList<>();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
                        FilterChain filterChain) throws IOException, ServletException {
        
        if (!safetyConfig.isEnabled()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("处理请求: {} {}", method, requestUri);
        
        try {
            // 创建请求包装器
            SafetyHttpServletRequestWrapper wrappedRequest = new SafetyHttpServletRequestWrapper(request);
            
            // 处理请求参数
            processRequestParameters(wrappedRequest, requestUri);
            
            // 处理请求头
            processRequestHeaders(wrappedRequest, requestUri);
            
            // 继续过滤器链
            filterChain.doFilter(wrappedRequest, response);
            
        } catch (SecurityException e) {
            log.warn("安全防护拦截请求: {} - {}", requestUri, e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"请求包含不安全内容\"}");
            return;
        }
    }
    
    /**
     * 处理请求参数
     */
    private void processRequestParameters(SafetyHttpServletRequestWrapper request, String requestUri) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();
            
            // 检查是否在排除列表中
            if (isExcludedParam(paramName, requestUri)) {
                continue;
            }
            
            for (int i = 0; i < paramValues.length; i++) {
                String originalValue = paramValues[i];
                if (StrUtil.isBlank(originalValue)) {
                    continue;
                }
                
                String processedValue = processParameterValue(originalValue, paramName, requestUri);
                if (!originalValue.equals(processedValue)) {
                    paramValues[i] = processedValue;
                    log.debug("参数 {} 被处理: {} -> {}", paramName, originalValue, processedValue);
                }
            }
        }
    }
    
    /**
     * 处理请求头
     */
    private void processRequestHeaders(SafetyHttpServletRequestWrapper request, String requestUri) {
        // 处理User-Agent等可能包含恶意内容的请求头
        String userAgent = request.getHeader("User-Agent");
        if (StrUtil.isNotBlank(userAgent)) {
            String processedUserAgent = processParameterValue(userAgent, "User-Agent", requestUri);
            if (!userAgent.equals(processedUserAgent)) {
                request.setHeader("User-Agent", processedUserAgent);
                log.debug("User-Agent被处理: {} -> {}", userAgent, processedUserAgent);
            }
        }
    }
    
    /**
     * 处理参数值
     */
    private String processParameterValue(String value, String paramName, String requestUri) {
        String result = value;
        
        // 使用规则系统处理
        if (safetyConfig.getRules().isEnabled()) {
            // XSS防护规则
            if (xssRules != null && !xssRules.isEmpty()) {
                SecurityRuleResult xssResult = ruleProcessor.processXssRules(result, xssRules, requestUri, paramName);
                if (xssResult.isMatched()) {
                    result = xssResult.getResult();
                    log.debug("XSS rule applied: {}", xssResult.getMessage());
                }
            }
            
            // SQL注入防护规则
            if (sqlInjectionRules != null && !sqlInjectionRules.isEmpty()) {
                SecurityRuleResult sqlResult = ruleProcessor.processSqlInjectionRules(result, sqlInjectionRules, requestUri, paramName);
                if (sqlResult.isMatched()) {
                    result = sqlResult.getResult();
                    log.debug("SQL injection rule applied: {}", sqlResult.getMessage());
                }
            }
            
            // 敏感词过滤规则
            if (sensitiveWordRules != null && !sensitiveWordRules.isEmpty()) {
                SecurityRuleResult sensitiveResult = ruleProcessor.processSensitiveWordRules(result, sensitiveWordRules, requestUri, paramName);
                if (sensitiveResult.isMatched()) {
                    result = sensitiveResult.getResult();
                    log.debug("Sensitive word rule applied: {}", sensitiveResult.getMessage());
                }
            }
        } else {
            // 兼容旧配置
            result = processWithLegacyConfig(result, paramName, requestUri);
        }
        
        return result;
    }
    
    /**
     * 使用旧配置处理（兼容性）
     */
    private String processWithLegacyConfig(String value, String paramName, String requestUri) {
        String result = value;
        
        // XSS防护
        if (safetyConfig.getXss().isEnabled() && !isExcludedFromXss(requestUri, paramName)) {
            if (XssUtil.containsXss(result)) {
                log.warn("检测到XSS攻击: 参数={}, 值={}", paramName, result);
                throw new SecurityException("检测到XSS攻击");
            }
            result = XssUtil.filterXss(result);
        }
        
        // SQL注入防护
        if (safetyConfig.getSqlInjection().isEnabled() && !isExcludedFromSqlInjection(requestUri, paramName)) {
            if (SqlInjectionUtil.containsSqlInjection(result)) {
                log.warn("检测到SQL注入攻击: 参数={}, 值={}", paramName, result);
                throw new SecurityException("检测到SQL注入攻击");
            }
            result = SqlInjectionUtil.filterSqlInjection(result);
        }
        
        // 敏感词过滤
        if (safetyConfig.getSensitiveWord().isEnabled() && !isExcludedFromSensitiveWord(requestUri, paramName)) {
            if (SensitiveWordUtil.containsSensitiveWords(result)) {
                log.warn("检测到敏感词: 参数={}, 值={}", paramName, result);
                result = SensitiveWordUtil.filterSensitiveWords(result, 
                    safetyConfig.getSensitiveWord().getReplaceChar());
            }
        }
        
        return result;
    }
    
    /**
     * 检查是否在排除列表中
     */
    private boolean isExcludedParam(String paramName, String requestUri) {
        return isExcludedFromXss(requestUri, paramName) && 
               isExcludedFromSqlInjection(requestUri, paramName) && 
               isExcludedFromSensitiveWord(requestUri, paramName);
    }
    
    /**
     * 检查是否从XSS防护中排除
     */
    private boolean isExcludedFromXss(String requestUri, String paramName) {
        // 检查URL模式（白名单）
        for (String pattern : safetyConfig.getXss().getExcludePatterns()) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否从SQL注入防护中排除
     */
    private boolean isExcludedFromSqlInjection(String requestUri, String paramName) {
        // 检查URL模式（白名单）
        for (String pattern : safetyConfig.getSqlInjection().getExcludePatterns()) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否从敏感词过滤中排除
     */
    private boolean isExcludedFromSensitiveWord(String requestUri, String paramName) {
        // 检查URL模式（白名单）
        for (String pattern : safetyConfig.getSensitiveWord().getExcludePatterns()) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void destroy() {
        log.info("安全防护过滤器销毁");
    }
}
