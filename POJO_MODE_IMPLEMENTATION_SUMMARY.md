# Securt-Kit POJO模式加解密功能实现总结

## 实现概述

成功在securt-kit项目中实现了POJO模式的字段加解密功能，该功能基于MyBatis拦截器和JSQLParser，通过动态修改Mapper的入参和响应来实现字段自动加解密。

## 核心组件

### 1. 注解系统
- **@FieldEncryptor**: 标识需要加密的字段
- **@PoJoResultEncryptor**: 标识需要解密的响应字段
- **@FieldInterceptorOrder**: 控制拦截器执行顺序

### 2. 策略模式
- **FieldEncryptorStrategy<T>**: 加解密策略接口
- **DefaultPoJoFieldEncryptorStrategy**: 默认DES算法实现

### 3. 拦截器系统
- **PoJoParamEncryptorInterceptor**: 入参加密拦截器
- **PoJoResultDecryptorInterceptor**: 响应解密拦截器

### 4. SQL解析
- **PoJoEncryptorStatementVisitor**: SQL解析访问者
- **JsqlparserUtil**: SQL解析工具类

### 5. 缓存管理
- **EncryptorInstanceCache**: 加密器实例缓存
- **TableCache**: 表结构字段信息缓存

### 6. 配置系统
- **EncryptorProperties**: 配置属性类
- **EncryptorAutoConfiguration**: 自动配置类

## 文件结构

```
securt-kit-core/src/main/java/com/chu7/securtkit/
├── annotation/
│   ├── FieldEncryptor.java
│   ├── PoJoResultEncryptor.java
│   └── FieldInterceptorOrder.java
├── strategy/
│   ├── FieldEncryptorStrategy.java
│   └── impl/
│       └── DefaultPoJoFieldEncryptorStrategy.java
├── interceptor/
│   ├── PoJoParamEncryptorInterceptor.java
│   └── PoJoResultDecryptorInterceptor.java
├── visitor/
│   └── PoJoEncryptorStatementVisitor.java
├── cache/
│   ├── EncryptorInstanceCache.java
│   └── TableCache.java
├── util/
│   ├── JsqlparserUtil.java
│   ├── StringUtils.java
│   ├── ReflectUtils.java
│   └── CollectionUtils.java
└── config/
    ├── EncryptorProperties.java
    └── EncryptorAutoConfiguration.java
```

## 使用方式

### 1. 配置属性
```yaml
securt-kit:
  encryptor:
    enabled: true
    mode: pojo
    secretKey: your-secret-key-here
    scanEntityPackages:
      - com.example.entity
    cacheCapacity: 100
```

### 2. 实体类标注
```java
@Data
@TableName(value = "tb_user")
public class UserEntity {
    
    @TableField(value = "phone")
    @FieldEncryptor // 标识这个字段需要加密
    private String phone;
    
    @TableField(value = "email")
    @PoJoResultEncryptor // 标识这个字段需要解密
    private String email;
}
```

### 3. Mapper使用
```java
@Mapper
public interface UserMapper {
    
    @Insert("INSERT INTO tb_user(phone, email) VALUES(#{phone}, #{email})")
    int insert(UserEntity user);
    
    @Select("SELECT * FROM tb_user WHERE phone = #{phone}")
    UserEntity selectByPhone(String phone);
}
```

## 核心流程

### 1. 入参加密流程
1. 拦截StatementHandler.prepare()方法
2. 检查SQL是否包含需要加密的表
3. 解析SQL，识别需要加密的字段
4. 对入参进行加密处理
5. 修改BoundSql中的参数值
6. 继续执行原SQL

### 2. 响应解密流程
1. 拦截Executor.query()方法
2. 执行SQL获取结果
3. 检查响应类型（实体类/Map/String）
4. 识别需要解密的字段
5. 对响应结果进行解密处理
6. 返回解密后的结果

## 技术特点

### 1. 无侵入性
- 业务代码零侵入
- 仅需在实体类字段上标注注解
- 自动处理所有相关SQL

### 2. 高性能
- SQL解析结果LRU缓存
- 智能跳过不需要处理的SQL
- 最小化性能影响

### 3. 高扩展性
- 策略模式支持自定义算法
- 支持多种加密模式
- 插件化架构设计

### 4. 易用性
- 基于Spring Boot自动配置
- 丰富的配置选项
- 完善的错误处理

## 测试用例

创建了完整的测试用例，包括：
- 加解密功能测试
- 配置属性测试
- 集成测试
- 空值处理测试

## 依赖管理

更新了pom.xml文件，添加了必要的依赖：
- Spring Boot Starter
- MyBatis
- JSQLParser
- Hutool
- Lombok

## 注意事项

1. **密钥安全**: 请妥善保管加密密钥
2. **性能考虑**: SQL解析有性能开销，建议启用缓存
3. **兼容性**: 确保与MyBatis版本兼容
4. **测试覆盖**: 建议编写完整的测试用例

## 后续优化建议

1. 完善SQL解析逻辑，支持更复杂的SQL语法
2. 增加更多加密算法支持
3. 优化缓存策略
4. 增加监控和指标
5. 完善文档和示例

## 总结

成功实现了POJO模式的字段加解密功能，该功能具有无侵入性、高性能、高扩展性等特点，能够满足企业级应用的数据安全需求。通过策略模式和拦截器机制，实现了灵活的加解密处理，为securt-kit项目增加了重要的安全功能。
