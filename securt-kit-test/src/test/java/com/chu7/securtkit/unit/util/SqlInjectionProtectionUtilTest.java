package com.chu7.securtkit.unit.util;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.util.SqlInjectionProtectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL注入防护工具类测试
 */
@DisplayName("SQL注入防护工具类测试")
class SqlInjectionProtectionUtilTest {

    private SafetyConfig.SqlInjectionConfig config;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        config = new SafetyConfig.SqlInjectionConfig();
        config.setEnabled(true);
        
        // URL白名单配置
        config.getUrlWhitelist().setEnabled(true);
        config.getUrlWhitelist().getAllowedUrls().add("/api/public/**");
        
        // IP白名单配置
        config.getIpWhitelist().setEnabled(true);
        config.getIpWhitelist().getAllowedIps().add("192.168.1.100");
        
        // SQL注入关键字配置已移除，直接通过读取配置文件获取
        
        request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
    }

    @Test
    @DisplayName("测试是否需要SQL注入防护")
    void testNeedsSqlInjectionProtection() {
        // 测试URL在白名单中
        request.setRequestURI("/api/public/health");
        assertFalse(SqlInjectionProtectionUtil.needsSqlInjectionProtection(request, config));
        
        // 测试IP在白名单中
        request.setRequestURI("/api/private/data");
        request.setRemoteAddr("192.168.1.100");
        assertFalse(SqlInjectionProtectionUtil.needsSqlInjectionProtection(request, config));
        
        // 测试IP不在白名单中
        request.setRemoteAddr("8.8.8.8");
        assertTrue(SqlInjectionProtectionUtil.needsSqlInjectionProtection(request, config));
        
        // 测试IP白名单禁用
        config.getIpWhitelist().setEnabled(false);
        assertTrue(SqlInjectionProtectionUtil.needsSqlInjectionProtection(request, config));
        
        // 测试URL白名单禁用
        config.getIpWhitelist().setEnabled(true);
        config.getUrlWhitelist().setEnabled(false);
        request.setRequestURI("/api/public/health");
        assertTrue(SqlInjectionProtectionUtil.needsSqlInjectionProtection(request, config));
    }

    @Test
    @DisplayName("测试检查SQL注入攻击")
    void testContainsSqlInjectionAttack() {
        // 测试包含SQL注入攻击
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("SELECT * FROM users"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("UNION SELECT username, password"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("DROP TABLE users"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("' OR '1'='1"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("\" OR \"1\"=\"1\""));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("'; DROP TABLE users; --"));
        
        // 测试不包含SQL注入攻击
        assertFalse(SqlInjectionProtectionUtil.containsSqlInjectionAttack("Hello World"));
        assertFalse(SqlInjectionProtectionUtil.containsSqlInjectionAttack("normal content"));
        assertFalse(SqlInjectionProtectionUtil.containsSqlInjectionAttack(""));
        assertFalse(SqlInjectionProtectionUtil.containsSqlInjectionAttack(null));
    }

    @Test
    @DisplayName("测试过滤SQL注入攻击")
    void testFilterSqlInjectionAttack() {
        // 测试过滤SQL注入攻击
        String content = "SELECT * FROM users WHERE id = 1";
        String filteredContent = SqlInjectionProtectionUtil.filterSqlInjectionAttack(content);
        
        assertNotNull(filteredContent);
        assertTrue(filteredContent.contains("*"));
        assertFalse(filteredContent.contains("SELECT"));
        assertFalse(filteredContent.contains("FROM"));
        assertFalse(filteredContent.contains("WHERE"));
        
        // 测试正常内容不过滤
        String normalContent = "normal content";
        String filteredNormalContent = SqlInjectionProtectionUtil.filterSqlInjectionAttack(normalContent);
        assertEquals(normalContent, filteredNormalContent);
    }

    @Test
    @DisplayName("测试获取匹配的SQL注入关键字")
    void testGetMatchedSqlInjectionKeywords() {
        // 测试获取匹配的关键字
        String content = "SELECT * FROM users WHERE id = 1 UNION SELECT username, password FROM users";
        List<String> matchedKeywords = SqlInjectionProtectionUtil.getMatchedSqlInjectionKeywords(content);
        
        assertNotNull(matchedKeywords);
        assertTrue(matchedKeywords.size() > 0);
        assertTrue(matchedKeywords.contains("SELECT"));
        assertTrue(matchedKeywords.contains("UNION"));
        assertTrue(matchedKeywords.contains("FROM"));
        assertTrue(matchedKeywords.contains("WHERE"));
        
        // 测试正常内容无匹配关键字
        String normalContent = "normal content";
        List<String> normalMatchedKeywords = SqlInjectionProtectionUtil.getMatchedSqlInjectionKeywords(normalContent);
        assertTrue(normalMatchedKeywords.isEmpty());
    }

    @Test
    @DisplayName("测试SQL注入攻击模式")
    void testSqlInjectionAttackPatterns() {
        // 测试常见的SQL注入攻击模式
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("' OR '1'='1"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("\" OR \"1\"=\"1\""));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("'; DROP TABLE users; --"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("UNION SELECT NULL,NULL,NULL"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("AND 1=1"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("OR 1=1"));
        
        // 测试编码绕过
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("%27 OR %271%27=%271"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("%22 OR %221%22=%221"));
        
        // 测试注释绕过
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("SELECT * FROM users /*"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("SELECT * FROM users --"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("SELECT * FROM users #"));
    }

    @Test
    @DisplayName("测试大小写不敏感")
    void testCaseInsensitive() {
        // 测试大小写不敏感
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("select * from users"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("SELECT * FROM USERS"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("Select * From Users"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("sElEcT * fRoM uSeRs"));
        
        // 测试混合大小写
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("UnIoN sElEcT"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("DrOp TaBlE"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("InSeRt InTo"));
    }

    @Test
    @DisplayName("测试复杂SQL注入攻击")
    void testComplexSqlInjectionAttacks() {
        // 测试复杂的SQL注入攻击
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("SELECT * FROM users WHERE id = 1 UNION SELECT username, password FROM users"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("INSERT INTO users (username, password) VALUES ('admin', 'password')"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("UPDATE users SET password = 'newpassword' WHERE id = 1"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("DELETE FROM users WHERE id = 1"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("CREATE TABLE hackers (id INT, name VARCHAR(50))"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("ALTER TABLE users ADD COLUMN hacked BOOLEAN"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("TRUNCATE TABLE users"));
        
        // 测试存储过程调用
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("EXEC sp_configure 'show advanced options', 1"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("EXEC xp_cmdshell 'dir'"));
        assertTrue(SqlInjectionProtectionUtil.containsSqlInjectionAttack("EXEC ms_foreachdb 'SELECT * FROM ?.sys.tables'"));
    }
}
