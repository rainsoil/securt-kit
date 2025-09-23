package com.chu7.securtkit.interceptor;

import cn.hutool.core.lang.Pair;
import com.chu7.securtkit.cache.EncryptorInstanceCache;
import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.annotation.FieldInterceptorOrder;
import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.constants.FieldConstant;
import com.chu7.securtkit.constants.InterceptorOrderConstant;
import com.chu7.securtkit.constants.SymbolConstant;
import com.chu7.securtkit.dto.ColumnTableDto;
import com.chu7.securtkit.dto.FieldEncryptorInfoDto;
import com.chu7.securtkit.util.InterceptorUtil;
import com.chu7.securtkit.util.JsqlparserUtil;
import com.chu7.securtkit.util.ReflectUtils;
import com.chu7.securtkit.util.StringUtils;
import com.chu7.securtkit.visitor.PoJoEncrtptorStatementVisitor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.sql.Connection;
import java.util.*;

/**
 * 采用java 函数对pojo处理的加解密模式
 * 处理入参
 *
 * @author liutangqi
 * @date 2024/7/9 14:06
 */
@FieldInterceptorOrder(InterceptorOrderConstant.ENCRYPTOR)
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class PoJoParamEncryptorInterceptor implements Interceptor, BeanPostProcessor {

    private static final ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    private static final ObjectFactory objectFactory = new DefaultObjectFactory();
    private static final ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();


    /**
     * 将入参的字段和占位符？ 对应起来  （boundSql.getParameterMappings()获取的参数和占位符的顺序是一致的，这个结果集里面也有对应的占位符的key，这样就可以全部关联起来了）
     * 思路： 将boundsql 中的？ 占位符替换为 XXX特殊符号防重_1  XXX特殊符号防重_2  XXX特殊符号防重_3  这种，解析时就能得到占位符合参数的对应关系
     * 得到关系后再对请求参数进行加解密处理，因为这个时候我们已经知道该参数对应的数据库表字段是哪个了
     * 处理完后，将我们替换后的 _XXX特殊符号防重_1  这种重新替换为？  这样就能解决这个问题，并且不会存在破坏预编译sql导致sql注入的问题了
     *
     * @author liutangqi
     * @date 2024/7/18 14:45
     * @Param [invocation]
     **/
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        
        // 判断拦截的是StatementHandler还是Executor
        if (target instanceof StatementHandler) {
            return interceptStatementHandler(invocation);
        } else if (target instanceof Executor) {
            return interceptExecutor(invocation);
        }
        
        return invocation.proceed();
    }
    
    /**
     * 拦截StatementHandler.prepare方法
     */
    private Object interceptStatementHandler(Invocation invocation) throws Throwable {
        //1.获取基础信息
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSql = boundSql.getSql();

        log.info("【securt-kit】PoJoParamEncryptorInterceptor被触发，SQL: {}", originalSql);
        log.info("【securt-kit】需要加密的表: {}", TableCache.getFieldEncryptTable());

        //2.当前sql如果肯定不需要加解密，则不解析sql，直接返回
        if (StringUtils.notExist(originalSql, TableCache.getFieldEncryptTable())) {
            log.info("【securt-kit】SQL不包含需要加密的表，跳过加密处理");
            return invocation.proceed();
        }

        //3.检查是否是批量操作，如果是则跳过StatementHandler处理，避免重复加密
        if (isBatchOperation(originalSql)) {
            log.info("【securt-kit】检测到批量操作，跳过StatementHandler处理，避免重复加密");
            return invocation.proceed();
        }

        log.info("【securt-kit】SQL包含需要加密的表，开始加密处理");

        //4.解析sql,获取入参和响应对应的表字段关系
        Pair<Map<String, ColumnTableDto>, List<FieldEncryptorInfoDto>> pair = parseSql(originalSql);

        //5.处理入参
        disposeParam(boundSql, pair);

        //6.执行sql
        Object proceed = invocation.proceed();

        //7.返回结果
        return proceed;
    }
    
    /**
     * 判断是否是批量操作
     */
    private boolean isBatchOperation(String sql) {
        String upperSql = sql.toUpperCase();
        
        // 对于MyBatis-Plus的批量操作，我们通过检查SQL格式来判断
        // MyBatis-Plus批量操作的SQL特征：
        // 1. 包含INSERT/UPDATE/DELETE
        // 2. 包含多个占位符(?)
        // 3. 格式规整，通常有换行和缩进
        if (upperSql.contains("INSERT") || upperSql.contains("UPDATE") || upperSql.contains("DELETE")) {
            // 检查是否包含多个占位符（通常批量操作会有8个或更多占位符）
            int placeholderCount = 0;
            int index = 0;
            while ((index = sql.indexOf("?", index)) != -1) {
                placeholderCount++;
                index += 1;
            }
            // 如果占位符数量较多（>=8），且SQL包含换行，可能是MyBatis-Plus的批量操作
            if (placeholderCount >= 8 && sql.contains("\n")) {
                return true;
            }
            
            // 检查SQL是否包含多个VALUES子句（真正的批量INSERT）
            // 真正的批量INSERT会有多个VALUES子句，如：INSERT INTO table VALUES (...), (...), (...)
            if (upperSql.contains("INSERT") && upperSql.contains("VALUES")) {
                // 计算VALUES子句的数量，如果有多个逗号分隔的VALUES，说明是批量操作
                int valuesCount = 0;
                index = 0;
                while ((index = upperSql.indexOf("VALUES", index)) != -1) {
                    valuesCount++;
                    index += 6; // "VALUES".length()
                }
                // 如果有多个VALUES子句，说明是真正的批量操作
                return valuesCount > 1;
            }
        }
        
        return false;
    }
    
    /**
     * 拦截Executor.update方法，处理批量操作
     */
    private Object interceptExecutor(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        
        // 获取SQL
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql();
        
        log.info("【securt-kit】Executor.update被触发，SQL: {}", originalSql);

        //当前sql如果肯定不需要加解密，则不解析sql，直接返回
        if (StringUtils.notExist(originalSql, TableCache.getFieldEncryptTable())) {
            return invocation.proceed();
        }

        // 检查是否是真正的批量操作（不是单条操作）
        if (isBatchOperation(originalSql)) {
            log.info("【securt-kit】批量操作需要加密，开始处理参数");

            //解析sql,获取入参和响应对应的表字段关系
            Pair<Map<String, ColumnTableDto>, List<FieldEncryptorInfoDto>> pair = parseSql(originalSql);

            //处理当前批次的参数 - 直接修改parameter对象
            disposeParamForExecutor(parameter, pair);
        } else {
            log.info("【securt-kit】单条操作，跳过Executor处理，由StatementHandler处理");
        }

        //执行sql
        return invocation.proceed();
    }

    /**
     * 为Executor.update处理参数，直接修改parameter对象
     */
    private void disposeParamForExecutor(Object parameter, Pair<Map<String, ColumnTableDto>, List<FieldEncryptorInfoDto>> pair) {
        if (parameter == null) {
            return;
        }
        
        // 如果是基本类型，直接返回
        if (FieldConstant.FUNDAMENTAL.contains(parameter.getClass())) {
            return;
        }
        
        // 检查pair和其内容是否为null
        if (pair == null || pair.getKey() == null) {
            log.warn("【securt-kit】disposeParamForExecutor: pair或其key为null，跳过参数处理");
            return;
        }
        
        // 获取需要加密的字段映射
        Map<String, ColumnTableDto> placeholderColumnTableMap = pair.getKey();
        
        // 处理不同类型的参数
        Object targetObject = parameter;
        
        // 如果是Map类型（MyBatis-Plus批量操作），尝试获取实体对象
        if (parameter instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) parameter;
            // 查找实体对象，可能是et、entity等key
            for (Object key : paramMap.keySet()) {
                Object value = paramMap.get(key);
                if (value != null && !FieldConstant.FUNDAMENTAL.contains(value.getClass())) {
                    // 检查是否是实体对象（不是条件构造器）
                    if (!isWrapperClass(value.getClass())) {
                        targetObject = value;
                        log.info("【securt-kit】从Map参数中提取实体对象: {} -> {}", key, value.getClass().getSimpleName());
                        break;
                    }
                }
            }
        }
        
        // 如果没有找到实体对象，跳过处理
        if (targetObject == null) {
            log.info("【securt-kit】未找到实体对象，跳过Executor参数加密处理");
            return;
        }
        
        // 使用反射直接修改targetObject对象的字段值
        MetaObject metaObject = MetaObject.forObject(targetObject, objectFactory, objectWrapperFactory, reflectorFactory);
        
        for (Map.Entry<String, ColumnTableDto> entry : placeholderColumnTableMap.entrySet()) {
            String placeholderKey = entry.getKey();
            ColumnTableDto columnTableDto = entry.getValue();
            
            // 获取数据库字段名
            String dbFieldName = columnTableDto.getSourceColumn();
            if (StringUtils.isBlank(dbFieldName)) {
                continue;
            }
            
            // 将数据库字段名转换为Java字段名（下划线转驼峰）
            String javaFieldName = convertToCamelCase(dbFieldName);
            
            try {
                // 获取字段值
                Object fieldValue = metaObject.getValue(javaFieldName);
                if (fieldValue == null || !(fieldValue instanceof String)) {
                    continue;
                }
                
                // 检查是否需要加密
                FieldEncryptor fieldEncryptor = JsqlparserUtil.parseFieldEncryptor(columnTableDto);
                if (fieldEncryptor != null) {
                    String ciphertext = EncryptorInstanceCache.<String>getInstance(fieldEncryptor.value()).encryption((String) fieldValue);
                    log.info("【securt-kit】Executor加密字段 {}: {} -> {}", javaFieldName, fieldValue, ciphertext);
                    metaObject.setValue(javaFieldName, ciphertext);
                }
            } catch (Exception e) {
                // 如果获取字段值失败，记录日志并继续处理其他字段
                log.warn("【securt-kit】获取字段值失败: {} -> {}", javaFieldName, e.getMessage());
                continue;
            }
        }
    }
    
    /**
     * 判断是否是MyBatis-Plus的条件构造器类
     */
    private boolean isWrapperClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        
        String className = clazz.getName();
        return className.contains("LambdaUpdateWrapper") || 
               className.contains("LambdaQueryWrapper") ||
               className.contains("UpdateWrapper") ||
               className.contains("QueryWrapper") ||
               className.contains("AbstractWrapper");
    }
    
    /**
     * 将下划线命名转换为驼峰命名
     */
    private String convertToCamelCase(String underscoreName) {
        if (StringUtils.isBlank(underscoreName)) {
            return underscoreName;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        
        for (int i = 0; i < underscoreName.length(); i++) {
            char c = underscoreName.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }

    /**
     * 将反射修改了property的给改回去，避免一级缓存导致找不到getter方法报错
     *
     * @author liutangqi
     * @date 2024/9/23 19:30
     * @Param [propertyMap, boundSql]
     **/
    private void revivificationParam(Map<String, String> propertyMap, BoundSql boundSql) {
        if (propertyMap == null || propertyMap.isEmpty()) {
            return;
        }

        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String originalValue = propertyMap.get(parameterMapping.getProperty());
            if (StringUtils.isNotBlank(originalValue)) {
                //反射修改property为原值
                ReflectUtils.setFieldValue(parameterMapping, "property", originalValue);
            }
        }

    }


    /**
     * 解析sql,获取入参和响应对应的表字段关系
     *
     * @author liutangqi
     * @date 2024/7/18 14:55
     * @Param [sql]
     **/
    private Pair<Map<String, ColumnTableDto>, List<FieldEncryptorInfoDto>> parseSql(String sql) throws JSQLParserException {
        //1.将sql中的 ? 占位符替换成我们自定义的特殊符号
        String placeholderSql = StringUtils.question2Placeholder(sql);

        //2.解析sql的响应结果，和占位符对应的表字段关系
        Statement statement = JsqlparserUtil.parse(placeholderSql);
        PoJoEncrtptorStatementVisitor poJoEncrtptorStatementVisitor = new PoJoEncrtptorStatementVisitor();
        statement.accept(poJoEncrtptorStatementVisitor);

        //3.获取解析结果
        Map<String, ColumnTableDto> placeholderColumnTableMap = poJoEncrtptorStatementVisitor.getPlaceholderColumnTableMap();
        List<FieldEncryptorInfoDto> fieldEncryptorInfos = poJoEncrtptorStatementVisitor.getFieldEncryptorInfos();
        return Pair.of(placeholderColumnTableMap, fieldEncryptorInfos);
    }

    /**
     * 将入参中需要加密的进行加密处理
     *
     * @author liutangqi
     * @date 2024/7/18 15:18
     * @Param [parameterObject, pair]
     **/
    private void disposeParam(BoundSql boundSql, Pair<Map<String, ColumnTableDto>, List<FieldEncryptorInfoDto>> pair) {
        //1.获取所有入参（这个的顺序和占位符顺序一致）
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        log.info("【securt-kit】参数映射数量: {}", parameterMappings.size());
        
        // 检查pair和其内容是否为null
        if (pair == null || pair.getKey() == null) {
            log.warn("【securt-kit】pair或其key为null，跳过参数处理");
            return;
        }
        
        log.info("【securt-kit】占位符映射数量: {}", pair.getKey().size());

        //2.将其中需要加密的字段进行加密(注意：这里只返回key value对应关系，不能现在就boundSql.setAdditionalParameter ，否则会导致 parseObj()方法中 hasAdditionalParameter()结果出错 aaa.bbb.ccc 这种方法只判断里面是否有aaa)
        Map<String, Object> parameterValue = new HashMap<>();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            //sql关系中，占位符被统一替换成了这个
            String placeholderKey = FieldConstant.PLACEHOLDER + i;
            //获取当前映射字段的入参值
            Object propertyValue = parseObj(boundSql, parameterMapping);
            log.info("【securt-kit】处理参数 {}: property={}, value={}, placeholderKey={}", 
                    i, parameterMapping.getProperty(), propertyValue, placeholderKey);

            //如果需要加密的话，将加密后的值，替换原有入参
            FieldEncryptor fieldEncryptor = parseFieldEncryptor(placeholderKey, pair.getKey());
            if (propertyValue instanceof String && fieldEncryptor != null) {
                String ciphertext = EncryptorInstanceCache.<String>getInstance(fieldEncryptor.value()).encryption((String) propertyValue);
                log.info("【securt-kit】字段加密: {} -> {}", propertyValue, ciphertext);
                parameterValue.put(String.valueOf(i), ciphertext);
            } else {
                //不需要加密的话，则入参还是使用旧值
                log.info("【securt-kit】字段不加密: {} (fieldEncryptor={})", propertyValue, fieldEncryptor);
                parameterValue.put(String.valueOf(i), propertyValue);
            }
        }

        //3.将处理好的结果集进行设置值
        for (int i = 0; i < parameterMappings.size(); i++) {
            boundSql.setAdditionalParameter(parameterMappings.get(i).getProperty(), parameterValue.get(String.valueOf(i)));
        }
        log.info("【securt-kit】参数处理完成");
    }


    /**
     * 判断当前映射的值是否存在一个入参，有多个不同值的情况 （比如一个入参，对应不同的表字段，这些表字段的加密算法或者明文，密文存储方式不同）
     *
     * @author liutangqi
     * @date 2025/4/10 9:46
     * @Param [parameterMappings, parameterValue]
     **/
    private boolean oneDataMuchValue(List<ParameterMapping> parameterMappings, Map<String, Object> parameterValue) {
        Map<String, Object> tmpMap = new HashMap<>();
        for (int i = 0; i < parameterMappings.size(); i++) {
            String property = parameterMappings.get(i).getProperty();
            Object curValue = parameterValue.get(String.valueOf(i));
            Object tmpValue = tmpMap.get(property);
            if (tmpValue != null && !Objects.equals(tmpValue, curValue)) {
                return true;
            }
            tmpMap.put(property, curValue);
        }
        return false;
    }

    /**
     * 根据占位符名字获取sql解析结果集中字段上的注解
     *
     * @author liutangqi
     * @date 2024/9/20 17:19
     * @Param [placeholderKey, placeholderColumnTableMap]
     **/
    private FieldEncryptor parseFieldEncryptor(String placeholderKey, Map<String, ColumnTableDto> placeholderColumnTableMap) {
        ColumnTableDto columnTableDto = placeholderColumnTableMap.getOrDefault(placeholderKey, new ColumnTableDto());
        return JsqlparserUtil.parseFieldEncryptor(columnTableDto);
    }

    /**
     * 解析映射对象的属性值
     *
     * @author liutangqi
     * @date 2024/7/24 14:49
     * @Param [configuration, boundSql, parameter]
     **/
    private Object parseObj(BoundSql boundSql, ParameterMapping parameter) {
        Object obj = boundSql.getParameterObject();
        String property = parameter.getProperty();

        //0.判断boundsql中AdditionalParameter是否存在，存在就取boundsql中的(当入参在实体类中存在List时会走这段逻辑)
        if (boundSql.hasAdditionalParameter(property)) {
            return boundSql.getAdditionalParameter(property);
        }

        //1. 基本数据类型的包装类或者字符串或时间类型，直接返回原值
        if (FieldConstant.FUNDAMENTAL.contains(obj.getClass())) {
            return obj;
        }

        //2.其它类型的值，通过反射获取，如果入参是  dto.xxx 这种，则分开解析每一段，直至获取最终值
        String[] propertyArr = property.split(SymbolConstant.ESC_FULL_STOP);

        //上一层对象
        Object pre = obj;
        for (String prop : propertyArr) {
            pre = MetaObject.forObject(pre, objectFactory, objectWrapperFactory, reflectorFactory).getValue(prop);
        }
        return pre;
    }

    /**
     * 低版本mybatis 这个方法不是default 方法，会报错找不到实现方法，所以这里实现默认的方法
     *
     * @author liutangqi
     * @date 2024/9/9 17:38
     * @Param [target]
     **/
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }


    /**
     * 实现父类default方法，避免低版本不兼容，找不到实现类
     *
     * @author liutangqi
     * @date 2024/9/10 11:36
     * @Param [bean, beanName]
     **/
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 实现父类default方法，避免低版本不兼容，找不到实现类
     *
     * @author liutangqi
     * @date 2024/9/10 11:36
     * @Param [bean, beanName]
     **/
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //当前没有注册此拦截器，则手动注册，避免有些项目自定义了SqlSessionFactory 导致拦截器漏注册
        //使用@Bean的方式注册，可能会导致某些项目的@PostContruct先于拦截器执行，导致拦截器业务代码失效
        if (SqlSessionFactory.class.isAssignableFrom(bean.getClass())) {
            SqlSessionFactory sessionFactory = (SqlSessionFactory) bean;
            if (sessionFactory.getConfiguration().getInterceptors()
                    .stream()
                    .filter(f -> PoJoParamEncryptorInterceptor.class.isAssignableFrom(f.getClass()))
                    .findAny()
                    .orElse(null) == null) {
                sessionFactory.getConfiguration().addInterceptor(new PoJoParamEncryptorInterceptor());
                log.info("【securt-kit】手动注册拦截器 PoJoParamEncryptorInterceptor");
            }

            //修改拦截器顺序
            InterceptorUtil.sort(sessionFactory.getConfiguration());
        }
        return bean;
    }

}