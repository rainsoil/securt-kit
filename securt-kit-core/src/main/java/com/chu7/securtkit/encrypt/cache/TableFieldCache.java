package com.chu7.securtkit.encrypt.cache;

import com.chu7.securtkit.encrypt.annotation.EncryptField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表字段缓存管理
 * 缓存需要加密的表和字段信息
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
public class TableFieldCache {
    
    /**
     * 缓存：表名 -> 加密字段列表
     */
    private final Map<String, Set<String>> tableEncryptFields = new ConcurrentHashMap<>();
    
    /**
     * 缓存：类名 -> 表名
     */
    private final Map<String, String> classNameToTableName = new ConcurrentHashMap<>();
    
    /**
     * 缓存：表名 -> 是否包含加密字段
     */
    private final Map<String, Boolean> tableHasEncryptFields = new ConcurrentHashMap<>();
    
    /**
     * 添加表的加密字段信息
     *
     * @param tableName 表名
     * @param encryptFields 加密字段列表
     */
    public void addTableEncryptFields(String tableName, Set<String> encryptFields) {
        if (tableName != null && encryptFields != null && !encryptFields.isEmpty()) {
            tableEncryptFields.put(tableName, encryptFields);
            tableHasEncryptFields.put(tableName, true);
            log.debug("添加表加密字段缓存: {} -> {}", tableName, encryptFields);
        }
    }
    
    /**
     * 添加类名到表名的映射
     *
     * @param className 类名
     * @param tableName 表名
     */
    public void addClassNameToTableName(String className, String tableName) {
        if (className != null && tableName != null) {
            classNameToTableName.put(className, tableName);
            log.debug("添加类名表名映射: {} -> {}", className, tableName);
        }
    }
    
    /**
     * 获取表的加密字段列表
     *
     * @param tableName 表名
     * @return 加密字段列表
     */
    public Set<String> getTableEncryptFields(String tableName) {
        return tableEncryptFields.getOrDefault(tableName, Collections.emptySet());
    }
    
    /**
     * 根据类名获取表名
     *
     * @param className 类名
     * @return 表名
     */
    public String getTableNameByClassName(String className) {
        return classNameToTableName.get(className);
    }
    
    /**
     * 判断表是否包含加密字段
     *
     * @param tableName 表名
     * @return 是否包含加密字段
     */
    public boolean hasEncryptFields(String tableName) {
        return tableHasEncryptFields.getOrDefault(tableName, false);
    }
    
    /**
     * 判断字段是否需要加密
     *
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 是否需要加密
     */
    public boolean isFieldEncrypted(String tableName, String fieldName) {
        Set<String> encryptFields = getTableEncryptFields(tableName);
        return encryptFields.contains(fieldName);
    }
    
    /**
     * 获取所有需要加密的表名
     *
     * @return 表名集合
     */
    public Set<String> getAllEncryptTables() {
        return new HashSet<>(tableHasEncryptFields.keySet());
    }
    
    /**
     * 清除缓存
     */
    public void clearCache() {
        tableEncryptFields.clear();
        classNameToTableName.clear();
        tableHasEncryptFields.clear();
        log.info("清除表字段缓存");
    }
    
    /**
     * 清除指定表的缓存
     *
     * @param tableName 表名
     */
    public void clearTableCache(String tableName) {
        tableEncryptFields.remove(tableName);
        tableHasEncryptFields.remove(tableName);
        log.debug("清除表缓存: {}", tableName);
    }
    
    /**
     * 获取缓存统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTables", tableHasEncryptFields.size());
        stats.put("totalEncryptFields", tableEncryptFields.values().stream()
                .mapToInt(Set::size).sum());
        stats.put("totalClassMappings", classNameToTableName.size());
        return stats;
    }
}
