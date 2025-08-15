package com.chu7.securtkit.encrypt.interceptor;

import com.chu7.securtkit.encrypt.annotation.EncryptField;
import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.strategy.EncryptStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * POJO模式参数加密拦截器
 * 拦截参数，对标注了@EncryptField的字段进行加密
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PojoParamEncryptorInterceptor implements Interceptor {
    
    @Autowired
    private TableFieldCache tableFieldCache;
    
    @Autowired
    private List<EncryptStrategy> encryptStrategies;
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取方法名
        String methodName = invocation.getMethod().getName();
        
        if ("update".equals(methodName)) {
            // 处理更新操作（INSERT、UPDATE、DELETE）
            return processUpdate(invocation);
        } else if ("query".equals(methodName)) {
            // 处理查询操作
            return invocation.proceed();
        }
        
        return invocation.proceed();
    }
    
    /**
     * 处理更新操作
     */
    private Object processUpdate(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        
        // 获取SQL命令类型
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        
        if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
            // 对参数进行加密处理
            encryptParameters(parameter);
        }
        
        return invocation.proceed();
    }
    
    /**
     * 加密参数
     */
    private void encryptParameters(Object parameter) {
        if (parameter == null) {
            return;
        }
        
        try {
            if (parameter instanceof Map) {
                // 处理Map类型的参数
                encryptMapParameters((Map<String, Object>) parameter);
            } else {
                // 处理对象类型的参数
                encryptObjectParameters(parameter);
            }
        } catch (Exception e) {
            log.error("加密参数失败", e);
        }
    }
    
    /**
     * 加密Map类型参数
     */
    private void encryptMapParameters(Map<String, Object> parameterMap) {
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null && value instanceof String) {
                String key = entry.getKey();
                // 检查是否需要加密
                if (needEncrypt(key)) {
                    String encryptedValue = encryptValue((String) value, "AES");
                    entry.setValue(encryptedValue);
                    log.debug("加密Map参数: {} -> {}", key, encryptedValue);
                }
            }
        }
    }
    
    /**
     * 加密对象类型参数
     */
    private void encryptObjectParameters(Object parameter) {
        Class<?> clazz = parameter.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            EncryptField encryptField = field.getAnnotation(EncryptField.class);
            if (encryptField != null && encryptField.enabled()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(parameter);
                    
                    if (value != null && value instanceof String) {
                        String algorithm = encryptField.algorithm();
                        String encryptedValue = encryptValue((String) value, algorithm);
                        field.set(parameter, encryptedValue);
                        log.debug("加密对象字段: {}.{} -> {}", clazz.getSimpleName(), field.getName(), encryptedValue);
                    }
                } catch (Exception e) {
                    log.error("加密对象字段失败: {}.{}", clazz.getSimpleName(), field.getName(), e);
                }
            }
        }
    }
    
    /**
     * 判断字段是否需要加密
     */
    private boolean needEncrypt(String fieldName) {
        // 这里可以根据配置或注解判断是否需要加密
        // 简化实现，可以根据实际需求扩展
        return false;
    }
    
    /**
     * 加密值
     */
    private String encryptValue(String value, String algorithm) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        // 查找对应的加密策略
        EncryptStrategy strategy = findEncryptStrategy(algorithm);
        if (strategy != null) {
            try {
                return strategy.encrypt(value, "default-key");
            } catch (Exception e) {
                log.error("加密失败: {}", e.getMessage(), e);
                return value;
            }
        }
        
        return value;
    }
    
    /**
     * 查找加密策略
     */
    private EncryptStrategy findEncryptStrategy(String algorithm) {
        return encryptStrategies.stream()
                .filter(strategy -> strategy.supports(algorithm))
                .findFirst()
                .orElse(null);
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
