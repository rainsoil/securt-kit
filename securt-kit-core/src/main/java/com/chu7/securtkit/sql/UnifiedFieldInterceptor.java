package com.chu7.securtkit.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统一字段拦截器，协调SQL解析和字段处理
 *
 * @author security-kit
 */
@Component
public class UnifiedFieldInterceptor {
    
    @Autowired
    private SqlAnalyzer sqlAnalyzer;
    
    @Autowired
    private FieldProcessor fieldProcessor;
    
    /**
     * 拦截并处理SQL
     *
     * @param sql 原始SQL
     * @return 处理后的SQL
     */
    public String intercept(String sql) {
        try {
            // 1. 判断是否需要处理
            if (!sqlAnalyzer.needProcess(sql)) {
                return sql;
            }
            
            // 2. 解析SQL
            SqlAnalysisResult analysisResult = sqlAnalyzer.analyze(sql);
            if (!analysisResult.isParseSuccess()) {
                System.out.println("SQL解析失败: " + analysisResult.getErrorMessage());
                return sql;
            }
            
            // 3. 判断是否需要处理
            if (!analysisResult.isNeedProcess()) {
                return sql;
            }
            
            // 4. 处理字段
            List<FieldContext> fieldsToProcess = analysisResult.getFieldContexts().stream()
                    .filter(field -> field.isNeedEncrypt() || field.isNeedDecrypt())
                    .collect(Collectors.toList());
            
            if (fieldsToProcess.isEmpty()) {
                return sql;
            }
            
            // 5. 调用字段处理器
            ProcessResult processResult = fieldProcessor.process(fieldsToProcess);
            
            // 6. 应用处理结果
            return applyProcessResult(sql, processResult, analysisResult);
            
        } catch (Exception e) {
            System.err.println("字段拦截处理失败: " + sql);
            e.printStackTrace();
            return sql;
        }
    }
    
    /**
     * 拦截并处理SQL（带参数）
     *
     * @param sql 原始SQL
     * @param parameters SQL参数
     * @return 处理后的SQL
     */
    public String intercept(String sql, Object parameters) {
        try {
            // 1. 判断是否需要处理
            if (!sqlAnalyzer.needProcess(sql)) {
                return sql;
            }
            
            // 2. 解析SQL（带参数）
            SqlAnalysisResult analysisResult = sqlAnalyzer.analyze(sql, parameters);
            if (!analysisResult.isParseSuccess()) {
                System.out.println("SQL解析失败: " + analysisResult.getErrorMessage());
                return sql;
            }
            
            // 3. 判断是否需要处理
            if (!analysisResult.isNeedProcess()) {
                return sql;
            }
            
            // 4. 处理字段
            List<FieldContext> fieldsToProcess = analysisResult.getFieldContexts().stream()
                    .filter(field -> field.isNeedEncrypt() || field.isNeedDecrypt())
                    .collect(Collectors.toList());
            
            if (fieldsToProcess.isEmpty()) {
                return sql;
            }
            
            // 5. 调用字段处理器
            ProcessResult processResult = fieldProcessor.process(fieldsToProcess);
            
            // 6. 应用处理结果
            return applyProcessResult(sql, processResult, analysisResult);
            
        } catch (Exception e) {
            System.err.println("字段拦截处理失败: " + sql);
            e.printStackTrace();
            return sql;
        }
    }
    
    /**
     * 应用处理结果到SQL
     *
     * @param originalSql 原始SQL
     * @param processResult 处理结果
     * @param analysisResult 分析结果
     * @return 处理后的SQL
     */
    private String applyProcessResult(String originalSql, ProcessResult processResult, SqlAnalysisResult analysisResult) {
        if (!processResult.isModified()) {
            return originalSql;
        }
        
        String resultSql = originalSql;
        
        // 应用表达式修改
        if (processResult.getProcessedExpressions() != null && !processResult.getProcessedExpressions().isEmpty()) {
            for (Map.Entry<String, String> entry : processResult.getProcessedExpressions().entrySet()) {
                String fieldName = entry.getKey();
                String newExpression = entry.getValue();
                // 这里需要根据具体的SQL类型和字段位置来替换
                // 可以使用正则表达式或者JSqlParser的修改功能
                resultSql = replaceFieldExpression(resultSql, fieldName, newExpression);
            }
        }
        
        return resultSql;
    }
    
    /**
     * 替换字段表达式
     *
     * @param sql SQL语句
     * @param fieldName 字段名
     * @param newExpression 新表达式
     * @return 替换后的SQL
     */
    private String replaceFieldExpression(String sql, String fieldName, String newExpression) {
        // 简单的字符串替换，实际项目中可以使用更复杂的逻辑
        // 这里只是示例，实际实现需要考虑SQL语法的正确性
        return sql.replaceAll("\\b" + fieldName + "\\b", newExpression);
    }
} 