package com.chu7.securtkit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
public class ReflectUtils {
    
    /**
     * 获取类的所有字段（包括父类）
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        
        return fields;
    }
    
    /**
     * 获取类的所有非静态非final字段
     */
    public static List<Field> getNotStaticFinalFields(Class<?> clazz) {
        return getAllFields(clazz).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .collect(Collectors.toList());
    }
    
    /**
     * 设置字段值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("设置字段值失败: " + fieldName, e);
        }
    }
    
    /**
     * 获取字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("获取字段值失败: " + fieldName, e);
        }
    }
    
    /**
     * 根据字段值查找字段名
     */
    public static String getFieldNameByValue(Object obj, Object value) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                if (fieldValue != null && fieldValue.equals(value)) {
                    return field.getName();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("根据值查找字段名失败", e);
        }
        return null;
    }
    
    /**
     * 判断类是否存在指定字段
     */
    public static boolean hasField(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return hasField(clazz.getSuperclass(), fieldName);
            }
            return false;
        }
    }
}