package com.chu7.securtkit.safety;

/**
 * 当策略配置为 block 时抛出的拦截异常。
 */
public class SafetyBlockException extends RuntimeException {
    public SafetyBlockException(String message) { super(message); }
}


