package com.chu7.securtkit.encrypt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加密配置属性
 *
 * @author chu7
 * @date 2025/8/15
 */
@Data
@ConfigurationProperties(prefix = "securt-kit.encrypt")
public class EncryptProperties {
    
    /**
     * 是否启用加密功能
     */
    private boolean enabled = true;
    
    /**
     * 默认加密算法
     */
    private String algorithm = "AES";
    
    /**
     * 加密密钥
     */
    private String key;
    
    /**
     * 需要加密的字段配置
     * key: 表名，value: 字段列表
     */
    private Map<String, List<String>> fields = new HashMap<>();
    
    /**
     * 排除加密的表
     */
    private List<String> excludeTables = new ArrayList<>();
    
    /**
     * 扫描实体类包路径
     */
    private List<String> scanEntityPackages = new ArrayList<>();
    
    /**
     * 加密模式：DB(数据库函数) 或 POJO(Java库)
     */
    private String patternType = "DB";
    
    /**
     * 是否启用缓存
     */
    private boolean enableCache = true;
    
    /**
     * 缓存过期时间(秒)
     */
    private long cacheExpireSeconds = 3600;
}
