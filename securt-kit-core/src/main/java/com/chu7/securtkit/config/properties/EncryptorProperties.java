package com.chu7.securtkit.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加密器配置属性
 *
 * @author chu7
 * @date 2024/7/24 17:41
 */
@Data
@ConfigurationProperties(prefix = "securt-kit.encryptor")
public class EncryptorProperties {

    /**
     * 加密密钥
     */
    private String secretKey = "securt-kit-default-key-12345678";

    /**
     * 算法类型
     */
    private String algorithm = "DES";

    /**
     * 编码格式
     */
    private String charset = "UTF-8";

    /**
     * 加密模式
     */
    private String mode = "ECB";

    /**
     * 填充方式
     */
    private String padding = "PKCS5Padding";
}