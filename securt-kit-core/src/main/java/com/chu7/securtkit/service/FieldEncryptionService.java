package com.chu7.securtkit.service;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.cache.EncryptorInstanceCache;
import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import com.chu7.securtkit.util.ReflectUtils;
import com.chu7.securtkit.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 字段加密服务类
 * 提供统一的字段加密/解密服务，简化业务代码中的加密处理逻辑
 * 
 * @author chu7
 * @date 2025/9/22
 */
@Slf4j
@Service
public class FieldEncryptionService {

    // 配置属性，预留用于未来扩展
    // @Autowired
    // private FieldEncryptorProperties fieldEncryptorProperties;

    /**
     * 加密单个对象
     * 
     * @param obj 待加密的对象
     * @param <T> 对象类型
     * @return 加密后的对象
     */
    public <T> T encryptObject(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            // 创建对象副本，避免修改原对象
            T encryptedObj = createObjectCopy(obj);
            
            // 获取需要加密的字段
            List<Field> encryptFields = getEncryptFields(obj.getClass());
            
            if (CollectionUtils.isEmpty(encryptFields)) {
                log.debug("对象 {} 没有需要加密的字段", obj.getClass().getSimpleName());
                return encryptedObj;
            }

            // 逐个加密字段
            for (Field field : encryptFields) {
                encryptField(encryptedObj, field);
            }

            log.debug("成功加密对象 {} 的 {} 个字段", obj.getClass().getSimpleName(), encryptFields.size());
            return encryptedObj;

        } catch (Exception e) {
            log.error("加密对象失败: {}", obj.getClass().getSimpleName(), e);
            throw new RuntimeException("加密对象失败", e);
        }
    }

    /**
     * 解密单个对象
     * 
     * @param obj 待解密的对象
     * @param <T> 对象类型
     * @return 解密后的对象
     */
    public <T> T decryptObject(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            // 创建对象副本，避免修改原对象
            T decryptedObj = createObjectCopy(obj);
            
            // 获取需要解密的字段
            List<Field> encryptFields = getEncryptFields(obj.getClass());
            
            if (CollectionUtils.isEmpty(encryptFields)) {
                log.debug("对象 {} 没有需要解密的字段", obj.getClass().getSimpleName());
                return decryptedObj;
            }

            // 逐个解密字段
            for (Field field : encryptFields) {
                decryptField(decryptedObj, field);
            }

            log.debug("成功解密对象 {} 的 {} 个字段", obj.getClass().getSimpleName(), encryptFields.size());
            return decryptedObj;

        } catch (Exception e) {
            log.error("解密对象失败: {}", obj.getClass().getSimpleName(), e);
            throw new RuntimeException("解密对象失败", e);
        }
    }

    /**
     * 批量加密对象列表
     * 
     * @param objects 待加密的对象列表
     * @param <T> 对象类型
     * @return 加密后的对象列表
     */
    public <T> List<T> encryptObjects(List<T> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return objects;
        }

        try {
            List<T> encryptedObjects = new ArrayList<>();
            for (T obj : objects) {
                encryptedObjects.add(encryptObject(obj));
            }
            
            log.debug("成功批量加密 {} 个对象", objects.size());
            return encryptedObjects;

        } catch (Exception e) {
            log.error("批量加密对象失败", e);
            throw new RuntimeException("批量加密对象失败", e);
        }
    }

    /**
     * 批量解密对象列表
     * 
     * @param objects 待解密的对象列表
     * @param <T> 对象类型
     * @return 解密后的对象列表
     */
    public <T> List<T> decryptObjects(List<T> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return objects;
        }

        try {
            List<T> decryptedObjects = new ArrayList<>();
            for (T obj : objects) {
                decryptedObjects.add(decryptObject(obj));
            }
            
            log.debug("成功批量解密 {} 个对象", objects.size());
            return decryptedObjects;

        } catch (Exception e) {
            log.error("批量解密对象失败", e);
            throw new RuntimeException("批量解密对象失败", e);
        }
    }

    /**
     * 加密Map中的字段
     * 
     * @param map 待加密的Map
     * @param fieldMappings 字段映射关系，key为Map的key，value为加密策略类
     * @return 加密后的Map
     */
    public Map<String, Object> encryptMap(Map<String, Object> map, Map<String, Class<? extends FieldEncryptorStrategy<String>>> fieldMappings) {
        if (map == null || CollectionUtils.isEmpty(fieldMappings)) {
            return map;
        }

        try {
            Map<String, Object> encryptedMap = new HashMap<>(map);
            
            for (Map.Entry<String, Class<? extends FieldEncryptorStrategy<String>>> entry : fieldMappings.entrySet()) {
                String fieldKey = entry.getKey();
                Class<? extends FieldEncryptorStrategy<String>> strategyClass = entry.getValue();
                
                Object value = encryptedMap.get(fieldKey);
                if (value instanceof String) {
                    String encryptedValue = encryptString((String) value, strategyClass);
                    encryptedMap.put(fieldKey, encryptedValue);
                }
            }
            
            log.debug("成功加密Map中的 {} 个字段", fieldMappings.size());
            return encryptedMap;

        } catch (Exception e) {
            log.error("加密Map失败", e);
            throw new RuntimeException("加密Map失败", e);
        }
    }

    /**
     * 解密Map中的字段
     * 
     * @param map 待解密的Map
     * @param fieldMappings 字段映射关系，key为Map的key，value为解密策略类
     * @return 解密后的Map
     */
    public Map<String, Object> decryptMap(Map<String, Object> map, Map<String, Class<? extends FieldEncryptorStrategy<String>>> fieldMappings) {
        if (map == null || CollectionUtils.isEmpty(fieldMappings)) {
            return map;
        }

        try {
            Map<String, Object> decryptedMap = new HashMap<>(map);
            
            for (Map.Entry<String, Class<? extends FieldEncryptorStrategy<String>>> entry : fieldMappings.entrySet()) {
                String fieldKey = entry.getKey();
                Class<? extends FieldEncryptorStrategy<String>> strategyClass = entry.getValue();
                
                Object value = decryptedMap.get(fieldKey);
                if (value instanceof String) {
                    String decryptedValue = decryptString((String) value, strategyClass);
                    decryptedMap.put(fieldKey, decryptedValue);
                }
            }
            
            log.debug("成功解密Map中的 {} 个字段", fieldMappings.size());
            return decryptedMap;

        } catch (Exception e) {
            log.error("解密Map失败", e);
            throw new RuntimeException("解密Map失败", e);
        }
    }

    /**
     * 获取需要加密的字段列表
     * 优先从TableCache中获取配置，如果没有则回退到注解扫描
     * 
     * @param clazz 类
     * @return 需要加密的字段列表
     */
    private List<Field> getEncryptFields(Class<?> clazz) {
        // 1. 首先尝试从TableCache中获取配置
        String tableName = getTableName(clazz);
        if (StringUtils.isNotBlank(tableName)) {
            Map<String, FieldEncryptor> tableFieldConfig = TableCache.getTableFieldEncryptInfo(tableName);
            if (!tableFieldConfig.isEmpty()) {
                return getEncryptFieldsFromTableCache(clazz, tableFieldConfig);
            }
        }
        
        // 2. 如果TableCache中没有配置，则回退到注解扫描
        return getEncryptFieldsFromAnnotations(clazz);
    }

    /**
     * 从TableCache配置中获取需要加密的字段
     * 
     * @param clazz 类
     * @param tableFieldConfig 表字段配置
     * @return 需要加密的字段列表
     */
    private List<Field> getEncryptFieldsFromTableCache(Class<?> clazz, Map<String, FieldEncryptor> tableFieldConfig) {
        List<Field> allFields = ReflectUtils.getNotStaticFinalFields(clazz);
        List<Field> encryptFields = new ArrayList<>();
        
        for (Field field : allFields) {
            String fieldName = field.getName();
            String columnName = getColumnName(field);
            
            // 检查字段名或列名是否在配置中
            if (tableFieldConfig.containsKey(fieldName.toLowerCase()) || 
                tableFieldConfig.containsKey(columnName.toLowerCase())) {
                encryptFields.add(field);
                log.debug("从TableCache配置中找到加密字段: {}.{}", clazz.getSimpleName(), fieldName);
            }
        }
        
        return encryptFields;
    }

    /**
     * 从注解中获取需要加密的字段（回退方案）
     * 
     * @param clazz 类
     * @return 需要加密的字段列表
     */
    private List<Field> getEncryptFieldsFromAnnotations(Class<?> clazz) {
        List<Field> allFields = ReflectUtils.getNotStaticFinalFields(clazz);
        
        return allFields.stream()
                .filter(field -> field.isAnnotationPresent(FieldEncryptor.class))
                .collect(Collectors.toList());
    }

    /**
     * 获取表名
     * 
     * @param clazz 类
     * @return 表名
     */
    private String getTableName(Class<?> clazz) {
        TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
        if (tableNameAnnotation != null && StringUtils.isNotBlank(tableNameAnnotation.value())) {
            return tableNameAnnotation.value().toLowerCase();
        }
        return null;
    }

    /**
     * 获取字段对应的数据库列名
     * 
     * @param field 字段
     * @return 列名
     */
    private String getColumnName(Field field) {
        com.baomidou.mybatisplus.annotation.TableField tableField = 
            field.getAnnotation(com.baomidou.mybatisplus.annotation.TableField.class);
        if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
            return tableField.value();
        }
        // 如果没有指定列名，则使用字段名（转换为下划线格式）
        return camelToUnderscore(field.getName());
    }

    /**
     * 驼峰转下划线
     * 
     * @param camelStr 驼峰字符串
     * @return 下划线字符串
     */
    private String camelToUnderscore(String camelStr) {
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
     * 获取字段的加密策略
     * 优先从TableCache中获取，如果没有则回退到注解
     * 
     * @param clazz 类
     * @param field 字段
     * @return 字段加密策略
     */
    private FieldEncryptor getFieldEncryptor(Class<?> clazz, Field field) {
        // 1. 首先尝试从TableCache中获取
        String tableName = getTableName(clazz);
        if (StringUtils.isNotBlank(tableName)) {
            String fieldName = field.getName();
            String columnName = getColumnName(field);
            
            // 尝试通过字段名获取
            FieldEncryptor fieldEncryptor = TableCache.getFieldEncryptor(tableName, fieldName);
            if (fieldEncryptor != null) {
                log.debug("从TableCache中获取字段加密策略: {}.{}", clazz.getSimpleName(), fieldName);
                return fieldEncryptor;
            }
            
            // 尝试通过列名获取
            fieldEncryptor = TableCache.getFieldEncryptor(tableName, columnName);
            if (fieldEncryptor != null) {
                log.debug("从TableCache中获取字段加密策略: {}.{} (列名: {})", clazz.getSimpleName(), fieldName, columnName);
                return fieldEncryptor;
            }
        }
        
        // 2. 如果TableCache中没有，则回退到注解
        FieldEncryptor annotation = field.getAnnotation(FieldEncryptor.class);
        if (annotation != null) {
            log.debug("从注解中获取字段加密策略: {}.{}", clazz.getSimpleName(), field.getName());
            return annotation;
        }
        
        return null;
    }

    /**
     * 加密字段
     * 
     * @param obj 对象
     * @param field 字段
     */
    private void encryptField(Object obj, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(obj);
            
            if (value instanceof String) {
                // 获取加密策略
                FieldEncryptor fieldEncryptor = getFieldEncryptor(obj.getClass(), field);
                if (fieldEncryptor != null) {
                    @SuppressWarnings("unchecked")
                    Class<? extends FieldEncryptorStrategy<String>> strategyClass = 
                        (Class<? extends FieldEncryptorStrategy<String>>) fieldEncryptor.value();
                    
                    String encryptedValue = encryptString((String) value, strategyClass);
                    field.set(obj, encryptedValue);
                    
                    log.debug("成功加密字段: {}.{}", obj.getClass().getSimpleName(), field.getName());
                } else {
                    log.warn("未找到字段加密策略: {}.{}", obj.getClass().getSimpleName(), field.getName());
                }
            }
            
        } catch (Exception e) {
            log.error("加密字段失败: {}.{}", obj.getClass().getSimpleName(), field.getName(), e);
            throw new RuntimeException("加密字段失败", e);
        }
    }

    /**
     * 解密字段
     * 
     * @param obj 对象
     * @param field 字段
     */
    private void decryptField(Object obj, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(obj);
            
            if (value instanceof String) {
                // 获取解密策略
                FieldEncryptor fieldEncryptor = getFieldEncryptor(obj.getClass(), field);
                if (fieldEncryptor != null) {
                    @SuppressWarnings("unchecked")
                    Class<? extends FieldEncryptorStrategy<String>> strategyClass = 
                        (Class<? extends FieldEncryptorStrategy<String>>) fieldEncryptor.value();
                    
                    String decryptedValue = decryptString((String) value, strategyClass);
                    field.set(obj, decryptedValue);
                    
                    log.debug("成功解密字段: {}.{}", obj.getClass().getSimpleName(), field.getName());
                } else {
                    log.warn("未找到字段解密策略: {}.{}", obj.getClass().getSimpleName(), field.getName());
                }
            }
            
        } catch (Exception e) {
            log.error("解密字段失败: {}.{}", obj.getClass().getSimpleName(), field.getName(), e);
            throw new RuntimeException("解密字段失败", e);
        }
    }

    /**
     * 加密字符串
     * 
     * @param plaintext 明文
     * @param strategyClass 加密策略类
     * @return 密文
     */
    private String encryptString(String plaintext, Class<? extends FieldEncryptorStrategy<String>> strategyClass) {
        if (plaintext == null) {
            return null;
        }

        try {
            FieldEncryptorStrategy<String> strategy = EncryptorInstanceCache.getInstance(strategyClass);
            return strategy.encryption(plaintext);
        } catch (Exception e) {
            log.error("加密字符串失败，使用策略: {}", strategyClass.getSimpleName(), e);
            throw new RuntimeException("加密字符串失败", e);
        }
    }

    /**
     * 解密字符串
     * 
     * @param ciphertext 密文
     * @param strategyClass 解密策略类
     * @return 明文
     */
    private String decryptString(String ciphertext, Class<? extends FieldEncryptorStrategy<String>> strategyClass) {
        if (ciphertext == null) {
            return null;
        }

        try {
            FieldEncryptorStrategy<String> strategy = EncryptorInstanceCache.getInstance(strategyClass);
            return strategy.decryption(ciphertext);
        } catch (Exception e) {
            log.error("解密字符串失败，使用策略: {}", strategyClass.getSimpleName(), e);
            throw new RuntimeException("解密字符串失败", e);
        }
    }

    /**
     * 创建对象副本
     * 
     * @param obj 原对象
     * @param <T> 对象类型
     * @return 对象副本
     */
    @SuppressWarnings("unchecked")
    private <T> T createObjectCopy(T obj) {
        try {
            // 简单的浅拷贝实现，实际项目中可以使用更复杂的拷贝工具
            Class<?> clazz = obj.getClass();
            T copy = (T) clazz.newInstance();
            
            // 复制所有字段值
            List<Field> fields = ReflectUtils.getNotStaticFinalFields(clazz);
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                field.set(copy, value);
            }
            
            return copy;
        } catch (Exception e) {
            log.error("创建对象副本失败: {}", obj.getClass().getSimpleName(), e);
            throw new RuntimeException("创建对象副本失败", e);
        }
    }

    /**
     * 检查对象是否有需要加密的字段
     * 
     * @param clazz 类
     * @return 是否有需要加密的字段
     */
    public boolean hasEncryptFields(Class<?> clazz) {
        return !getEncryptFields(clazz).isEmpty();
    }

    /**
     * 获取对象中需要加密的字段数量
     * 
     * @param clazz 类
     * @return 需要加密的字段数量
     */
    public int getEncryptFieldCount(Class<?> clazz) {
        return getEncryptFields(clazz).size();
    }

    /**
     * 获取对象中需要加密的字段名称列表
     * 
     * @param clazz 类
     * @return 需要加密的字段名称列表
     */
    public List<String> getEncryptFieldNames(Class<?> clazz) {
        return getEncryptFields(clazz).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}
