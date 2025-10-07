package com.chu7.securtkit.unit.keywords;

import com.chu7.securtkit.safety.keywords.SqlInjectionKeywordsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL注入关键字管理器测试
 */
@DisplayName("SQL注入关键字管理器测试")
class SqlInjectionKeywordsManagerTest {

    @BeforeEach
    void setUp() {
        // 清空缓存
        SqlInjectionKeywordsManager.clearSqlInjectionKeywordsCache();
        
        // 初始化SQL注入关键字
        SqlInjectionKeywordsManager.initSqlInjectionKeywords();
    }

    @Test
    @DisplayName("测试SQL注入关键字初始化")
    void testInitSqlInjectionKeywords() {
        // 验证SQL注入关键字是否生效
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("SELECT * FROM users"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("UNION SELECT"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("DROP TABLE"));
        
        // 验证不在关键字中的内容
        assertFalse(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("Hello World"));
        assertFalse(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("normal content"));
    }

    @Test
    @DisplayName("测试SQL注入关键字检测")
    void testContainsSqlInjectionKeywords() {
        // 测试包含SQL注入关键字
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("SELECT * FROM users WHERE id = 1"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("UNION SELECT username, password FROM users"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("DROP TABLE users"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("INSERT INTO users VALUES"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("UPDATE users SET password"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("DELETE FROM users WHERE"));
        
        // 测试不包含SQL注入关键字
        assertFalse(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("Hello World"));
        assertFalse(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("normal content"));
        assertFalse(SqlInjectionKeywordsManager.containsSqlInjectionKeywords(""));
        assertFalse(SqlInjectionKeywordsManager.containsSqlInjectionKeywords(null));
    }

    @Test
    @DisplayName("测试获取匹配的SQL注入关键字")
    void testGetMatchedKeywords() {
        // 测试获取匹配的关键字
        String content = "SELECT * FROM users WHERE id = 1 UNION SELECT username, password FROM users";
        List<String> matchedKeywords = SqlInjectionKeywordsManager.getMatchedKeywords(content);
        
        assertNotNull(matchedKeywords);
        assertTrue(matchedKeywords.size() > 0);
        assertTrue(matchedKeywords.contains("SELECT"));
        assertTrue(matchedKeywords.contains("UNION"));
        assertTrue(matchedKeywords.contains("FROM"));
        assertTrue(matchedKeywords.contains("WHERE"));
        
        // 测试正常内容无匹配关键字
        String normalContent = "normal content";
        List<String> normalMatchedKeywords = SqlInjectionKeywordsManager.getMatchedKeywords(normalContent);
        assertTrue(normalMatchedKeywords.isEmpty());
    }

    @Test
    @DisplayName("测试检测模式")
    void testDetectionMode() {
        // 测试默认模式
        assertEquals("normal", SqlInjectionKeywordsManager.getDetectionMode());
        
        // 测试设置检测模式
        SqlInjectionKeywordsManager.setDetectionMode("strict");
        assertEquals("strict", SqlInjectionKeywordsManager.getDetectionMode());
        
        SqlInjectionKeywordsManager.setDetectionMode("loose");
        assertEquals("loose", SqlInjectionKeywordsManager.getDetectionMode());
        
        // 恢复默认模式
        SqlInjectionKeywordsManager.setDetectionMode("normal");
        assertEquals("normal", SqlInjectionKeywordsManager.getDetectionMode());
    }

    @Test
    @DisplayName("测试关键字数量")
    void testGetKeywordCount() {
        int keywordCount = SqlInjectionKeywordsManager.getKeywordCount();
        assertTrue(keywordCount > 0);
        
        // 清空缓存后数量应该为0
        SqlInjectionKeywordsManager.clearSqlInjectionKeywordsCache();
        assertEquals(0, SqlInjectionKeywordsManager.getKeywordCount());
    }

    @Test
    @DisplayName("测试重新加载关键字")
    void testReloadSqlInjectionKeywords() {
        // 获取初始关键字数量
        int initialCount = SqlInjectionKeywordsManager.getKeywordCount();
        
        // 重新加载关键字
        SqlInjectionKeywordsManager.reloadSqlInjectionKeywords();
        
        // 验证关键字数量
        int reloadedCount = SqlInjectionKeywordsManager.getKeywordCount();
        assertEquals(initialCount, reloadedCount);
        
        // 验证关键字仍然有效
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("SELECT * FROM users"));
    }

    @Test
    @DisplayName("测试SQL注入攻击模式")
    void testSqlInjectionPatterns() {
        // 测试常见的SQL注入攻击模式
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("' OR '1'='1"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("\" OR \"1\"=\"1\""));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("'; DROP TABLE users; --"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("UNION SELECT NULL,NULL,NULL"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("AND 1=1"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("OR 1=1"));
        
        // 测试编码绕过
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("%27 OR %271%27=%271"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("%22 OR %221%22=%221"));
        
        // 测试注释绕过
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("SELECT * FROM users /*"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("SELECT * FROM users --"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("SELECT * FROM users #"));
    }

    @Test
    @DisplayName("测试大小写不敏感")
    void testCaseInsensitive() {
        // 测试大小写不敏感
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("select * from users"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("SELECT * FROM USERS"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("Select * From Users"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("sElEcT * fRoM uSeRs"));
        
        // 测试混合大小写
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("UnIoN sElEcT"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("DrOp TaBlE"));
        assertTrue(SqlInjectionKeywordsManager.containsSqlInjectionKeywords("InSeRt InTo"));
    }
}
