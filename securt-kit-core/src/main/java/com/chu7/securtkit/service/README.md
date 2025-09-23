# FieldEncryptionService 字段加密服务

## 概述

`FieldEncryptionService` 是一个统一的字段加密/解密服务类，旨在简化业务代码中的加密处理逻辑，提供更简洁、统一的API接口。

## 主要特性

- **混合配置支持**：同时支持注解和配置文件两种配置方式，配置文件优先级更高
- **自动字段识别**：基于 `@FieldEncryptor` 注解和 `TableCache` 配置自动识别需要加密的字段
- **多种加密策略**：支持AES、Base64、MD5等多种加密策略
- **批量处理**：支持批量加密/解密对象列表
- **Map处理**：支持Map类型数据的字段加密/解密
- **类型安全**：使用泛型确保类型安全
- **错误处理**：统一的异常处理和日志记录
- **配置验证**：提供配置验证和调试功能
- **向后兼容**：完全兼容现有的注解配置方式

## 快速开始

### 1. 基本使用

```java
@Service
public class UserService {
    
    @Autowired
    private FieldEncryptionService fieldEncryptionService;
    
    public User saveUser(User user) {
        // 自动加密敏感字段
        User encryptedUser = fieldEncryptionService.encryptObject(user);
        userMapper.insert(encryptedUser);
        return encryptedUser;
    }
    
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        // 自动解密敏感字段
        return fieldEncryptionService.decryptObject(user);
    }
}
```

### 2. 实体类配置

#### 方式1：注解配置（推荐用于简单场景）

```java
@Data
@TableName("test_user")
public class User {
    private Long id;
    private String username;
    
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String password;
    
    @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
    private String phone;
    
    @FieldEncryptor(Md5PoJoFieldEncryptorStrategy.class)
    private String email;
    
    private String address; // 无需加密
}
```

#### 方式2：配置文件配置（推荐用于复杂场景）

```yaml
securt-kit:
  field-encryptor:
    enabled: true
    default-strategy: com.chu7.securtkit.encryptor.pojo.DefaultPoJoFieldEncryptorPattern
    tables:
      test_user:
        enabled: true
        fields:
          password:
            strategy: com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy
            params:
              key: "custom-aes-key-1234567890123456"
          phone:
            strategy: com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy
          email:
            strategy: com.chu7.securtkit.encryptor.pojo.Md5PoJoFieldEncryptorStrategy
```

#### 方式3：混合配置（推荐用于生产环境）

```java
@Data
@TableName("test_user")
public class User {
    private Long id;
    private String username;
    
    // 注解配置
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String password;
    
    // 通过配置文件配置（优先级更高）
    private String phone;
    private String email;
    
    private String address; // 无需加密
}
```

**配置优先级**：配置文件 > 注解 > 默认策略

### 3. 批量处理

```java
public List<User> batchSaveUsers(List<User> users) {
    // 批量加密
    List<User> encryptedUsers = fieldEncryptionService.encryptObjects(users);
    userMapper.batchInsert(encryptedUsers);
    
    // 批量解密（如果需要返回给前端）
    return fieldEncryptionService.decryptObjects(encryptedUsers);
}
```

### 4. Map数据处理

```java
public Map<String, Object> processUserMap(Map<String, Object> userMap) {
    // 定义字段映射关系
    Map<String, Class<? extends FieldEncryptorStrategy<String>>> fieldMappings = Map.of(
        "password", AesPoJoFieldEncryptorStrategy.class,
        "phone", Base64PoJoFieldEncryptorStrategy.class,
        "email", Md5PoJoFieldEncryptorStrategy.class
    );
    
    // 加密Map中的敏感字段
    Map<String, Object> encryptedMap = fieldEncryptionService.encryptMap(userMap, fieldMappings);
    
    // 解密Map中的敏感字段
    return fieldEncryptionService.decryptMap(encryptedMap, fieldMappings);
}
```

## API 参考

### 核心方法

#### 单个对象处理

```java
// 加密单个对象
<T> T encryptObject(T obj)

// 解密单个对象
<T> T decryptObject(T obj)
```

#### 批量处理

```java
// 批量加密对象列表
<T> List<T> encryptObjects(List<T> objects)

// 批量解密对象列表
<T> List<T> decryptObjects(List<T> objects)
```

#### Map处理

```java
// 加密Map中的字段
Map<String, Object> encryptMap(Map<String, Object> map, 
    Map<String, Class<? extends FieldEncryptorStrategy<String>>> fieldMappings)

// 解密Map中的字段
Map<String, Object> decryptMap(Map<String, Object> map, 
    Map<String, Class<? extends FieldEncryptorStrategy<String>>> fieldMappings)
```

#### 配置查询

```java
// 检查是否有需要加密的字段
boolean hasEncryptFields(Class<?> clazz)

// 获取需要加密的字段数量
int getEncryptFieldCount(Class<?> clazz)

// 获取需要加密的字段名称列表
List<String> getEncryptFieldNames(Class<?> clazz)
```

## 在拦截器中使用

### 替换参数加密拦截器

```java
@Component
public class PoJoParamEncryptorInterceptor implements Interceptor {
    
    @Autowired
    private FieldEncryptionService fieldEncryptionService;
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        
        // 处理参数
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && fieldEncryptionService.hasEncryptFields(args[i].getClass())) {
                args[i] = fieldEncryptionService.encryptObject(args[i]);
            }
        }
        
        return invocation.proceed();
    }
}
```

### 替换结果解密拦截器

```java
@Component
public class PoJoResultDecryptorInterceptor implements Interceptor {
    
    @Autowired
    private FieldEncryptionService fieldEncryptionService;
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        
        if (result instanceof List) {
            List<?> resultList = (List<?>) result;
            if (!resultList.isEmpty() && fieldEncryptionService.hasEncryptFields(resultList.get(0).getClass())) {
                return fieldEncryptionService.decryptObjects(resultList);
            }
        } else if (result != null && fieldEncryptionService.hasEncryptFields(result.getClass())) {
            return fieldEncryptionService.decryptObject(result);
        }
        
        return result;
    }
}
```

## 支持的加密策略

- `AesPoJoFieldEncryptorStrategy` - AES加密
- `Base64PoJoFieldEncryptorStrategy` - Base64编码
- `Md5PoJoFieldEncryptorStrategy` - MD5哈希
- 自定义策略 - 实现 `FieldEncryptorStrategy<String>` 接口

## 错误处理

服务类提供统一的错误处理机制：

- **空值安全**：自动处理null对象和空集合
- **异常捕获**：捕获并记录加密/解密过程中的异常
- **日志记录**：提供详细的调试和错误日志
- **异常传播**：将底层异常包装为运行时异常

## 性能优化

- **策略缓存**：使用 `EncryptorInstanceCache` 缓存策略实例
- **反射优化**：缓存字段信息，减少反射调用
- **批量处理**：支持批量操作，减少方法调用开销
- **懒加载**：按需加载配置和策略

## 最佳实践

1. **统一使用**：在项目中统一使用 `FieldEncryptionService`，避免手动加密
2. **配置验证**：在应用启动时验证加密配置
3. **日志监控**：监控加密/解密的性能和错误
4. **测试覆盖**：为加密功能编写完整的单元测试
5. **文档维护**：及时更新加密字段的文档说明

## 迁移指南

### 从手动加密迁移

**迁移前：**
```java
public User saveUser(User user) {
    // 手动加密
    user.setPassword(encryptPassword(user.getPassword()));
    user.setPhone(encryptPhone(user.getPhone()));
    user.setEmail(encryptEmail(user.getEmail()));
    
    userMapper.insert(user);
    return user;
}
```

**迁移后：**
```java
public User saveUser(User user) {
    // 自动加密
    User encryptedUser = fieldEncryptionService.encryptObject(user);
    userMapper.insert(encryptedUser);
    return encryptedUser;
}
```

### 从拦截器迁移

**迁移前：**
```java
// 复杂的拦截器逻辑
private void disposeParam(BoundSql boundSql, Pair<Map<String, ColumnTableDto>, List<FieldEncryptorInfoDto>> pair) {
    // 复杂的参数处理逻辑
    for (int i = 0; i < parameterMappings.size(); i++) {
        // 获取字段加密策略
        FieldEncryptor fieldEncryptor = parseFieldEncryptor(placeholderKey, pair.getKey());
        if (propertyValue instanceof String && fieldEncryptor != null) {
            String ciphertext = EncryptorInstanceCache.<String>getInstance(fieldEncryptor.value()).encryption((String) propertyValue);
            // 设置加密后的值
        }
    }
}
```

**迁移后：**
```java
// 简化的拦截器逻辑
private void disposeParam(Object parameterObject) {
    if (parameterObject != null && fieldEncryptionService.hasEncryptFields(parameterObject.getClass())) {
        return fieldEncryptionService.encryptObject(parameterObject);
    }
    return parameterObject;
}
```

## 注意事项

1. **对象拷贝**：服务会创建对象副本，避免修改原对象
2. **字段类型**：目前只支持String类型的字段加密
3. **注解必需**：字段必须标注 `@FieldEncryptor` 注解才会被处理
4. **策略配置**：确保加密策略类已正确配置到Spring容器中
5. **性能考虑**：大量数据时建议使用批量处理方法

## 扩展开发

如需扩展功能，可以：

1. **自定义策略**：实现 `FieldEncryptorStrategy<String>` 接口
2. **添加方法**：在服务类中添加新的处理方法
3. **配置扩展**：扩展配置属性类
4. **监控集成**：集成监控和指标收集

## 版本历史

- **v1.0.0** - 初始版本，提供基本的字段加密/解密功能
- **v1.1.0** - 添加批量处理和Map处理功能
- **v1.2.0** - 添加配置验证和调试功能
