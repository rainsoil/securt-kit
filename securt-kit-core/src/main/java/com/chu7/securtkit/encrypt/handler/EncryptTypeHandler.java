package com.chu7.securtkit.encrypt.handler;

import com.chu7.securtkit.encrypt.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加密字段类型处理器
 * 用于MyBatis处理加密字段的类型转换
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@MappedTypes(String.class)
public class EncryptTypeHandler extends BaseTypeHandler<String> {
    
    @Autowired
    private EncryptUtil encryptUtil;
    
    /**
     * 加密算法
     */
    private String algorithm = "AES";
    
    /**
     * 是否启用加密
     */
    private boolean enabled = true;
    
    public EncryptTypeHandler() {}
    
    public EncryptTypeHandler(String algorithm) {
        this.algorithm = algorithm;
    }
    
    public EncryptTypeHandler(String algorithm, boolean enabled) {
        this.algorithm = algorithm;
        this.enabled = enabled;
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        if (enabled && parameter != null) {
            String encryptedValue = encryptUtil.encrypt(parameter, algorithm);
            ps.setString(i, encryptedValue);
            log.debug("设置加密参数: {} -> {}", parameter, encryptedValue);
        } else {
            ps.setString(i, parameter);
        }
    }
    
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (enabled && value != null) {
            String decryptedValue = encryptUtil.decrypt(value, algorithm);
            log.debug("获取解密结果: {} -> {}", value, decryptedValue);
            return decryptedValue;
        }
        return value;
    }
    
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (enabled && value != null) {
            String decryptedValue = encryptUtil.decrypt(value, algorithm);
            log.debug("获取解密结果: {} -> {}", value, decryptedValue);
            return decryptedValue;
        }
        return value;
    }
    
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (enabled && value != null) {
            String decryptedValue = encryptUtil.decrypt(value, algorithm);
            log.debug("获取解密结果: {} -> {}", value, decryptedValue);
            return decryptedValue;
        }
        return value;
    }
    
    /**
     * 设置加密算法
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    /**
     * 设置是否启用加密
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * 获取加密算法
     */
    public String getAlgorithm() {
        return algorithm;
    }
    
    /**
     * 是否启用加密
     */
    public boolean isEnabled() {
        return enabled;
    }
} 