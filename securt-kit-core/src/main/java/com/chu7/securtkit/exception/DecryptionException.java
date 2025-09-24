package com.chu7.securtkit.exception;

/**
 * 解密异常
 *
 * @author chu7
 * @date 2025/9/24
 */
public class DecryptionException extends SecurtKitException {

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecryptionException(Throwable cause) {
        super(cause);
    }
}
