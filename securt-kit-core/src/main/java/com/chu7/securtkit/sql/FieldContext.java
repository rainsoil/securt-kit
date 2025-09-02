package com.chu7.securtkit.sql;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 字段上下文，存储字段的解析信息
 *
 * @author security-kit
 */
@Data
@Builder
public class FieldContext implements Serializable {
    
    /**
     * 表名（小写）
     */
    private String tableName;
    
    /**
     * 字段名（小写）
     */
    private String fieldName;
    
    /**
     * 字段别名
     */
    private String alias;
    
    /**
     * 字段位置类型
     */
    private FieldPosition position;
    
    /**
     * SQL类型
     */
    private SqlType sqlType;
    
    /**
     * 字段值（如果有）
     */
    private Object value;
    
    /**
     * 是否需要加密
     */
    private boolean needEncrypt;
    
    /**
     * 是否需要解密
     */
    private boolean needDecrypt;
    
    /**
     * 字段来源表名（用于子查询）
     */
    private String sourceTableName;
    
    /**
     * 是否来自真实表
     */
    private boolean fromSourceTable;
    
    /**
     * 字段位置枚举
     */
    public enum FieldPosition {
        SELECT,     // SELECT子句
        WHERE,      // WHERE条件
        INSERT,     // INSERT值
        UPDATE,     // UPDATE值
        JOIN,       // JOIN条件
        HAVING,     // HAVING条件
        ORDER_BY,   // ORDER BY
        GROUP_BY    // GROUP BY
    }
    
    /**
     * SQL类型枚举
     */
    public enum SqlType {
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }
} 