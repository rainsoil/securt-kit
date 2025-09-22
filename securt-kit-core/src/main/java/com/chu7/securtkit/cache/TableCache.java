package com.chu7.securtkit.cache;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.util.ReflectUtils;
import com.chu7.securtkit.util.StringUtils;
import com.chu7.securtkit.util.CollectionUtils;
import com.chu7.securtkit.util.ClassScannerUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表缓存，用于缓存表和字段的映射关系
 *
 * @author chu7
 * @date 2025/6/24 17:58
 */
@Slf4j
public class TableCache {

    /**
     * 表字段加密信息缓存
     * key: 表名（小写）
     * value: Map<字段名（小写）, FieldEncryptor注解>
     */
    private static final Map<String, Map<String, FieldEncryptor>> TABLE_FIELD_ENCRYPT_INFO = new ConcurrentHashMap<>();

    /**
     * 需要加密的表名集合（小写）
     */
    private static final Set<String> FIELD_ENCRYPT_TABLE = new HashSet<>();

    /**
     * 初始化表缓存（通过包名扫描）
     *
     * @param scanPackages 扫描包路径列表
     */
    public static void initByPackages(List<String> scanPackages) {
        if (CollectionUtils.isEmpty(scanPackages)) {
            log.warn("【securt-kit】未配置实体类扫描包路径，跳过表缓存初始化");
            return;
        }
        
        Set<Class<?>> entityClasses = ClassScannerUtil.scanMultiplePackages(
            scanPackages.toArray(new String[0]), 
            TableName.class
        );
        
        init(entityClasses);
    }

    /**
     * 初始化表缓存
     *
     * @param entityClasses 实体类集合
     */
    public static void init(Set<Class<?>> entityClasses) {
        for (Class<?> entityClass : entityClasses) {
            processEntityClass(entityClass);
        }
        log.info("【securt-kit】表缓存初始化完成，共缓存 {} 张表", TABLE_FIELD_ENCRYPT_INFO.size());
    }

    /**
     * 通过配置文件初始化表缓存
     *
     * @param fieldEncryptorProperties 字段加密配置属性
     */
    public static void initByConfig(com.chu7.securtkit.config.properties.FieldEncryptorProperties fieldEncryptorProperties) {
        if (fieldEncryptorProperties == null || !fieldEncryptorProperties.isEnabled()) {
            log.warn("【securt-kit】字段加密配置未启用，跳过配置文件初始化");
            return;
        }
        
        Map<String, com.chu7.securtkit.config.properties.FieldEncryptorProperties.TableConfig> tables = 
            fieldEncryptorProperties.getTables();
        
        if (tables == null || tables.isEmpty()) {
            log.warn("【securt-kit】未配置表字段加密信息，跳过配置文件初始化");
            return;
        }
        
        String defaultStrategy = fieldEncryptorProperties.getDefaultStrategy();
        log.info("【securt-kit】默认加密策略: {}", defaultStrategy);
        
        for (Map.Entry<String, com.chu7.securtkit.config.properties.FieldEncryptorProperties.TableConfig> tableEntry : tables.entrySet()) {
            String tableName = tableEntry.getKey().toLowerCase();
            com.chu7.securtkit.config.properties.FieldEncryptorProperties.TableConfig tableConfig = tableEntry.getValue();
            
            if (!tableConfig.isEnabled()) {
                log.info("【securt-kit】表 {} 的字段加密已禁用", tableName);
                continue;
            }
            
            Map<String, com.chu7.securtkit.config.properties.FieldEncryptorProperties.FieldConfig> fields = 
                tableConfig.getFields();
            
            if (fields == null || fields.isEmpty()) {
                log.info("【securt-kit】表 {} 未配置字段加密信息", tableName);
                continue;
            }
            
            for (Map.Entry<String, com.chu7.securtkit.config.properties.FieldEncryptorProperties.FieldConfig> fieldEntry : fields.entrySet()) {
                String fieldName = fieldEntry.getKey().toLowerCase();
                com.chu7.securtkit.config.properties.FieldEncryptorProperties.FieldConfig fieldConfig = fieldEntry.getValue();
                
                // 使用字段配置的策略，如果为空则使用默认策略
                String strategy = fieldConfig.getStrategy();
                if (strategy == null || strategy.trim().isEmpty()) {
                    strategy = defaultStrategy;
                }
                
                // 创建FieldEncryptor注解对象
                FieldEncryptor fieldEncryptor = createFieldEncryptorFromConfig(strategy, fieldConfig.getParams());
                if (fieldEncryptor != null) {
                    TABLE_FIELD_ENCRYPT_INFO.computeIfAbsent(tableName, k -> new HashMap<>())
                            .put(fieldName, fieldEncryptor);
                    FIELD_ENCRYPT_TABLE.add(tableName);
                    log.info("【securt-kit】配置文件添加字段加密: {}.{} -> {}", tableName, fieldName, strategy);
                }
            }
        }
        
        log.info("【securt-kit】配置文件表缓存初始化完成，共缓存 {} 张表", TABLE_FIELD_ENCRYPT_INFO.size());
    }

    /**
     * 处理单个实体类
     */
    private static void processEntityClass(Class<?> entityClass) {
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null) {
            return;
        }

        String tableName = tableNameAnnotation.value().toLowerCase();
        Map<String, FieldEncryptor> fieldEncryptorMap = new HashMap<>();

        // 获取类的所有字段
        List<Field> allFields = ReflectUtils.getAllFields(entityClass);

        // 过滤掉不属于实体类的字段，过滤掉static修饰的字段
        List<Field> entityFields = allFields.stream()
                .filter(f -> f.getAnnotation(TableField.class) == null || f.getAnnotation(TableField.class).exist())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toList());

        // 解析字段对应数据库字段名和标注的加解密注解
        for (Field field : entityFields) {
            FieldEncryptor fieldEncryptor = field.getAnnotation(FieldEncryptor.class);
            if (fieldEncryptor != null) {
                String columnName = getColumnName(field).toLowerCase();
                fieldEncryptorMap.put(columnName, fieldEncryptor);
            }
        }

        if (!fieldEncryptorMap.isEmpty()) {
            TABLE_FIELD_ENCRYPT_INFO.put(tableName, fieldEncryptorMap);
            FIELD_ENCRYPT_TABLE.add(tableName);
        }
    }

    /**
     * 获取字段对应的数据库列名
     */
    private static String getColumnName(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
            return tableField.value();
        }
        // 如果没有指定列名，则使用字段名（转换为下划线格式）
        return camelToUnderscore(field.getName());
    }

    /**
     * 驼峰转下划线
     */
    private static String camelToUnderscore(String camelStr) {
        if (StringUtils.isBlank(camelStr)) {
            return camelStr;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelStr.length(); i++) {
            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 获取表字段加密信息
     */
    public static Map<String, Map<String, FieldEncryptor>> getTableFieldEncryptInfo() {
        return new HashMap<>(TABLE_FIELD_ENCRYPT_INFO);
    }

    /**
     * 获取需要加密的表名集合
     */
    public static Set<String> getFieldEncryptTable() {
        return new HashSet<>(FIELD_ENCRYPT_TABLE);
    }

    /**
     * 获取指定表的字段加密信息
     */
    public static Map<String, FieldEncryptor> getTableFieldEncryptInfo(String tableName) {
        return TABLE_FIELD_ENCRYPT_INFO.getOrDefault(tableName.toLowerCase(), new HashMap<>());
    }

    /**
     * 获取指定表字段的加密注解
     */
    public static FieldEncryptor getFieldEncryptor(String tableName, String columnName) {
        Map<String, FieldEncryptor> fieldMap = getTableFieldEncryptInfo(tableName);
        return fieldMap.get(columnName.toLowerCase());
    }

    /**
     * 从配置文件创建FieldEncryptor注解对象
     */
    @SuppressWarnings("unchecked")
    private static FieldEncryptor createFieldEncryptorFromConfig(String strategy, Map<String, Object> params) {
        try {
            Class<?> strategyClass = Class.forName(strategy);
            return new FieldEncryptor() {
                @Override
                public Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<?>> value() {
                    return (Class<? extends com.chu7.securtkit.strategy.FieldEncryptorStrategy<?>>) strategyClass;
                }
                
                @Override
                public Class<? extends java.lang.annotation.Annotation> annotationType() {
                    return FieldEncryptor.class;
                }
            };
        } catch (ClassNotFoundException e) {
            log.error("【securt-kit】找不到加密策略类: {}", strategy, e);
            return null;
        }
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        TABLE_FIELD_ENCRYPT_INFO.clear();
        FIELD_ENCRYPT_TABLE.clear();
    }
}