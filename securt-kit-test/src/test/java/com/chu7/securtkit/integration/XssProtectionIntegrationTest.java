package com.chu7.securtkit.integration;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.filter.XssProtectionFilter;
import com.chu7.securtkit.safety.keywords.XssKeywordsManager;
import com.chu7.securtkit.safety.util.XssProtectionUtil;
import com.chu7.securtkit.safety.whitelist.IpWhitelistManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * XSS防护集成测试
 */
@DisplayName("XSS防护集成测试")
class XssProtectionIntegrationTest {

    private XssProtectionFilter xssProtectionFilter;
    private SafetyConfig safetyConfig;
    private FilterChain mockFilterChain;

    @BeforeEach
    void setUp() throws ServletException {
        // 创建配置
        safetyConfig = new SafetyConfig();
        safetyConfig.setEnabled(true);
        
        // 配置XSS防护
        safetyConfig.getXss().setEnabled(true);
        safetyConfig.getXss().getIpWhitelist().setEnabled(true);
        safetyConfig.getXss().getIpWhitelist().setEnableBuiltin(true);
        safetyConfig.getXss().getIpWhitelist().getAllowedIps().addAll(Arrays.asList(
            "192.168.1.100",
            "10.0.0.0/8"
        ));
        
        safetyConfig.getXss().getKeywords().setEnabled(true);
        safetyConfig.getXss().getKeywords().setEnableBuiltin(true);
        safetyConfig.getXss().getKeywords().setDetectionMode("normal");
        
        // 创建过滤器
        xssProtectionFilter = new XssProtectionFilter();
        xssProtectionFilter.setSafetyConfig(safetyConfig);
        
        // 初始化过滤器
        MockFilterConfig filterConfig = new MockFilterConfig();
        xssProtectionFilter.init(filterConfig);
        
        // 创建模拟的FilterChain
        mockFilterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("测试IP白名单集成")
    void testIpWhitelistIntegration() throws IOException, ServletException {
        // 创建来自白名单IP的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("192.168.1.100");
        request.addParameter("content", "<script>alert('xss')</script>");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证FilterChain被调用（IP在白名单中，直接通过）
        verify(mockFilterChain).doFilter(any(), any());
        
        // 验证响应状态
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("测试XSS关键字检测集成")
    void testXssKeywordsDetectionIntegration() throws IOException, ServletException {
        // 创建来自非白名单IP的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("8.8.8.8");
        request.addParameter("content", "<script>alert('xss')</script>");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证请求被拦截
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("XSS攻击被拦截"));
        
        // 验证FilterChain未被调用
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("测试XSS关键字过滤集成")
    void testXssKeywordsFilteringIntegration() throws IOException, ServletException {
        // 创建包含XSS关键字的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("8.8.8.8");
        request.addParameter("content", "normal content with script tag");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证FilterChain被调用
        verify(mockFilterChain).doFilter(any(), any());
        
        // 验证响应状态
        assertEquals(200, response.getStatus());
        
        // 验证内容被过滤
        String filteredContent = request.getParameter("content");
        assertTrue(filteredContent.contains("*"));
        assertFalse(filteredContent.contains("script"));
    }

    @Test
    @DisplayName("测试URL排除列表集成")
    void testUrlExclusionIntegration() throws IOException, ServletException {
        // 配置URL排除列表
        safetyConfig.getXss().getExcludePatterns().add("/api/public/**");
        
        // 创建来自排除URL的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/public/test");
        request.setRemoteAddr("8.8.8.8");
        request.addParameter("content", "<script>alert('xss')</script>");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证FilterChain被调用（URL在排除列表中）
        verify(mockFilterChain).doFilter(any(), any());
        
        // 验证响应状态
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("测试User-Agent检测集成")
    void testUserAgentDetectionIntegration() throws IOException, ServletException {
        // 创建包含恶意User-Agent的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("8.8.8.8");
        request.addHeader("User-Agent", "<script>alert('xss')</script>");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证请求被拦截
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("XSS攻击被拦截"));
        
        // 验证FilterChain未被调用
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("测试Referer检测集成")
    void testRefererDetectionIntegration() throws IOException, ServletException {
        // 创建包含恶意Referer的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("8.8.8.8");
        request.addHeader("Referer", "javascript:alert('xss')");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证请求被拦截
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("XSS攻击被拦截"));
        
        // 验证FilterChain未被调用
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("测试XSS防护禁用集成")
    void testXssProtectionDisabledIntegration() throws IOException, ServletException {
        // 禁用XSS防护
        safetyConfig.getXss().setEnabled(false);
        
        // 创建包含XSS攻击的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("8.8.8.8");
        request.addParameter("content", "<script>alert('xss')</script>");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证FilterChain被调用（XSS防护已禁用）
        verify(mockFilterChain).doFilter(any(), any());
        
        // 验证响应状态
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("测试混合攻击检测集成")
    void testMixedAttackDetectionIntegration() throws IOException, ServletException {
        // 创建包含多种XSS攻击的请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setRemoteAddr("8.8.8.8");
        request.addParameter("content", "<script>alert('xss')</script>");
        request.addHeader("User-Agent", "javascript:alert('xss')");
        request.addHeader("Referer", "onclick=alert('xss')");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行过滤器
        xssProtectionFilter.doFilter(request, response, mockFilterChain);
        
        // 验证请求被拦截
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("XSS攻击被拦截"));
        
        // 验证FilterChain未被调用
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    // 添加setter方法以便测试
    private static class TestableXssProtectionFilter extends XssProtectionFilter {
        public void setSafetyConfig(SafetyConfig config) {
            this.safetyConfig = config;
        }
    }
}
