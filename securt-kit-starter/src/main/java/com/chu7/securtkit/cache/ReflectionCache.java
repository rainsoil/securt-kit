package com.chu7.securtkit.cache;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.annotation.PoJoResultEncryptor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射缓存类，用于缓存反射相关的数据以提高性能
 *
 * @author chu7
 * @date 2025/9/24
 */
public class ReflectionCache {

    private static final ConcurrentHashMap<Class<?>, List<Field>> FIELDS_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, TableName> TABLE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Field, FieldEncryptor> FIELD_ENCRYPTOR_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Field, PoJoResultEncryptor> POJO_RESULT_ENCRYPTOR_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取类的所有字段（使用缓存）
     */
    public static List<Field> getFields(Class<?> clazz) {
        return FIELDS_CACHE.computeIfAbsent(clazz, k -> {
            Field[] fieldArray = cn.hutool.core.util.ReflectUtil.getFields(clazz);
            return java.util.Arrays.asList(fieldArray);
        });
    }

    /**
     * 获取类的TableName注解（使用缓存）
     */
    public static TableName getTableName(Class<?> clazz) {
        return TABLE_NAME_CACHE.computeIfAbsent(clazz, k -> clazz.getAnnotation(TableName.class));
    }

    /**
     * 获取字段的FieldEncryptor注解（使用缓存）
     */
    public static FieldEncryptor getFieldEncryptorAnnotation(Field field) {
        return FIELD_ENCRYPTOR_CACHE.computeIfAbsent(field, k -> field.getAnnotation(FieldEncryptor.class));
    }

    /**
     * 获取字段的PoJoResultEncryptor注解（使用缓存）
     */
    public static PoJoResultEncryptor getPoJoResultEncryptorAnnotation(Field field) {
        return POJO_RESULT_ENCRYPTOR_CACHE.computeIfAbsent(field, k -> field.getAnnotation(PoJoResultEncryptor.class));
    }

    /**
     * 获取需要加密的字段列表（使用缓存）
     */
    public static List<Field> getEncryptFields(Class<?> clazz) {
        List<Field> allFields = getFields(clazz);
        return allFields.stream()
                .filter(field -> getFieldEncryptorAnnotation(field) != null)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 清理缓存
     */
    public static void clearCache() {
        FIELDS_CACHE.clear();
        TABLE_NAME_CACHE.clear();
        FIELD_ENCRYPTOR_CACHE.clear();
        POJO_RESULT_ENCRYPTOR_CACHE.clear();
    }
}
