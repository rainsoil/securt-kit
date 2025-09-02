package com.chu7.securtkit.sql;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理结果，存储字段处理的结果信息
 *
 * @author security-kit
 */
@Data
@Builder
public class ProcessResult {
    
    /**
     * 是否有修改
     */
    private boolean modified;
    
    /**
     * 处理后的字段值映射（字段名 -> 新值）
     */
    private Map<String, Object> processedValues;
    
    /**
     * 处理后的SQL表达式映射（字段名 -> 新表达式）
     */
    private Map<String, String> processedExpressions;
    
    /**
     * 处理类型
     */
    private ProcessType processType;
    
    /**
     * 处理类型枚举
     */
    public enum ProcessType {
        VALUE,          // 值修改
        EXPRESSION,     // 表达式修改
        MIXED           // 混合修改
    }
    
    /**
     * 创建值修改结果
     */
    public static ProcessResult valueResult() {
        return ProcessResult.builder()
                .modified(false)
                .processedValues(new HashMap<>())
                .processedExpressions(new HashMap<>())
                .processType(ProcessType.VALUE)
                .build();
    }
    
    /**
     * 创建表达式修改结果
     */
    public static ProcessResult expressionResult() {
        return ProcessResult.builder()
                .modified(false)
                .processedValues(new HashMap<>())
                .processedExpressions(new HashMap<>())
                .processType(ProcessType.EXPRESSION)
                .build();
    }
    
    /**
     * 添加处理后的值
     */
    public ProcessResult addProcessedValue(String fieldName, Object value) {
        if (this.processedValues == null) {
            this.processedValues = new HashMap<>();
        }
        this.processedValues.put(fieldName, value);
        this.modified = true;
        return this;
    }
    
    /**
     * 添加处理后的表达式
     */
    public ProcessResult addProcessedExpression(String fieldName, String expression) {
        if (this.processedExpressions == null) {
            this.processedExpressions = new HashMap<>();
        }
        this.processedExpressions.put(fieldName, expression);
        this.modified = true;
        return this;
    }
} 