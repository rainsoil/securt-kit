package com.chu7.securtkit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 类缓存键
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasssCacheKey {
    
    /**
     * 类名
     */
    private String className;
    
    /**
     * 类加载器
     */
    private String classLoader;
    
    /**
     * 构建缓存键
     */
    public static ClasssCacheKey buildKey(Class<?> clazz) {
        return new ClasssCacheKey(
            clazz.getName(),
            clazz.getClassLoader() != null ? clazz.getClassLoader().toString() : "null"
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClasssCacheKey that = (ClasssCacheKey) o;
        return Objects.equals(className, that.className) &&
               Objects.equals(classLoader, that.classLoader);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(className, classLoader);
    }
}