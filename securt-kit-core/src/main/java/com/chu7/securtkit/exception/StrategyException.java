package com.chu7.securtkit.exception;

/**
 * 策略异常
 *
 * @author chu7
 * @date 2025/9/24
 */
public class StrategyException extends SecurtKitException {

    public StrategyException(String message) {
        super(message);
    }

    public StrategyException(String message, Throwable cause) {
        super(message, cause);
    }

    public StrategyException(Throwable cause) {
        super(cause);
    }
}
