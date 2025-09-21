package com.chu7.securtkit.dto;

import com.chu7.securtkit.annotation.FieldEncryptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段加密信息 DTO
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldEncryptorInfoDto {
    
    /**
     * 列名（查询结果中的字段名或别名）
     */
    private String columnName;
    
    /**
     * 源列名（实际数据库表中的字段名）
     */
    private String sourceColumn;
    
    /**
     * 源表名
     */
    private String sourceTableName;
    
    /**
     * 字段加密注解
     */
    private FieldEncryptor fieldEncryptor;
}