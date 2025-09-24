package com.chu7.securtkit.exception;

/**
 * securt-kit框架基础异常类
 *
 * @author chu7
 * @date 2025/9/24
 */
public class SecurtKitException extends RuntimeException {

    public SecurtKitException(String message) {
        super(message);
    }

    public SecurtKitException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurtKitException(Throwable cause) {
        super(cause);
    }
}
