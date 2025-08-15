package com.chu7.securtkit.encrypt.util;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

/**
 * SQL解析工具类
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
public class SqlParseUtil {
    
    /**
     * 解析SQL语句
     *
     * @param sql SQL语句
     * @return 解析后的Statement对象
     */
    public static Statement parseSql(String sql) {
        try {
            return CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            log.error("SQL解析失败: {}", sql, e);
            return null;
        }
    }
    
    /**
     * 判断是否为SELECT语句
     *
     * @param statement Statement对象
     * @return 是否为SELECT语句
     */
    public static boolean isSelectStatement(Statement statement) {
        return statement instanceof Select;
    }
    
    /**
     * 判断是否为INSERT语句
     *
     * @param statement Statement对象
     * @return 是否为INSERT语句
     */
    public static boolean isInsertStatement(Statement statement) {
        return statement instanceof net.sf.jsqlparser.statement.insert.Insert;
    }
    
    /**
     * 判断是否为UPDATE语句
     *
     * @param statement Statement对象
     * @return 是否为UPDATE语句
     */
    public static boolean isUpdateStatement(Statement statement) {
        return statement instanceof net.sf.jsqlparser.statement.update.Update;
    }
    
    /**
     * 判断是否为DELETE语句
     *
     * @param statement Statement对象
     * @return 是否为DELETE语句
     */
    public static boolean isDeleteStatement(Statement statement) {
        return statement instanceof net.sf.jsqlparser.statement.delete.Delete;
    }
    
    /**
     * 获取SQL类型
     *
     * @param statement Statement对象
     * @return SQL类型字符串
     */
    public static String getSqlType(Statement statement) {
        if (statement == null) {
            return "UNKNOWN";
        }
        
        if (isSelectStatement(statement)) {
            return "SELECT";
        } else if (isInsertStatement(statement)) {
            return "INSERT";
        } else if (isUpdateStatement(statement)) {
            return "UPDATE";
        } else if (isDeleteStatement(statement)) {
            return "DELETE";
        } else {
            return statement.getClass().getSimpleName().toUpperCase();
        }
    }
    
    /**
     * 判断SQL是否包含指定表名
     *
     * @param sql SQL语句
     * @param tableName 表名
     * @return 是否包含
     */
    public static boolean containsTable(String sql, String tableName) {
        if (sql == null || tableName == null) {
            return false;
        }
        
        // 简单的字符串匹配，后续可以优化为更精确的解析
        String upperSql = sql.toUpperCase();
        String upperTableName = tableName.toUpperCase();
        return upperSql.contains(upperTableName);
    }
    
    /**
     * 判断SQL是否包含指定字段
     *
     * @param sql SQL语句
     * @param fieldName 字段名
     * @return 是否包含
     */
    public static boolean containsField(String sql, String fieldName) {
        if (sql == null || fieldName == null) {
            return false;
        }
        
        // 简单的字符串匹配，后续可以优化为更精确的解析
        String upperSql = sql.toUpperCase();
        String upperFieldName = fieldName.toUpperCase();
        return upperSql.contains(upperFieldName);
    }
}
