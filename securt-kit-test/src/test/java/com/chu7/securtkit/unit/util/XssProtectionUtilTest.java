package com.chu7.securtkit.unit.util;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.util.XssProtectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XSS防护工具类测试
 */
@DisplayName("XSS防护工具类测试")
class XssProtectionUtilTest {

    private SafetyConfig.XssConfig config;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        config = new SafetyConfig.XssConfig();
        config.setEnabled(true);
        
        // URL白名单配置
        config.getUrlWhitelist().setEnabled(true);
        config.getUrlWhitelist().getAllowedUrls().add("/api/public/**");
        
        // IP白名单配置
        config.getIpWhitelist().setEnabled(true);
        config.getIpWhitelist().getAllowedIps().add("192.168.1.100");
        
        // XSS关键字配置已移除，直接通过读取配置文件获取
        
        request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
    }

    @Test
    @DisplayName("测试是否需要XSS防护")
    void testNeedsXssProtection() {
        // 测试URL在白名单中
        request.setRequestURI("/api/public/health");
        assertFalse(XssProtectionUtil.needsXssProtection(request, config));
        
        // 测试IP在白名单中
        request.setRequestURI("/api/private/data");
        request.setRemoteAddr("192.168.1.100");
        assertFalse(XssProtectionUtil.needsXssProtection(request, config));
        
        // 测试IP不在白名单中
        request.setRemoteAddr("8.8.8.8");
        assertTrue(XssProtectionUtil.needsXssProtection(request, config));
        
        // 测试IP白名单禁用
        config.getIpWhitelist().setEnabled(false);
        assertTrue(XssProtectionUtil.needsXssProtection(request, config));
        
        // 测试URL白名单禁用
        config.getIpWhitelist().setEnabled(true);
        config.getUrlWhitelist().setEnabled(false);
        request.setRequestURI("/api/public/health");
        assertTrue(XssProtectionUtil.needsXssProtection(request, config));
    }

    @Test
    @DisplayName("测试检查XSS攻击")
    void testContainsXssAttack() {
        // 测试包含XSS攻击
        assertTrue(XssProtectionUtil.containsXssAttack("<script>alert('xss')</script>"));
        assertTrue(XssProtectionUtil.containsXssAttack("javascript:alert('xss')"));
        assertTrue(XssProtectionUtil.containsXssAttack("onclick=alert('xss')"));
        
        // 测试不包含XSS攻击
        assertFalse(XssProtectionUtil.containsXssAttack("normal content"));
        assertFalse(XssProtectionUtil.containsXssAttack("safe text"));
        
        // 测试空值
        assertFalse(XssProtectionUtil.containsXssAttack(null));
        assertFalse(XssProtectionUtil.containsXssAttack(""));
    }

    @Test
    @DisplayName("测试过滤XSS攻击")
    void testFilterXssAttack() {
        // 测试过滤XSS攻击
        String content = "<script>alert('xss')</script>";
        String filteredContent = XssProtectionUtil.filterXssAttack(content);
        
        assertNotNull(filteredContent);
        assertTrue(filteredContent.contains("*"));
        assertFalse(filteredContent.contains("script"));
        
        // 测试正常内容不过滤
        String normalContent = "normal content";
        String filteredNormalContent = XssProtectionUtil.filterXssAttack(normalContent);
        assertEquals(normalContent, filteredNormalContent);
    }

    @Test
    @DisplayName("测试获取匹配的XSS关键字")
    void testGetMatchedXssKeywords() {
        // 测试获取匹配的关键字
        String content = "<script>alert('xss')</script>";
        List<String> matchedKeywords = XssProtectionUtil.getMatchedXssKeywords(content);
        
        assertNotNull(matchedKeywords);
        assertTrue(matchedKeywords.size() > 0);
        assertTrue(matchedKeywords.contains("script"));
        assertTrue(matchedKeywords.contains("alert"));
        
        // 测试正常内容无匹配关键字
        String normalContent = "normal content";
        List<String> normalMatchedKeywords = XssProtectionUtil.getMatchedXssKeywords(normalContent);
        assertTrue(normalMatchedKeywords.isEmpty());
    }

    @Test
    @DisplayName("测试获取客户端IP")
    void testGetClientIp() {
        // 测试直接IP
        request.setRemoteAddr("192.168.1.100");
        assertEquals("192.168.1.100", XssProtectionUtil.getClientIp(request));
        
        // 测试X-Forwarded-For头
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "192.168.1.100");
        assertEquals("192.168.1.100", XssProtectionUtil.getClientIp(request));
        
        // 测试多个IP的情况
        request.addHeader("X-Forwarded-For", "192.168.1.100, 10.0.0.1");
        assertEquals("192.168.1.100", XssProtectionUtil.getClientIp(request));
    }

    @Test
    @DisplayName("测试获取XSS防护统计信息")
    void testGetXssProtectionStats() {
        // 获取统计信息
        Map<String, Object> stats = XssProtectionUtil.getXssProtectionStats(config);
        
        // 验证统计信息
        assertNotNull(stats);
        assertTrue(stats.containsKey("enabled"));
        assertTrue(stats.containsKey("ipWhitelistEnabled"));
        assertTrue(stats.containsKey("keywordsEnabled"));
        assertTrue(stats.containsKey("detectionMode"));
        
        // 验证配置信息
        assertTrue((Boolean) stats.get("enabled"));
        assertTrue((Boolean) stats.get("ipWhitelistEnabled"));
        assertTrue((Boolean) stats.get("keywordsEnabled"));
        assertEquals("normal", stats.get("detectionMode"));
    }

    @Test
    @DisplayName("测试重新加载XSS防护配置")
    void testReloadXssProtection() {
        // 重新加载XSS防护配置
        XssProtectionUtil.reloadXssProtection(config);
        
        // 验证配置已重新加载
        assertTrue(XssProtectionUtil.containsXssAttack("script", config));
        assertFalse(XssProtectionUtil.needsXssProtection(request, config));
    }

    @Test
    @DisplayName("测试XSS防护禁用")
    void testXssProtectionDisabled() {
        // 禁用XSS防护
        config.setEnabled(false);
        
        // 测试防护功能
        assertFalse(XssProtectionUtil.needsXssProtection(request, config));
        assertFalse(XssProtectionUtil.containsXssAttack("<script>alert('xss')</script>", config));
        
        String content = "<script>alert('xss')</script>";
        String filteredContent = XssProtectionUtil.filterXssAttack(content, config);
        assertEquals(content, filteredContent);
    }

    @Test
    @DisplayName("测试关键字检测禁用")
    void testKeywordsDetectionDisabled() {
        // 禁用关键字检测
        config.getKeywords().setEnabled(false);
        
        // 测试关键字检测
        assertFalse(XssProtectionUtil.containsXssAttack("<script>alert('xss')</script>", config));
        
        String content = "<script>alert('xss')</script>";
        String filteredContent = XssProtectionUtil.filterXssAttack(content, config);
        assertEquals(content, filteredContent);
        
        List<String> matchedKeywords = XssProtectionUtil.getMatchedXssKeywords(content, config);
        assertTrue(matchedKeywords.isEmpty());
    }
}
