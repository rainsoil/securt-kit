package com.chu7.securtkit.encrypt.interceptor;

import com.chu7.securtkit.encrypt.annotation.EncryptField;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * POJO模式结果解密拦截器
 * 拦截查询结果，对标注了@EncryptField的字段进行解密
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PojoResultDecryptorInterceptor implements Interceptor {
    
    @Autowired
    private List<EncryptStrategy> encryptStrategies;
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取方法名
        String methodName = invocation.getMethod().getName();
        
        if ("query".equals(methodName)) {
            // 处理查询操作
            return processQuery(invocation);
        }
        
        return invocation.proceed();
    }
    
    /**
     * 处理查询操作
     */
    private Object processQuery(Invocation invocation) throws Throwable {
        // 执行查询
        Object result = invocation.proceed();
        
        // 对结果进行解密处理
        if (result != null) {
            decryptResult(result);
        }
        
        return result;
    }
    
    /**
     * 解密结果
     */
    private void decryptResult(Object result) {
        try {
            if (result instanceof Collection) {
                // 处理集合类型的结果
                Collection<?> collection = (Collection<?>) result;
                for (Object item : collection) {
                    decryptObject(item);
                }
            } else if (result instanceof Map) {
                // 处理Map类型的结果
                decryptMap((Map<String, Object>) result);
            } else {
                // 处理单个对象的结果
                decryptObject(result);
            }
        } catch (Exception e) {
            log.error("解密结果失败", e);
        }
    }
    
    /**
     * 解密对象
     */
    private void decryptObject(Object obj) {
        if (obj == null) {
            return;
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            EncryptField encryptField = field.getAnnotation(EncryptField.class);
            if (encryptField != null && encryptField.enabled()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    
                    if (value != null && value instanceof String) {
                        String algorithm = encryptField.algorithm();
                        String decryptedValue = decryptValue((String) value, algorithm);
                        field.set(obj, decryptedValue);
                        log.debug("解密对象字段: {}.{} -> {}", clazz.getSimpleName(), field.getName(), decryptedValue);
                    }
                } catch (Exception e) {
                    log.error("解密对象字段失败: {}.{}", clazz.getSimpleName(), field.getName(), e);
                }
            }
        }
    }
    
    /**
     * 解密Map
     */
    private void decryptMap(Map<String, Object> resultMap) {
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null && value instanceof String) {
                String key = entry.getKey();
                // 检查是否需要解密
                if (needDecrypt(key)) {
                    String decryptedValue = decryptValue((String) value, "AES");
                    entry.setValue(decryptedValue);
                    log.debug("解密Map字段: {} -> {}", key, decryptedValue);
                }
            }
        }
    }
    
    /**
     * 判断字段是否需要解密
     */
    private boolean needDecrypt(String fieldName) {
        // 这里可以根据配置或注解判断是否需要解密
        // 简化实现，可以根据实际需求扩展
        return false;
    }
    
    /**
     * 解密值
     */
    private String decryptValue(String value, String algorithm) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        // 查找对应的解密策略
        EncryptStrategy strategy = findDecryptStrategy(algorithm);
        if (strategy != null) {
            try {
                return strategy.decrypt(value, "default-key");
            } catch (Exception e) {
                log.error("解密失败: {}", e.getMessage(), e);
                return value;
            }
        }
        
        return value;
    }
    
    /**
     * 查找解密策略
     */
    private EncryptStrategy findDecryptStrategy(String algorithm) {
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
