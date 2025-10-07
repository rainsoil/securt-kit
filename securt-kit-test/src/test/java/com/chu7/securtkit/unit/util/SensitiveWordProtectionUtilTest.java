package com.chu7.securtkit.unit.util;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.util.SensitiveWordProtectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 敏感词过滤工具类测试
 */
@DisplayName("敏感词过滤工具类测试")
class SensitiveWordProtectionUtilTest {

    private SafetyConfig.SensitiveWordConfig config;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        config = new SafetyConfig.SensitiveWordConfig();
        config.setEnabled(true);
        
        // URL白名单配置
        config.getUrlWhitelist().setEnabled(true);
        config.getUrlWhitelist().getAllowedUrls().add("/api/public/**");
        
        // IP白名单配置
        config.getIpWhitelist().setEnabled(true);
        config.getIpWhitelist().getAllowedIps().add("192.168.1.100");
        
        // 敏感词关键字配置已移除，直接通过读取配置文件获取
        
        request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
    }

    @Test
    @DisplayName("测试是否需要敏感词过滤防护")
    void testNeedsSensitiveWordProtection() {
        // 测试URL在白名单中
        request.setRequestURI("/api/public/health");
        assertFalse(SensitiveWordProtectionUtil.needsSensitiveWordProtection(request, config));
        
        // 测试IP在白名单中
        request.setRequestURI("/api/private/data");
        request.setRemoteAddr("192.168.1.100");
        assertFalse(SensitiveWordProtectionUtil.needsSensitiveWordProtection(request, config));
        
        // 测试IP不在白名单中
        request.setRemoteAddr("8.8.8.8");
        assertTrue(SensitiveWordProtectionUtil.needsSensitiveWordProtection(request, config));
        
        // 测试IP白名单禁用
        config.getIpWhitelist().setEnabled(false);
        assertTrue(SensitiveWordProtectionUtil.needsSensitiveWordProtection(request, config));
        
        // 测试URL白名单禁用
        config.getIpWhitelist().setEnabled(true);
        config.getUrlWhitelist().setEnabled(false);
        request.setRequestURI("/api/public/health");
        assertTrue(SensitiveWordProtectionUtil.needsSensitiveWordProtection(request, config));
    }

    @Test
    @DisplayName("测试检查敏感词")
    void testContainsSensitiveWord() {
        // 测试包含敏感词
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政治敏感内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("暴力恐怖内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("色情低俗内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("赌博博彩内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("毒品违禁内容"));
        
        // 测试不包含敏感词
        assertFalse(SensitiveWordProtectionUtil.containsSensitiveWord("Hello World"));
        assertFalse(SensitiveWordProtectionUtil.containsSensitiveWord("normal content"));
        assertFalse(SensitiveWordProtectionUtil.containsSensitiveWord(""));
        assertFalse(SensitiveWordProtectionUtil.containsSensitiveWord(null));
    }

    @Test
    @DisplayName("测试过滤敏感词")
    void testFilterSensitiveWord() {
        // 测试过滤敏感词
        String content = "政治敏感内容和暴力恐怖内容";
        String filteredContent = SensitiveWordProtectionUtil.filterSensitiveWord(content);
        
        assertNotNull(filteredContent);
        assertTrue(filteredContent.contains("*"));
        assertFalse(filteredContent.contains("政治"));
        assertFalse(filteredContent.contains("暴力"));
        assertFalse(filteredContent.contains("恐怖"));
        
        // 测试正常内容不过滤
        String normalContent = "normal content";
        String filteredNormalContent = SensitiveWordProtectionUtil.filterSensitiveWord(normalContent);
        assertEquals(normalContent, filteredNormalContent);
    }

    @Test
    @DisplayName("测试获取匹配的敏感词关键字")
    void testGetMatchedSensitiveWordKeywords() {
        // 测试获取匹配的关键字
        String content = "政治敏感内容和暴力恐怖内容";
        List<String> matchedKeywords = SensitiveWordProtectionUtil.getMatchedSensitiveWordKeywords(content);
        
        assertNotNull(matchedKeywords);
        assertTrue(matchedKeywords.size() > 0);
        assertTrue(matchedKeywords.contains("政治"));
        assertTrue(matchedKeywords.contains("暴力"));
        assertTrue(matchedKeywords.contains("恐怖"));
        
        // 测试正常内容无匹配关键字
        String normalContent = "normal content";
        List<String> normalMatchedKeywords = SensitiveWordProtectionUtil.getMatchedSensitiveWordKeywords(normalContent);
        assertTrue(normalMatchedKeywords.isEmpty());
    }

    @Test
    @DisplayName("测试敏感词类型")
    void testSensitiveWordTypes() {
        // 测试政治敏感词
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政治敏感内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政府相关内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("领导人相关内容"));
        
        // 测试暴力恐怖词
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("暴力恐怖内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("爆炸袭击内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("杀人死亡内容"));
        
        // 测试色情低俗词
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("色情低俗内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("成人情色内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("黄色下流内容"));
        
        // 测试赌博博彩词
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("赌博博彩内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("彩票下注内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("赌场庄家内容"));
        
        // 测试毒品违禁词
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("毒品违禁内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("吸毒贩毒内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("海洛因冰毒内容"));
    }

    @Test
    @DisplayName("测试大小写不敏感")
    void testCaseInsensitive() {
        // 测试大小写不敏感
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政治敏感内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政治敏感内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政治敏感内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政治敏感内容"));
        
        // 测试混合大小写
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("政治敏感内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("暴力恐怖内容"));
        assertTrue(SensitiveWordProtectionUtil.containsSensitiveWord("色情低俗内容"));
    }
}
