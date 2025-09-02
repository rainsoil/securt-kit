package com.chu7.securtkit.sql.impl;

import com.chu7.securtkit.sql.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 默认SQL解析器实现，基于JSqlParser解析SQL
 *
 * @author security-kit
 */
@Component
public class DefaultSqlAnalyzer implements SqlAnalyzer {
    
    @Override
    public SqlAnalysisResult analyze(String sql) {
        try {
            // 1. 解析SQL
            Statement statement = CCJSqlParserUtil.parse(sql);
            
            // 2. 分析SQL类型
            FieldContext.SqlType sqlType = getSqlType(statement);
            
            // 3. 创建分析结果
            SqlAnalysisResult.SqlAnalysisResultBuilder builder = SqlAnalysisResult.builder()
                    .originalSql(sql)
                    .statement(statement)
                    .sqlType(sqlType)
                    .parseSuccess(true);
            
            // 4. 解析字段信息
            List<FieldContext> fieldContexts = parseFields(statement, sqlType);
            builder.fieldContexts(fieldContexts);
            
            // 5. 构建表字段映射
            Map<String, List<FieldContext>> tableFieldMap = buildTableFieldMap(fieldContexts);
            builder.tableFieldMap(tableFieldMap);
            
            // 6. 识别需要处理的字段
            Set<String> encryptFields = new HashSet<>();
            Set<String> decryptFields = new HashSet<>();
            
            for (FieldContext field : fieldContexts) {
                if (field.isNeedEncrypt()) {
                    encryptFields.add(field.getFieldName());
                }
                if (field.isNeedDecrypt()) {
                    decryptFields.add(field.getFieldName());
                }
            }
            
            builder.encryptFields(encryptFields)
                    .decryptFields(decryptFields)
                    .needProcess(!encryptFields.isEmpty() || !decryptFields.isEmpty());
            
            return builder.build();
            
        } catch (JSQLParserException e) {
            return SqlAnalysisResult.builder()
                    .originalSql(sql)
                    .parseSuccess(false)
                    .errorMessage(e.getMessage())
                    .needProcess(false)
                    .build();
        }
    }
    
    @Override
    public SqlAnalysisResult analyze(String sql, Object parameters) {
        // 这里可以添加参数解析逻辑
        // 暂时调用无参数版本
        return analyze(sql);
    }
    
    @Override
    public boolean needProcess(String sql) {
        // 简单的判断逻辑，可以根据实际需求优化
        return sql != null && (sql.toLowerCase().contains("select") || 
                              sql.toLowerCase().contains("insert") || 
                              sql.toLowerCase().contains("update") || 
                              sql.toLowerCase().contains("delete"));
    }
    
    @Override
    public String getAnalyzerName() {
        return "DefaultSqlAnalyzer";
    }
    
    /**
     * 获取SQL类型
     */
    private FieldContext.SqlType getSqlType(Statement statement) {
        if (statement instanceof Select) {
            return FieldContext.SqlType.SELECT;
        } else if (statement instanceof Insert) {
            return FieldContext.SqlType.INSERT;
        } else if (statement instanceof Update) {
            return FieldContext.SqlType.UPDATE;
        } else if (statement instanceof Delete) {
            return FieldContext.SqlType.DELETE;
        }
        return FieldContext.SqlType.SELECT; // 默认
    }
    
    /**
     * 解析字段信息
     */
    private List<FieldContext> parseFields(Statement statement, FieldContext.SqlType sqlType) {
        List<FieldContext> fields = new ArrayList<>();
        
        // 这里需要根据不同的SQL类型实现具体的字段解析逻辑
        // 可以使用访问者模式遍历SQL语法树
        // 暂时返回空列表，实际实现需要完善
        
        return fields;
    }
    
    /**
     * 构建表字段映射
     */
    private Map<String, List<FieldContext>> buildTableFieldMap(List<FieldContext> fieldContexts) {
        Map<String, List<FieldContext>> tableFieldMap = new HashMap<>();
        
        for (FieldContext field : fieldContexts) {
            String tableName = field.getTableName();
            if (tableName != null) {
                tableFieldMap.computeIfAbsent(tableName, k -> new ArrayList<>()).add(field);
            }
        }
        
        return tableFieldMap;
    }
} 