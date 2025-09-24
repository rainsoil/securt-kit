package com.chu7.securtkit.exception;

/**
 * 加密异常
 *
 * @author chu7
 * @date 2025/9/24
 */
public class EncryptionException extends SecurtKitException {

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionException(Throwable cause) {
        super(cause);
    }
}
