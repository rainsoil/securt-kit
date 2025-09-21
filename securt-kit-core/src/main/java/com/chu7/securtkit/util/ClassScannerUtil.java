package com.chu7.securtkit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * 类扫描工具
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
@Slf4j
public class ClassScannerUtil {
    
    private static final String RESOURCE_PATTERN = "/**/*.class";
    
    /**
     * 扫描指定包下带有指定注解的类
     * 
     * @param basePackage 基础包路径
     * @param annotation 注解类
     * @return 类集合
     */
    public static Set<Class<?>> scan(String basePackage, Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();
        
        try {
            // 将包名转换为路径
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + RESOURCE_PATTERN;
            
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    try {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotation)) {
                            classes.add(clazz);
                        }
                    } catch (Exception e) {
                        log.debug("【securt-kit】扫描类失败: {}", resource.getFilename(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("【securt-kit】扫描包失败: {}", basePackage, e);
        }
        
        log.info("【securt-kit】扫描包 {} 找到 {} 个带有 @{} 注解的类", 
                basePackage, classes.size(), annotation.getSimpleName());
        return classes;
    }
    
    /**
     * 解析基础包名
     */
    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }
    
    /**
     * 扫描多个包
     */
    public static Set<Class<?>> scanMultiplePackages(String[] basePackages, Class<? extends Annotation> annotation) {
        Set<Class<?>> allClasses = new HashSet<>();
        
        for (String basePackage : basePackages) {
            if (basePackage != null && !basePackage.trim().isEmpty()) {
                Set<Class<?>> classes = scan(basePackage, annotation);
                allClasses.addAll(classes);
            }
        }
        
        return allClasses;
    }
}