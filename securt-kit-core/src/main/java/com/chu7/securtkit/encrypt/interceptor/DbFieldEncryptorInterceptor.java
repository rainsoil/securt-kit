package com.chu7.securtkit.encrypt.interceptor;

import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.util.SqlParseUtil;
import com.chu7.securtkit.encrypt.visitor.DbEncryptStatementVisitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * 数据库字段加密拦截器
 * 拦截SQL执行，对需要加密的字段进行自动加解密处理
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DbFieldEncryptorInterceptor implements Interceptor {
    
    @Autowired
    private TableFieldCache tableFieldCache;
    
    @Autowired
    private DbEncryptStatementVisitor dbEncryptStatementVisitor;
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取拦截器目标对象
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        
        // 获取当前执行的SQL
        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSql = boundSql.getSql();
        
        log.debug("【DbFieldEncryptor】原始SQL: {}", originalSql);
        
        // 检查是否需要处理加密
        if (!needProcess(originalSql)) {
            return invocation.proceed();
        }
        
        // 使用访问者处理SQL
        String processedSql = dbEncryptStatementVisitor.processSql(originalSql);
        
        if (dbEncryptStatementVisitor.hasChanges()) {
            log.debug("【DbFieldEncryptor】处理后的SQL: {}", processedSql);
            
            // 反射修改SQL语句
            try {
                Field sqlField = boundSql.getClass().getDeclaredField("sql");
                sqlField.setAccessible(true);
                sqlField.set(boundSql, processedSql);
            } catch (Exception e) {
                log.error("修改SQL语句失败", e);
            }
        }
        
        // 执行修改后的SQL
        return invocation.proceed();
    }
    
    /**
     * 判断是否需要处理加密
     */
    private boolean needProcess(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        
        // 检查是否包含需要加密的表
        return tableFieldCache.getAllEncryptTables().stream()
                .anyMatch(tableName -> sql.toLowerCase().contains(tableName.toLowerCase()));
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
        // 可以在这里设置拦截器属性
    }
}
