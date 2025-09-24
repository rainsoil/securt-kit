package com.chu7.securtkit.exception;

/**
 * 配置异常
 *
 * @author chu7
 * @date 2025/9/24
 */
public class ConfigurationException extends SecurtKitException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
