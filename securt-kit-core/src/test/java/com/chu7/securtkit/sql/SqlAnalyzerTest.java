package com.chu7.securtkit.sql;

import com.chu7.securtkit.sql.impl.DefaultSqlAnalyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL解析器测试类
 *
 * @author security-kit
 */
public class SqlAnalyzerTest {
    
    private DefaultSqlAnalyzer sqlAnalyzer;
    
    @BeforeEach
    void setUp() {
        sqlAnalyzer = new DefaultSqlAnalyzer();
    }
    
    @Test
    void testNeedProcess() {
        // 测试需要处理的SQL
        assertTrue(sqlAnalyzer.needProcess("SELECT * FROM users"));
        assertTrue(sqlAnalyzer.needProcess("INSERT INTO users (name) VALUES (?)"));
        assertTrue(sqlAnalyzer.needProcess("UPDATE users SET name = ? WHERE id = ?"));
        assertTrue(sqlAnalyzer.needProcess("DELETE FROM users WHERE id = ?"));
        
        // 测试不需要处理的SQL
        assertFalse(sqlAnalyzer.needProcess(null));
        assertFalse(sqlAnalyzer.needProcess(""));
        assertFalse(sqlAnalyzer.needProcess("SHOW TABLES"));
    }
    
    @Test
    void testAnalyzeSelect() {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        SqlAnalysisResult result = sqlAnalyzer.analyze(sql);
        
        assertNotNull(result);
        assertTrue(result.isParseSuccess());
        assertEquals(FieldContext.SqlType.SELECT, result.getSqlType());
        assertEquals(sql, result.getOriginalSql());
        assertNotNull(result.getStatement());
    }
    
    @Test
    void testAnalyzeInsert() {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        SqlAnalysisResult result = sqlAnalyzer.analyze(sql);
        
        assertNotNull(result);
        assertTrue(result.isParseSuccess());
        assertEquals(FieldContext.SqlType.INSERT, result.getSqlType());
        assertEquals(sql, result.getOriginalSql());
        assertNotNull(result.getStatement());
    }
    
    @Test
    void testAnalyzeUpdate() {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        SqlAnalysisResult result = sqlAnalyzer.analyze(sql);
        
        assertNotNull(result);
        assertTrue(result.isParseSuccess());
        assertEquals(FieldContext.SqlType.UPDATE, result.getSqlType());
        assertEquals(sql, result.getOriginalSql());
        assertNotNull(result.getStatement());
    }
    
    @Test
    void testAnalyzeDelete() {
        String sql = "DELETE FROM users WHERE id = ?";
        SqlAnalysisResult result = sqlAnalyzer.analyze(sql);
        
        assertNotNull(result);
        assertTrue(result.isParseSuccess());
        assertEquals(FieldContext.SqlType.DELETE, result.getSqlType());
        assertEquals(sql, result.getOriginalSql());
        assertNotNull(result.getStatement());
    }
    
    @Test
    void testAnalyzeInvalidSql() {
        String sql = "INVALID SQL STATEMENT";
        SqlAnalysisResult result = sqlAnalyzer.analyze(sql);
        
        assertNotNull(result);
        assertFalse(result.isParseSuccess());
        assertNotNull(result.getErrorMessage());
        assertEquals(sql, result.getOriginalSql());
    }
    
    @Test
    void testGetAnalyzerName() {
        assertEquals("DefaultSqlAnalyzer", sqlAnalyzer.getAnalyzerName());
    }
} 