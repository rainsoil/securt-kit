package com.chu7.securtkit.safety.core;

/**
 * 安全防护类型枚举
 * 
 * @author chu7
 * @date 2024/12/19
 */
public enum SecurityProtectionType {
    
    /**
     * XSS防护
     */
    XSS("xss", "XSS防护", null),
    
    /**
     * SQL注入防护
     */
    SQL_INJECTION("sql-injection", "SQL注入防护", null),
    
    /**
     * 敏感词过滤
     */
    SENSITIVE_WORD("sensitive-word", "敏感词过滤", null);
    
    private final String code;
    private final String name;
    private final String builtinKeywordFile;
    
    SecurityProtectionType(String code, String name, String builtinKeywordFile) {
        this.code = code;
        this.name = name;
        this.builtinKeywordFile = builtinKeywordFile;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getBuiltinKeywordFile() {
        return builtinKeywordFile;
    }
    
    /**
     * 根据代码获取防护类型
     * @param code 代码
     * @return 防护类型
     */
    public static SecurityProtectionType fromCode(String code) {
        for (SecurityProtectionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown security protection type: " + code);
    }
}
