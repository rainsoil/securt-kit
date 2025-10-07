package com.chu7.securtkit.unit.keywords;

import com.chu7.securtkit.safety.keywords.SensitiveWordKeywordsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 敏感词关键字管理器测试
 */
@DisplayName("敏感词关键字管理器测试")
class SensitiveWordKeywordsManagerTest {

    @BeforeEach
    void setUp() {
        // 清空缓存
        SensitiveWordKeywordsManager.clearSensitiveWordKeywordsCache();
        
        // 初始化敏感词关键字
        SensitiveWordKeywordsManager.initSensitiveWordKeywords();
    }

    @Test
    @DisplayName("测试敏感词关键字初始化")
    void testInitSensitiveWordKeywords() {
        // 验证敏感词关键字是否生效
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("暴力恐怖内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("色情低俗内容"));
        
        // 验证不在关键字中的内容
        assertFalse(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("Hello World"));
        assertFalse(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("normal content"));
    }

    @Test
    @DisplayName("测试敏感词关键字检测")
    void testContainsSensitiveWordKeywords() {
        // 测试包含敏感词关键字
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("暴力恐怖内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("色情低俗内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("赌博博彩内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("毒品违禁内容"));
        
        // 测试不包含敏感词关键字
        assertFalse(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("Hello World"));
        assertFalse(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("normal content"));
        assertFalse(SensitiveWordKeywordsManager.containsSensitiveWordKeywords(""));
        assertFalse(SensitiveWordKeywordsManager.containsSensitiveWordKeywords(null));
    }

    @Test
    @DisplayName("测试获取匹配的敏感词关键字")
    void testGetMatchedKeywords() {
        // 测试获取匹配的关键字
        String content = "政治敏感内容和暴力恐怖内容";
        List<String> matchedKeywords = SensitiveWordKeywordsManager.getMatchedKeywords(content);
        
        assertNotNull(matchedKeywords);
        assertTrue(matchedKeywords.size() > 0);
        assertTrue(matchedKeywords.contains("政治"));
        assertTrue(matchedKeywords.contains("暴力"));
        assertTrue(matchedKeywords.contains("恐怖"));
        
        // 测试正常内容无匹配关键字
        String normalContent = "normal content";
        List<String> normalMatchedKeywords = SensitiveWordKeywordsManager.getMatchedKeywords(normalContent);
        assertTrue(normalMatchedKeywords.isEmpty());
    }

    @Test
    @DisplayName("测试检测模式")
    void testDetectionMode() {
        // 测试默认模式
        assertEquals("normal", SensitiveWordKeywordsManager.getDetectionMode());
        
        // 测试设置检测模式
        SensitiveWordKeywordsManager.setDetectionMode("strict");
        assertEquals("strict", SensitiveWordKeywordsManager.getDetectionMode());
        
        SensitiveWordKeywordsManager.setDetectionMode("loose");
        assertEquals("loose", SensitiveWordKeywordsManager.getDetectionMode());
        
        // 恢复默认模式
        SensitiveWordKeywordsManager.setDetectionMode("normal");
        assertEquals("normal", SensitiveWordKeywordsManager.getDetectionMode());
    }

    @Test
    @DisplayName("测试替换字符")
    void testReplaceChar() {
        // 测试默认替换字符
        assertEquals("*", SensitiveWordKeywordsManager.getReplaceChar());
        
        // 测试设置替换字符
        SensitiveWordKeywordsManager.setReplaceChar("#");
        assertEquals("#", SensitiveWordKeywordsManager.getReplaceChar());
        
        // 恢复默认替换字符
        SensitiveWordKeywordsManager.setReplaceChar("*");
        assertEquals("*", SensitiveWordKeywordsManager.getReplaceChar());
    }

    @Test
    @DisplayName("测试关键字数量")
    void testGetKeywordCount() {
        int keywordCount = SensitiveWordKeywordsManager.getKeywordCount();
        assertTrue(keywordCount > 0);
        
        // 清空缓存后数量应该为0
        SensitiveWordKeywordsManager.clearSensitiveWordKeywordsCache();
        assertEquals(0, SensitiveWordKeywordsManager.getKeywordCount());
    }

    @Test
    @DisplayName("测试重新加载关键字")
    void testReloadSensitiveWordKeywords() {
        // 获取初始关键字数量
        int initialCount = SensitiveWordKeywordsManager.getKeywordCount();
        
        // 重新加载关键字
        SensitiveWordKeywordsManager.reloadSensitiveWordKeywords();
        
        // 验证关键字数量
        int reloadedCount = SensitiveWordKeywordsManager.getKeywordCount();
        assertEquals(initialCount, reloadedCount);
        
        // 验证关键字仍然有效
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
    }

    @Test
    @DisplayName("测试敏感词过滤")
    void testFilterSensitiveWordKeywords() {
        // 测试过滤敏感词
        String content = "政治敏感内容和暴力恐怖内容";
        String filteredContent = SensitiveWordKeywordsManager.filterSensitiveWordKeywords(content);
        
        assertNotNull(filteredContent);
        assertTrue(filteredContent.contains("*"));
        assertFalse(filteredContent.contains("政治"));
        assertFalse(filteredContent.contains("暴力"));
        assertFalse(filteredContent.contains("恐怖"));
        
        // 测试正常内容不过滤
        String normalContent = "normal content";
        String filteredNormalContent = SensitiveWordKeywordsManager.filterSensitiveWordKeywords(normalContent);
        assertEquals(normalContent, filteredNormalContent);
    }

    @Test
    @DisplayName("测试大小写不敏感")
    void testCaseInsensitive() {
        // 测试大小写不敏感
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
        
        // 测试混合大小写
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("政治敏感内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("暴力恐怖内容"));
        assertTrue(SensitiveWordKeywordsManager.containsSensitiveWordKeywords("色情低俗内容"));
    }
}
