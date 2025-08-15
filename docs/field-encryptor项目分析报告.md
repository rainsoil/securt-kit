# field-encryptor 项目分析报告

## 项目概述

**field-encryptor** 是一个基于 MyBatis 拦截器 + JSqlParser 的动态 SQL 改写框架，主要用于数据库字段的自动加解密、查询脱敏、SQL 语法自动切换和业务数据自动隔离等功能。

- **项目地址**: https://gitee.com/tired-of-the-water/field-encryptor
- **当前版本**: 3.5.2-alpha
- **技术栈**: Java 8+, Spring Boot, MyBatis, JSqlParser, Hutool
- **许可证**: Apache License 2.0

## 核心功能特性

### 1. 数据库字段自动加解密
- **DB模式**: 依赖数据库加解密函数，通过 SQL 改写实现
- **POJO模式**: 依赖 Java 库加解密算法，通过参数和结果拦截实现
- 支持密文模糊查询（DB模式）
- 业务代码零侵入

### 2. 数据库字段查询脱敏
- 基于 SQL 结果集的脱敏处理
- 支持业务差异化脱敏
- 可获取完整响应对象进行业务判断

### 3. SQL 语法自动切换
- 支持 MySQL 到达梦数据库的语法自动转换
- 覆盖率高，一次配置全项目生效
- 避免硬编码改造

### 4. 业务数据自动隔离
- 基于实体类注解配置
- 支持多种隔离策略（=、LIKE、IN等）
- 项目规范统一，易于维护

## 技术架构设计

### 模块结构
```
field-encryptor/
├── encryptor-annos/          # 注解模块
│   └── src/main/java/com/sangsang/domain/
│       ├── annos/            # 注解定义
│       ├── enums/            # 枚举定义
│       ├── strategy/         # 策略接口
│       └── exception/        # 异常定义
└── encryptor-core/           # 核心功能模块
    └── src/main/java/com/sangsang/
        ├── interceptor/      # 拦截器实现
        ├── visitor/          # 访问者模式实现
        ├── config/           # 配置类
        ├── cache/            # 缓存管理
        ├── encryptor/        # 加解密算法
        ├── transformation/   # SQL转换
        ├── aop/              # 切面处理
        └── util/             # 工具类
```

### 核心设计模式

#### 1. 拦截器模式
- **DBFieldEncryptorInterceptor**: 数据库模式加解密拦截器
- **PoJoParamEncrtptorInterceptor**: POJO模式参数加密拦截器
- **PoJoResultEncrtptorInterceptor**: POJO模式结果解密拦截器
- **FieldDesensitizeInterceptor**: 字段脱敏拦截器
- **IsolationInterceptor**: 数据隔离拦截器

#### 2. 访问者模式
- **DBDencryptStatementVisitor**: 数据库加解密语句访问者
- **DBDecryptSelectVisitor**: 数据库解密查询访问者
- **DBDecryptExpressionVisitor**: 数据库解密表达式访问者

#### 3. 策略模式
- **FieldEncryptorStrategy**: 加解密策略接口
- **DefaultDBFieldEncryptorPattern**: 默认数据库加解密策略
- **DefaultPoJoFieldEncryptorPattern**: 默认POJO加解密策略

## 核心代码逻辑分析

### 1. 加解密流程

#### DB模式加解密流程
```java
// DBFieldEncryptorInterceptor.java
@Override
public Object intercept(Invocation invocation) throws Throwable {
    // 1. 获取SQL语句
    StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
    BoundSql boundSql = statementHandler.getBoundSql();
    String oldSql = boundSql.getSql();
    
    // 2. 判断是否需要加解密
    if (StringUtils.notExist(oldSql, TableCache.getFieldEncryptTable())) {
        return invocation.proceed();
    }
    
    // 3. 使用JSqlParser解析SQL
    Statement statement = JsqlparserUtil.parse(oldSql);
    DBDencryptStatementVisitor dbStatementVisitor = new DBDencryptStatementVisitor();
    statement.accept(dbStatementVisitor);
    
    // 4. 获取改写后的SQL
    String newSql = dbStatementVisitor.getResultSql();
    
    // 5. 反射修改SQL语句
    Field field = boundSql.getClass().getDeclaredField("sql");
    field.setAccessible(true);
    field.set(boundSql, newSql);
    
    // 6. 执行修改后的SQL
    return invocation.proceed();
}
```

#### POJO模式加解密流程
- **参数加密**: 在 `PoJoParamEncrtptorInterceptor` 中拦截参数，对标注字段进行加密
- **结果解密**: 在 `PoJoResultEncrtptorInterceptor` 中拦截结果集，对标注字段进行解密

### 2. SQL解析与改写

#### 访问者模式实现
```java
// DBDencryptStatementVisitor.java
public class DBDencryptStatementVisitor extends StatementVisitorAdapter {
    @Override
    public void visit(Select select) {
        // 处理SELECT语句
        SelectBody selectBody = select.getSelectBody();
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            // 处理SELECT字段列表
            List<SelectItem> selectItems = plainSelect.getSelectItems();
            // 处理WHERE条件
            Expression whereExpression = plainSelect.getWhere();
            // 处理FROM子句
            FromItem fromItem = plainSelect.getFromItem();
        }
    }
}
```

#### SQL改写示例
```sql
-- 原始SQL
SELECT phone, user_name FROM tb_user WHERE phone = ?

-- DB模式改写后
SELECT 
CAST(AES_DECRYPT(FROM_BASE64(phone), 'secret-key') AS CHAR) AS phone,
user_name 
FROM tb_user 
WHERE phone = TO_BASE64(AES_ENCRYPT(?, 'secret-key'))
```

### 3. 配置管理

#### 配置属性
```java
// EncryptorConfig.java
@Configuration
public class EncryptorConfig {
    
    @Bean
    @ConditionalOnProperty(name = "field.encryptor.patternType", havingValue = "db")
    public DBFieldEncryptorInterceptor dbInterceptor() {
        return new DBFieldEncryptorInterceptor();
    }
    
    @Bean
    @ConditionalOnProperty(name = "field.encryptor.patternType", havingValue = "pojo")
    public PoJoParamEncrtptorInterceptor pojoParamInterceptor() {
        return new PoJoParamEncrtptorInterceptor();
    }
}
```

#### 配置文件示例
```properties
# 扫描实体类包路径
field.scanEntityPackage[0]=com.sangsang.*.entity
# 加解密模式：db 或 pojo
field.encryptor.patternType=db
# 加密密钥
field.encryptor.secretKey=TIREDTHEWATER
```

## 注解系统

### 核心注解

#### @FieldEncryptor
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldEncryptor {
    Class<? extends FieldEncryptorStrategy> value() 
        default DefaultStrategyBase.EncryptorBeanStrategy.class;
}
```

#### @FieldDesensitize
```java
// 字段脱敏注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDesensitize {
    // 脱敏策略配置
}
```

#### @FieldIsolation
```java
// 数据隔离注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldIsolation {
    // 隔离策略配置
}
```

## 缓存机制

### 表字段缓存
- **TableCache**: 缓存表字段信息，避免重复解析
- **EncryptorInstanceCache**: 缓存加解密算法实例
- **FieldParseCache**: 缓存字段解析结果

### 缓存策略
- 基于实体类扫描的预加载
- 运行时动态缓存
- 支持缓存刷新和清理

## 扩展性设计

### 1. 策略接口扩展
```java
public interface FieldEncryptorStrategy {
    String encrypt(String plainText, String key);
    String decrypt(String cipherText, String key);
    String getAlgorithm();
}
```

### 2. 自定义加解密算法
- 实现 `FieldEncryptorStrategy` 接口
- 注册为Spring Bean
- 支持多种算法并存

### 3. 自定义脱敏策略
- 实现脱敏策略接口
- 支持业务差异化脱敏
- 可获取完整上下文信息

## 性能优化

### 1. SQL解析优化
- 使用JSqlParser进行高效SQL解析
- 缓存解析结果，避免重复解析
- 支持批量操作优化

### 2. 拦截器顺序优化
```java
@FieldInterceptorOrder(InterceptorOrderConstant.ENCRYPTOR)
public class DBFieldEncryptorInterceptor implements Interceptor {
    // 拦截器执行顺序控制
}
```

### 3. 条件判断优化
- 提前判断是否需要处理
- 避免不必要的SQL解析
- 支持表级别过滤

## 兼容性考虑

### 1. MyBatis版本兼容
- 支持MyBatis 3.x版本
- 兼容MyBatis Plus
- 处理低版本兼容性问题

### 2. 数据库兼容
- 支持MySQL、达梦等数据库
- 自动语法转换
- 扩展UDF支持

### 3. Spring Boot兼容
- 支持Spring Boot 2.x
- 自动配置支持
- 条件化Bean注册

## 使用示例

### 1. 基本配置
```properties
# application.properties
field.scanEntityPackage[0]=com.example.entity
field.encryptor.patternType=db
field.encryptor.secretKey=your-secret-key
```

### 2. 实体类标注
```java
@Data
@TableName(value = "tb_user")
public class UserEntity {
    
    @TableField(value = "phone")
    @FieldEncryptor
    private String phone;
    
    @TableField(value = "email")
    @FieldEncryptor
    private String email;
    
    private String userName; // 不加密字段
}
```

### 3. 业务代码
```java
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public void createUser(UserEntity user) {
        // phone和email字段会自动加密
        userMapper.insert(user);
    }
    
    public UserEntity getUserById(Long id) {
        // phone和email字段会自动解密
        return userMapper.selectById(id);
    }
}
```

## 项目优势

### 1. 零侵入性
- 业务代码无需修改
- 仅需在实体类上标注注解
- 自动拦截处理

### 2. 功能全面
- 支持加解密、脱敏、隔离、语法转换
- 多种模式可选
- 扩展性强

### 3. 性能优秀
- 基于JSqlParser的高效解析
- 智能缓存机制
- 条件化处理

### 4. 易于使用
- 配置简单
- 文档完善
- 示例丰富

## 项目不足

### 1. 版本状态
- 当前为alpha版本，可能存在稳定性问题
- 需要更多生产环境验证

### 2. 学习成本
- 需要理解JSqlParser的工作原理
- 自定义扩展需要一定技术基础

### 3. 调试难度
- SQL改写过程相对复杂
- 问题排查需要深入理解框架原理

## 总结

**field-encryptor** 是一个设计优秀、功能强大的数据库字段处理框架，特别适合需要数据安全、多数据库支持、业务隔离等场景的企业级应用。其基于访问者模式的SQL解析改写机制和零侵入的设计理念，为开发者提供了优雅的解决方案。

该框架在技术架构、扩展性、性能优化等方面都表现出色，是一个值得学习和使用的优秀开源项目。建议在非关键业务场景中先进行充分测试，逐步推广到生产环境。
