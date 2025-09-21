package com.chu7.securtkit.config.properties;

import com.chu7.securtkit.constants.NumberConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * securt-kit 配置属性
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
@Data
@ConfigurationProperties(prefix = "securt-kit")
public class SecurtKitProperties {
    
    /**
     * 扫描的实体类的包路径
     * 如需使用数据库加解密功能，需要配置
     */
    private List<String> scanEntityPackage = new ArrayList<>();
    
    /**
     * SQL语法解析的LRU缓存长度
     * 默认100
     */
    private Integer lruCapacity = NumberConstant.HUNDRED;
    
    /**
     * 加解密相关的配置
     */
    private EncryptorProperties encryptor = new EncryptorProperties();
}