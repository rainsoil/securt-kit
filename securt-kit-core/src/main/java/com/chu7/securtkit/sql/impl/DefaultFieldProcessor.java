package com.chu7.securtkit.sql.impl;

import com.chu7.securtkit.sql.FieldContext;
import com.chu7.securtkit.sql.FieldProcessor;
import com.chu7.securtkit.sql.ProcessResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 默认字段处理器实现，提供基本的字段处理逻辑
 *
 * @author security-kit
 */
@Component
public class DefaultFieldProcessor implements FieldProcessor {
    
    @Override
    public ProcessResult process(List<FieldContext> fieldContexts) {
        ProcessResult result = ProcessResult.valueResult();
        
        for (FieldContext field : fieldContexts) {
            ProcessResult fieldResult = process(field);
            if (fieldResult.isModified()) {
                // 合并处理结果
                if (fieldResult.getProcessedValues() != null) {
                    fieldResult.getProcessedValues().forEach(result::addProcessedValue);
                }
                if (fieldResult.getProcessedExpressions() != null) {
                    fieldResult.getProcessedExpressions().forEach(result::addProcessedExpression);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public ProcessResult process(FieldContext fieldContext) {
        ProcessResult result = ProcessResult.valueResult();
        
        try {
            if (fieldContext.isNeedEncrypt()) {
                // 处理加密逻辑
                String encryptedValue = encryptField(fieldContext);
                if (encryptedValue != null) {
                    result.addProcessedValue(fieldContext.getFieldName(), encryptedValue);
                }
            }
            
            if (fieldContext.isNeedDecrypt()) {
                // 处理解密逻辑
                String decryptedValue = decryptField(fieldContext);
                if (decryptedValue != null) {
                    result.addProcessedValue(fieldContext.getFieldName(), decryptedValue);
                }
            }
            
        } catch (Exception e) {
            System.err.println("字段处理失败: " + fieldContext.getFieldName() + ", 错误: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public String getProcessorName() {
        return "DefaultFieldProcessor";
    }
    
    @Override
    public boolean supports(FieldContext fieldContext) {
        // 默认支持所有字段
        return true;
    }
    
    /**
     * 加密字段
     */
    private String encryptField(FieldContext fieldContext) {
        // 这里实现具体的加密逻辑
        // 可以根据字段类型、表名等选择不同的加密策略
        // 暂时返回原值，实际实现需要完善
        
        Object value = fieldContext.getValue();
        if (value == null) {
            return null;
        }
        
        // 示例：简单的Base64编码（实际项目中应该使用真正的加密算法）
        return java.util.Base64.getEncoder().encodeToString(value.toString().getBytes());
    }
    
    /**
     * 解密字段
     */
    private String decryptField(FieldContext fieldContext) {
        // 这里实现具体的解密逻辑
        // 可以根据字段类型、表名等选择不同的解密策略
        // 暂时返回原值，实际实现需要完善
        
        Object value = fieldContext.getValue();
        if (value == null) {
            return null;
        }
        
        try {
            // 示例：简单的Base64解码（实际项目中应该使用真正的解密算法）
            return new String(java.util.Base64.getDecoder().decode(value.toString()));
        } catch (Exception e) {
            // 如果解码失败，返回原值
            return value.toString();
        }
    }
} 