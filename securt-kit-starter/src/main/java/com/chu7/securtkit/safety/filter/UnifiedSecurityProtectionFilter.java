package com.chu7.securtkit.safety.filter;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.processor.SecurityProcessor;
import com.chu7.securtkit.safety.processor.XssProcessor;
import com.chu7.securtkit.safety.processor.SqlInjectionProcessor;
import com.chu7.securtkit.safety.processor.SensitiveWordProcessor;
import com.chu7.securtkit.safety.service.WhitelistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 统一安全防护过滤器
 *
 * 作用：作为唯一入口，对请求参数、请求头、JSON Body 进行安全检测与清洗。
 * 设计：
 * 1) 白名单优先：若命中白名单（模块级），直接跳过对应处理器。
 * 2) 处理器迭代：遍历注入的 SecurityProcessor 列表，按需执行 hasAttack/sanitize。
 * 3) 动作控制：当 Processor 配置为 block 时抛出 SafetyBlockException；replace 时进行替换清洗。
 *
 * 注意：
 * - 不做业务逻辑；仅聚焦安全检测与净化。
 * - 对 JSON Body 通过 Wrapper 的缓存机制进行一次性读取与回写，避免多次读取。
 */
@Component
public class UnifiedSecurityProtectionFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(UnifiedSecurityProtectionFilter.class);

    @Autowired
    private SafetyConfig safetyConfig;

    @Autowired(required = false)
    private java.util.List<SecurityProcessor> processors;

    @Autowired
    private WhitelistService whitelistService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("统一安全防护过滤器初始化开始");

        if (!safetyConfig.isEnabled()) {
            log.info("安全防护功能已禁用");
            return;
        }

        log.info("统一安全防护过滤器初始化完成");
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

        log.debug("处理统一安全防护请求: {} {}", method, requestUri);


        // 创建请求包装器
        SafetyHttpServletRequestWrapper wrappedRequest = new SafetyHttpServletRequestWrapper(request);

        // 处理参数、请求头、body（按模块级白名单逐项跳过）
        processParameters(request, wrappedRequest);
        processHeaders(request, wrappedRequest);
        processBody(request, wrappedRequest);
        // 继续过滤器链
        filterChain.doFilter(wrappedRequest, response);

    }

    /**
     * 处理请求参数
     */
    /**
     * 处理并清洗所有请求参数
     */
    private void processParameters(HttpServletRequest rawRequest, SafetyHttpServletRequestWrapper request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();

            for (int i = 0; i < paramValues.length; i++) {
                String originalValue = paramValues[i];
                if (originalValue == null || originalValue.trim().isEmpty()) {
                    continue;
                }

                String filteredValue = applyProcessors(rawRequest, originalValue);
                if (!originalValue.equals(filteredValue)) {
                    paramValues[i] = filteredValue;
                    log.debug("参数 {} 被安全过滤: {} -> {}", paramName, originalValue, filteredValue);
                }
            }
        }
    }

    /**
     * 处理请求头
     */
    /**
     * 处理并清洗关键请求头（User-Agent/Referer 等）
     */
    private void processHeaders(HttpServletRequest rawRequest, SafetyHttpServletRequestWrapper request) {
        // 处理User-Agent等可能包含攻击内容的请求头
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.trim().isEmpty()) {
            String filteredUserAgent = applyProcessors(rawRequest, userAgent);
            if (!userAgent.equals(filteredUserAgent)) {
                request.setHeader("User-Agent", filteredUserAgent);
                log.debug("User-Agent被安全过滤: {} -> {}", userAgent, filteredUserAgent);
            }
        }

        // 处理Referer请求头
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.trim().isEmpty()) {
            String filteredReferer = applyProcessors(rawRequest, referer);
            if (!referer.equals(filteredReferer)) {
                request.setHeader("Referer", filteredReferer);
                log.debug("Referer被安全过滤: {} -> {}", referer, filteredReferer);
            }
        }
    }

    /**
     * 处理JSON body内容
     */
    /**
     * 处理并清洗 JSON Body（仅对 application/json 生效）
     */
    private void processBody(HttpServletRequest rawRequest, SafetyHttpServletRequestWrapper request) {
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try {
                javax.servlet.ServletInputStream in = request.getInputStream();
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                String body = new String(out.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
                String filtered = applyProcessors(rawRequest, body);
                if (!body.equals(filtered)) {
                    // 写回包装器缓存（使用显式方法）
                    request.setCachedBody(filtered.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.error("处理JSON body时发生错误", e);
                throw new SecurityException("JSON body处理失败: " + e.getMessage());
            }
        }
    }

    /**
     * 统一执行安全处理器：若被模块级白名单放行则跳过当前处理器
     */
    private String applyProcessors(HttpServletRequest request, String value) {
        if (processors == null || processors.isEmpty()) return value;
        String result = value;
        for (SecurityProcessor p : processors) {
            if (p.supports(request)) {
                // 针对不同处理器检查各自白名单
                if (p instanceof XssProcessor && whitelistService.isWhitelistedForXss(request)) {
                    continue;
                }
                if (p instanceof SqlInjectionProcessor && whitelistService.isWhitelistedForSql(request)) {
                    continue;
                }
                if (p instanceof SensitiveWordProcessor && whitelistService.isWhitelistedForSensitive(request)) {
                    continue;
                }
                if (p.hasAttack(result)) {
                    result = p.sanitize(result);
                }
            }
        }
        return result;
    }


    @Override
    public void destroy() {
        log.info("统一安全防护过滤器销毁");
    }
}
