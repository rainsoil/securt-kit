package com.chu7.securtkit.test;

import com.chu7.securtkit.core.analyzer.UnifiedSqlAnalyzer;
import com.chu7.securtkit.core.interceptor.UnifiedFieldInterceptor;
import com.chu7.securtkit.core.model.SqlAnalysisResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字段加密器测试类
 * 测试三个核心组件的功能
 *
 * @author securt-kit
 */
@SpringBootTest
public class FieldEncryptorTest {
    
    @Autowired
    private UnifiedSqlAnalyzer sqlAnalyzer;
    
    @Autowired
    private UnifiedFieldInterceptor fieldInterceptor;
    
    @Test
    public void testSqlAnalyzer() {
        System.out.println("=== 测试SQL解析器 ===");
        
        // 测试SELECT语句
        String selectSql = "SELECT id, name, email FROM users WHERE age > 18";
        SqlAnalysisResult result = sqlAnalyzer.analyze(selectSql);
        
        assertNotNull(result);
        assertTrue(result.isNeedProcess());
        assertEquals(selectSql, result.getOriginalSql());
        assertNotNull(result.getFieldContexts());
        
        System.out.println("SELECT语句解析结果:");
        System.out.println("字段数量: " + result.getFieldContexts().size());
        result.getFieldContexts().forEach(field -> 
            System.out.println("字段: " + field.getFieldName() + 
                            ", 位置: " + field.getPosition() + 
                            ", 表: " + field.getTableName())
        );
        
        // 测试INSERT语句
        String insertSql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        result = sqlAnalyzer.analyze(insertSql);
        
        assertNotNull(result);
        assertTrue(result.isNeedProcess());
        System.out.println("\nINSERT语句解析结果:");
        System.out.println("字段数量: " + result.getFieldContexts().size());
        result.getFieldContexts().forEach(field -> 
            System.out.println("字段: " + field.getFieldName() + 
                            ", 位置: " + field.getPosition() + 
                            ", 表: " + field.getTableName())
        );
        
        // 测试UPDATE语句
        String updateSql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        result = sqlAnalyzer.analyze(updateSql);
        
        assertNotNull(result);
        assertTrue(result.isNeedProcess());
        System.out.println("\nUPDATE语句解析结果:");
        System.out.println("字段数量: " + result.getFieldContexts().size());
        result.getFieldContexts().forEach(field -> 
            System.out.println("字段: " + field.getFieldName() + 
                            ", 位置: " + field.getPosition() + 
                            ", 表: " + field.getTableName())
        );
        
        // 测试DELETE语句
        String deleteSql = "DELETE FROM users WHERE id = ?";
        result = sqlAnalyzer.analyze(deleteSql);
        
        assertNotNull(result);
        assertTrue(result.isNeedProcess());
        System.out.println("\nDELETE语句解析结果:");
        System.out.println("字段数量: " + result.getFieldContexts().size());
        result.getFieldContexts().forEach(field -> 
            System.out.println("字段: " + field.getFieldName() + 
                            ", 位置: " + field.getPosition() + 
                            ", 表: " + field.getTableName())
        );
    }
    
    @Test
    public void testFieldInterceptor() {
        System.out.println("\n=== 测试字段拦截器 ===");
        
        // 测试SELECT语句拦截
        String selectSql = "SELECT id, name, email FROM users WHERE age > 18";
        String processedSql = fieldInterceptor.interceptSql(selectSql);
        
        assertNotNull(processedSql);
        System.out.println("SELECT语句拦截结果:");
        System.out.println("原始SQL: " + selectSql);
        System.out.println("处理后SQL: " + processedSql);
        
        // 测试INSERT语句拦截
        String insertSql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        processedSql = fieldInterceptor.interceptSql(insertSql);
        
        assertNotNull(processedSql);
        System.out.println("\nINSERT语句拦截结果:");
        System.out.println("原始SQL: " + insertSql);
        System.out.println("处理后SQL: " + processedSql);
        
        // 测试UPDATE语句拦截
        String updateSql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        processedSql = fieldInterceptor.interceptSql(updateSql);
        
        assertNotNull(processedSql);
        System.out.println("\nUPDATE语句拦截结果:");
        System.out.println("原始SQL: " + updateSql);
        System.out.println("处理后SQL: " + processedSql);
        
        // 测试DELETE语句拦截
        String deleteSql = "DELETE FROM users WHERE id = ?";
        processedSql = fieldInterceptor.interceptSql(deleteSql);
        
        assertNotNull(processedSql);
        System.out.println("\nDELETE语句拦截结果:");
        System.out.println("原始SQL: " + deleteSql);
        System.out.println("处理后SQL: " + processedSql);
    }
    
    @Test
    public void testComplexSql() {
        System.out.println("\n=== 测试复杂SQL语句 ===");
        
        // 测试复杂SELECT语句
        String complexSelectSql = "SELECT u.id, u.name, u.email, " +
                                 "COUNT(o.id) as order_count " +
                                 "FROM users u " +
                                 "LEFT JOIN orders o ON u.id = o.user_id " +
                                 "WHERE u.age > 18 AND u.status = 'active' " +
                                 "GROUP BY u.id, u.name, u.email " +
                                 "HAVING order_count > 0 " +
                                 "ORDER BY order_count DESC";
        
        SqlAnalysisResult result = sqlAnalyzer.analyze(complexSelectSql);
        
        assertNotNull(result);
        assertTrue(result.isNeedProcess());
        System.out.println("复杂SELECT语句解析结果:");
        System.out.println("字段数量: " + result.getFieldContexts().size());
        result.getFieldContexts().forEach(field -> 
            System.out.println("字段: " + field.getFieldName() + 
                            ", 位置: " + field.getPosition() + 
                            ", 表: " + field.getTableName())
        );
        
        // 测试拦截处理
        String processedSql = fieldInterceptor.interceptSql(complexSelectSql);
        assertNotNull(processedSql);
        System.out.println("\n复杂SELECT语句拦截结果:");
        System.out.println("原始SQL: " + complexSelectSql);
        System.out.println("处理后SQL: " + processedSql);
    }
} 