package com.chu7.securtkit.encrypt.visitor;

import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.config.EncryptProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库加解密语句访问者
 * 使用正则表达式处理SQL语句，实现字段的自动加解密
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
public class DbEncryptStatementVisitor {
    
    @Autowired
    private TableFieldCache tableFieldCache;
    
    @Autowired
    private EncryptProperties encryptProperties;
    
    private String resultSql;
    private boolean hasChanges = false;
    
    // 正则表达式模式
    private static final Pattern SELECT_PATTERN = Pattern.compile(
        "SELECT\\s+(.*?)\\s+FROM\\s+(\\w+)(?:\\s+AS\\s+(\\w+))?", 
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    private static final Pattern WHERE_PATTERN = Pattern.compile(
        "WHERE\\s+(.*?)(?:ORDER\\s+BY|GROUP\\s+BY|LIMIT|$)", 
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    /**
     * 处理SQL语句
     *
     * @param sql 原始SQL
     * @return 处理后的SQL
     */
    public String processSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }
        
        reset();
        
        try {
            // 检查是否包含需要加密的表
            Set<String> encryptTables = tableFieldCache.getAllEncryptTables();
            boolean needProcess = false;
            
            for (String tableName : encryptTables) {
                if (sql.toLowerCase().contains(tableName.toLowerCase())) {
                    needProcess = true;
                    break;
                }
            }
            
            if (!needProcess) {
                return sql;
            }
            
            // 处理SELECT语句
            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                resultSql = processSelectSql(sql);
            }
            // 处理INSERT语句
            else if (sql.trim().toUpperCase().startsWith("INSERT")) {
                resultSql = processInsertSql(sql);
            }
            // 处理UPDATE语句
            else if (sql.trim().toUpperCase().startsWith("UPDATE")) {
                resultSql = processUpdateSql(sql);
            }
            // 处理DELETE语句
            else if (sql.trim().toUpperCase().startsWith("DELETE")) {
                resultSql = processDeleteSql(sql);
            }
            
            return hasChanges ? resultSql : sql;
            
        } catch (Exception e) {
            log.error("处理SQL失败: {}", sql, e);
            return sql;
        }
    }
    
    /**
     * 处理SELECT语句
     */
    private String processSelectSql(String sql) {
        String processedSql = sql;
        
        // 处理SELECT字段列表
        Matcher selectMatcher = SELECT_PATTERN.matcher(sql);
        if (selectMatcher.find()) {
            String selectFields = selectMatcher.group(1);
            String tableName = selectMatcher.group(2);
            String tableAlias = selectMatcher.group(3);
            
            // 获取表的加密字段
            Set<String> encryptFields = tableFieldCache.getTableEncryptFields(tableName);
            
            for (String fieldName : encryptFields) {
                // 处理字段别名
                String fieldPattern = String.format("\\b(%s)\\b", fieldName);
                Pattern pattern = Pattern.compile(fieldPattern, Pattern.CASE_INSENSITIVE);
                Matcher fieldMatcher = pattern.matcher(selectFields);
                
                if (fieldMatcher.find()) {
                    String decryptExpression = String.format(
                        "CAST(AES_DECRYPT(FROM_BASE64(%s), '%s') AS CHAR) AS %s",
                        fieldName, encryptProperties.getKey(), fieldName
                    );
                    
                    selectFields = selectFields.replaceAll(fieldPattern, decryptExpression);
                    hasChanges = true;
                }
            }
            
            // 替换SELECT字段部分
            if (hasChanges) {
                processedSql = sql.replaceAll(
                    "SELECT\\s+(.*?)\\s+FROM", 
                    "SELECT " + selectFields + " FROM"
                );
            }
        }
        
        // 处理WHERE条件
        processedSql = processWhereClause(processedSql);
        
        return processedSql;
    }
    
    /**
     * 处理INSERT语句
     */
    private String processInsertSql(String sql) {
        // INSERT语句的加密处理逻辑
        // 这里可以添加对INSERT值的加密处理
        return sql;
    }
    
    /**
     * 处理UPDATE语句
     */
    private String processUpdateSql(String sql) {
        // UPDATE语句的加密处理逻辑
        // 这里可以添加对UPDATE值的加密处理
        return sql;
    }
    
    /**
     * 处理DELETE语句
     */
    private String processDeleteSql(String sql) {
        // DELETE语句的加密处理逻辑
        // 这里可以添加对WHERE条件的加密处理
        return processWhereClause(sql);
    }
    
    /**
     * 处理WHERE子句
     */
    private String processWhereClause(String sql) {
        Matcher whereMatcher = WHERE_PATTERN.matcher(sql);
        if (whereMatcher.find()) {
            String whereClause = whereMatcher.group(1);
            String processedWhere = whereClause;
            
            // 获取所有需要加密的表
            Set<String> encryptTables = tableFieldCache.getAllEncryptTables();
            
            for (String tableName : encryptTables) {
                Set<String> encryptFields = tableFieldCache.getTableEncryptFields(tableName);
                
                for (String fieldName : encryptFields) {
                    // 处理WHERE条件中的加密字段
                    String fieldPattern = String.format("\\b(%s)\\s*=", fieldName);
                    Pattern pattern = Pattern.compile(fieldPattern, Pattern.CASE_INSENSITIVE);
                    Matcher fieldMatcher = pattern.matcher(whereClause);
                    
                    if (fieldMatcher.find()) {
                        String encryptExpression = String.format(
                            "%s = TO_BASE64(AES_ENCRYPT(?, '%s'))",
                            fieldName, encryptProperties.getKey()
                        );
                        
                        processedWhere = processedWhere.replaceAll(fieldPattern + "\\s*\\?", encryptExpression);
                        hasChanges = true;
                    }
                }
            }
            
            // 替换WHERE子句
            if (hasChanges) {
                sql = sql.replaceAll(
                    "WHERE\\s+(.*?)(?:ORDER\\s+BY|GROUP\\s+BY|LIMIT|$)", 
                    "WHERE " + processedWhere + "$1"
                );
            }
        }
        
        return sql;
    }
    
    /**
     * 获取结果SQL
     */
    public String getResultSql() {
        return resultSql;
    }
    
    /**
     * 是否有变化
     */
    public boolean hasChanges() {
        return hasChanges;
    }
    
    /**
     * 重置状态
     */
    public void reset() {
        resultSql = null;
        hasChanges = false;
    }
}
