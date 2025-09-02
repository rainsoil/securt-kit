package com.chu7.securtkit.sql;

import lombok.Builder;
import lombok.Data;
import net.sf.jsqlparser.statement.Statement;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SQL分析结果，存储SQL解析的完整信息
 *
 * @author security-kit
 */
@Data
@Builder
public class SqlAnalysisResult {
    
    /**
     * 原始SQL
     */
    private String originalSql;
    
    /**
     * 解析后的Statement对象
     */
    private Statement statement;
    
    /**
     * SQL类型
     */
    private FieldContext.SqlType sqlType;
    
    /**
     * 所有字段上下文
     */
    private List<FieldContext> fieldContexts;
    
    /**
     * 表字段映射（表名 -> 字段列表）
     */
    private Map<String, List<FieldContext>> tableFieldMap;
    
    /**
     * 需要加密的字段
     */
    private Set<String> encryptFields;
    
    /**
     * 需要解密的字段
     */
    private Set<String> decryptFields;
    
    /**
     * 占位符映射（占位符 -> 字段上下文）
     */
    private Map<String, FieldContext> placeholderMap;
    
    /**
     * 是否需要进行处理
     */
    private boolean needProcess;
    
    /**
     * 解析是否成功
     */
    private boolean parseSuccess;
    
    /**
     * 错误信息（如果解析失败）
     */
    private String errorMessage;
} 