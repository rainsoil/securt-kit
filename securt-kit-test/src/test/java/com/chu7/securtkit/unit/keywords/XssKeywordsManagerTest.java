package com.chu7.securtkit.unit.keywords;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.keywords.XssKeywordsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XSS关键字管理器测试
 */
@DisplayName("XSS关键字管理器测试")
class XssKeywordsManagerTest {

    private SafetyConfig.XssConfig.XssKeywordsConfig config;

    @BeforeEach
    void setUp() {
        config = new SafetyConfig.XssConfig.XssKeywordsConfig();
        config.setEnabled(true);
        config.setEnableBuiltin(true);
        config.setDetectionMode("normal");
        config.getKeywords().addAll(Arrays.asList(
            "custom-script",
            "company-specific",
            "internal-tool"
        ));
    }

    @Test
    @DisplayName("测试XSS关键字初始化")
    void testInitXssKeywords() {
        // 清空缓存
        XssKeywordsManager.clearXssKeywordsCache();
        
        // 初始化XSS关键字
        XssKeywordsManager.initXssKeywords(config);
        
        // 验证XSS关键字是否生效
        assertTrue(XssKeywordsManager.containsXssKeywords("custom-script"));
        assertTrue(XssKeywordsManager.containsXssKeywords("company-specific"));
        assertTrue(XssKeywordsManager.containsXssKeywords("internal-tool"));
        
        // 验证内置关键字
        assertTrue(XssKeywordsManager.containsXssKeywords("script"));
        assertTrue(XssKeywordsManager.containsXssKeywords("javascript"));
        assertTrue(XssKeywordsManager.containsXssKeywords("onclick"));
    }

    @Test
    @DisplayName("测试不同检测模式")
    void testDetectionModes() {
        // 测试严格模式
        config.setDetectionMode("strict");
        XssKeywordsManager.clearXssKeywordsCache();
        XssKeywordsManager.initXssKeywords(config);
        
        assertTrue(XssKeywordsManager.containsXssKeywords("script"));
        assertTrue(XssKeywordsManager.containsXssKeywords("javascript"));
        assertFalse(XssKeywordsManager.containsXssKeywords("style"));
        
        // 测试普通模式
        config.setDetectionMode("normal");
        XssKeywordsManager.clearXssKeywordsCache();
        XssKeywordsManager.initXssKeywords(config);
        
        assertTrue(XssKeywordsManager.containsXssKeywords("script"));
        assertTrue(XssKeywordsManager.containsXssKeywords("alert"));
        assertFalse(XssKeywordsManager.containsXssKeywords("style"));
        
        // 测试宽松模式
        config.setDetectionMode("loose");
        XssKeywordsManager.clearXssKeywordsCache();
        XssKeywordsManager.initXssKeywords(config);
        
        assertTrue(XssKeywordsManager.containsXssKeywords("script"));
        assertTrue(XssKeywordsManager.containsXssKeywords("alert"));
        assertTrue(XssKeywordsManager.containsXssKeywords("style"));
    }

    @Test
    @DisplayName("测试获取匹配的关键字")
    void testGetMatchedKeywords() {
        // 清空缓存
        XssKeywordsManager.clearXssKeywordsCache();
        
        // 初始化XSS关键字
        XssKeywordsManager.initXssKeywords(config);
        
        // 测试获取匹配的关键字
        String content = "This is a script tag with javascript";
        List<String> matchedKeywords = XssKeywordsManager.getMatchedKeywords(content);
        
        assertNotNull(matchedKeywords);
        assertTrue(matchedKeywords.size() > 0);
        assertTrue(matchedKeywords.contains("script"));
        assertTrue(matchedKeywords.contains("javascript"));
    }

    @Test
    @DisplayName("测试过滤XSS关键字")
    void testFilterXssKeywords() {
        // 清空缓存
        XssKeywordsManager.clearXssKeywordsCache();
        
        // 初始化XSS关键字
        XssKeywordsManager.initXssKeywords(config);
        
        // 测试过滤XSS关键字
        String content = "This is a script tag with javascript";
        String filteredContent = XssKeywordsManager.filterXssKeywords(content, "*");
        
        assertNotNull(filteredContent);
        assertTrue(filteredContent.contains("*"));
        assertFalse(filteredContent.contains("script"));
        assertFalse(filteredContent.contains("javascript"));
    }

    @Test
    @DisplayName("测试XSS关键字统计信息")
    void testGetXssKeywordsStats() {
        // 清空缓存
        XssKeywordsManager.clearXssKeywordsCache();
        
        // 初始化XSS关键字
        XssKeywordsManager.initXssKeywords(config);
        
        // 获取统计信息
        Map<String, Object> stats = XssKeywordsManager.getXssKeywordsStats();
        
        // 验证统计信息
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalKeywords"));
        assertTrue(stats.containsKey("detectionMode"));
        assertTrue(stats.containsKey("strictKeywords"));
        assertTrue(stats.containsKey("normalKeywords"));
        assertTrue(stats.containsKey("looseKeywords"));
        
        // 验证数量大于0
        assertTrue((Integer) stats.get("totalKeywords") > 0);
        assertEquals("normal", stats.get("detectionMode"));
    }

    @Test
    @DisplayName("测试XSS关键字重新加载")
    void testReloadXssKeywords() {
        // 清空缓存
        XssKeywordsManager.clearXssKeywordsCache();
        
        // 初始化XSS关键字
        XssKeywordsManager.initXssKeywords(config);
        
        // 获取初始统计信息
        Map<String, Object> initialStats = XssKeywordsManager.getXssKeywordsStats();
        
        // 重新加载XSS关键字
        XssKeywordsManager.reloadXssKeywords(config);
        
        // 获取重新加载后的统计信息
        Map<String, Object> reloadedStats = XssKeywordsManager.getXssKeywordsStats();
        
        // 验证统计信息一致
        assertEquals(initialStats.get("totalKeywords"), reloadedStats.get("totalKeywords"));
        assertEquals(initialStats.get("detectionMode"), reloadedStats.get("detectionMode"));
    }

    @Test
    @DisplayName("测试空值和边界情况处理")
    void testNullAndEdgeCaseHandling() {
        // 清空缓存
        XssKeywordsManager.clearXssKeywordsCache();
        
        // 初始化XSS关键字
        XssKeywordsManager.initXssKeywords(config);
        
        // 测试空值
        assertFalse(XssKeywordsManager.containsXssKeywords(null));
        assertFalse(XssKeywordsManager.containsXssKeywords(""));
        assertFalse(XssKeywordsManager.containsXssKeywords("   "));
        
        // 测试获取匹配关键字空值
        assertTrue(XssKeywordsManager.getMatchedKeywords(null).isEmpty());
        assertTrue(XssKeywordsManager.getMatchedKeywords("").isEmpty());
        assertTrue(XssKeywordsManager.getMatchedKeywords("   ").isEmpty());
        
        // 测试过滤空值
        assertNull(XssKeywordsManager.filterXssKeywords(null, "*"));
        assertEquals("", XssKeywordsManager.filterXssKeywords("", "*"));
        assertEquals("   ", XssKeywordsManager.filterXssKeywords("   ", "*"));
    }

    @Test
    @DisplayName("测试XSS关键字禁用")
    void testXssKeywordsDisabled() {
        // 禁用XSS关键字
        config.setEnabled(false);
        
        // 清空缓存
        XssKeywordsManager.clearXssKeywordsCache();
        
        // 初始化XSS关键字
        XssKeywordsManager.initXssKeywords(config);
        
        // 验证XSS关键字未加载
        assertFalse(XssKeywordsManager.containsXssKeywords("script"));
        assertFalse(XssKeywordsManager.containsXssKeywords("javascript"));
        assertTrue(XssKeywordsManager.getMatchedKeywords("script").isEmpty());
    }
}
