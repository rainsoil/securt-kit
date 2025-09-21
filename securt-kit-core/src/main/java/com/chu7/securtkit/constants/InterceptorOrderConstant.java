package com.chu7.securtkit.constants;

/**
 * 拦截器顺序常量
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
public class InterceptorOrderConstant {
    
    /**
     * 加密器拦截器顺序
     */
    public static final int ENCRYPTOR = 100;
    
    /**
     * 脱敏拦截器顺序
     */
    public static final int DESENSITIZE = 200;
    
    /**
     * 数据隔离拦截器顺序
     */
    public static final int ISOLATION = 300;
    
    /**
     * 默认值拦截器顺序
     */
    public static final int FIELD_DEFAULT = 400;
    
    /**
     * 语法转换拦截器顺序
     */
    public static final int TRANSFORMATION = 500;
}