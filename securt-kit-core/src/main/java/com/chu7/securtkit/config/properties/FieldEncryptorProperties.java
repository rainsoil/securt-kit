package com.chu7.securtkit.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 字段加密配置属性
 * 支持通过配置文件配置字段加密策略
 * 结构：表 -> 字段 -> 算法
 *
 * @author chu7
 * @date 2025/9/22 23:00
 */
@Data
@ConfigurationProperties(prefix = "securt-kit.field-encryptor")
public class FieldEncryptorProperties {

    /**
     * 是否启用配置文件字段加密（默认启用）
     */
    private boolean enabled = true;

    /**
     * 默认加密策略类名（当字段未配置时使用）
     */
    private String defaultStrategy = "com.chu7.securtkit.encryptor.pojo.DefaultPoJoFieldEncryptorPattern";

    /**
     * 表字段加密配置
     * key: 表名
     * value: 该表的字段加密配置
     */
    private Map<String, TableConfig> tables = new HashMap<>();

    /**
     * 表加密配置
     */
    @Data
    public static class TableConfig {
        
        /**
         * 表名
         */
        private String tableName;
        
        /**
         * 是否启用该表的字段加密（默认启用）
         */
        private boolean enabled = true;
        
        /**
         * 该表的字段加密配置
         * key: 字段名
         * value: 字段加密配置
         */
        private Map<String, FieldConfig> fields = new HashMap<>();
    }

    /**
     * 字段加密配置
     */
    @Data
    public static class FieldConfig {
        
        /**
         * 字段名
         */
        private String fieldName;
        
        /**
         * 加密策略类名（为空时使用默认策略）
         * 如果不配置此字段，表示该字段不加密
         */
        private String strategy;
        
        /**
         * 自定义参数（用于策略初始化）
         */
        private Map<String, Object> params = new HashMap<>();
    }
}
