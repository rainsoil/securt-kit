# POJO模式加密功能说明

## 概述

POJO模式是 securt-kit 安全工具包提供的一种数据加密模式，通过在 MyBatis 拦截器层面对入参和结果进行加解密处理，实现敏感数据的自动保护。

## 特性

- **零侵入**: 业务代码无需修改，仅需在实体类字段上添加注解
- **自动加解密**: 自动识别需要加密的字段，在数据入库时加密，查询时解密
- **多种加密算法**: 支持 DES、AES、Base64、MD5 等多种加密策略
- **高性能**: 基于 MyBatis 拦截器实现，性能开销小
- **灵活配置**: 支持多种配置方式，满足不同场景需求

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.chu7.securtkit</groupId>
    <artifactId>securt-kit-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置启用

在 `application.yml` 中配置：

```yaml
securt-kit:
  encryptor:
    enabled: true
    mode: pojo
    secretKey: your-secret-key
    scanEntityPackages:
      - com.example.entity
  param-encrypt-enabled: true
  result-decrypt-enabled: true
```

### 3. 实体类配置

在需要加密的字段上添加 `@FieldEncryptor` 注解：

```java
@Data
@TableName("user")
public class User {
    @TableId
    private Long id;
    
    private String userName;
    
    @FieldEncryptor
    private String phone;
    
    @FieldEncryptor
    private String email;
    
    @FieldEncryptor
    private String idCard;
}
```

### 4. 使用示例

```java
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public User saveUser(User user) {
        // 插入时自动加密敏感字段
        userMapper.insert(user);
        return user;
    }
    
    public User getUserById(Long id) {
        // 查询时自动解密敏感字段
        return userMapper.selectById(id);
    }
}
```

## 配置说明

### 基本配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `securt-kit.encryptor.enabled` | 是否启用加密功能 | `true` |
| `securt-kit.encryptor.mode` | 加密模式 | `pojo` |
| `securt-kit.encryptor.secretKey` | 加密密钥 | 必填 |
| `securt-kit.param-encrypt-enabled` | 是否启用参数加密 | `true` |
| `securt-kit.result-decrypt-enabled` | 是否启用结果解密 | `true` |

### 高级配置

```yaml
securt-kit:
  encryptor:
    algorithm: DES  # 加密算法：DES、AES、Base64、MD5
    secretKey: your-secret-key
    scanEntityPackages:
      - com.example.entity
    cacheCapacity: 1000
  pojo-mode:
    enabled: true
    default-strategy: DES
```

## 注解说明

### @FieldEncryptor

用于标记需要加密的字段：

```java
@FieldEncryptor(value = DefaultPoJoFieldEncryptorPattern.class)
private String sensitiveData;
```

**属性说明：**
- `value`: 指定加密策略类，默认为 `DefaultPoJoFieldEncryptorPattern`

### @PoJoResultEncryptor

用于在响应结果中强制指定解密：

```java
@PoJoResultEncryptor(value = AesPoJoFieldEncryptorStrategy.class)
private String responseData;
```

## 加密策略

### 1. DES 加密（默认）

```java
@FieldEncryptor(value = DefaultPoJoFieldEncryptorPattern.class)
private String data;
```

### 2. AES 加密

```java
@FieldEncryptor(value = AesPoJoFieldEncryptorStrategy.class)
private String data;
```

### 3. Base64 编码

```java
@FieldEncryptor(value = Base64PoJoFieldEncryptorStrategy.class)
private String data;
```

### 4. MD5 哈希

```java
@FieldEncryptor(value = Md5PoJoFieldEncryptorStrategy.class)
private String data;
```

## 测试

### 单元测试

```java
@SpringBootTest
@ActiveProfiles("test")
public class PoJoEncryptionTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testEncryption() {
        User user = new User();
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        
        // 保存时自动加密
        userService.saveUser(user);
        
        // 查询时自动解密
        User queriedUser = userService.getUserById(user.getId());
        assertEquals("13800138000", queriedUser.getPhone());
        assertEquals("test@example.com", queriedUser.getEmail());
    }
}
```

### 性能测试

```java
@Test
public void testPerformance() {
    long startTime = System.currentTimeMillis();
    
    for (int i = 0; i < 1000; i++) {
        User user = new User();
        user.setPhone("13800138000");
        userService.saveUser(user);
    }
    
    long endTime = System.currentTimeMillis();
    System.out.println("1000次操作耗时: " + (endTime - startTime) + "ms");
}
```

## 注意事项

1. **密钥管理**: 请妥善保管加密密钥，建议使用环境变量或配置中心管理
2. **性能考虑**: 大量数据操作时，加密解密会有一定性能开销
3. **数据迁移**: 从明文数据迁移到加密数据时，需要先解密再重新加密
4. **索引问题**: 加密后的数据无法直接用于数据库索引和查询
5. **兼容性**: 确保 MyBatis 版本兼容性

## 故障排除

### 常见问题

1. **加密不生效**
   - 检查注解是否正确添加
   - 确认配置是否正确
   - 查看日志是否有错误信息

2. **解密失败**
   - 检查密钥是否正确
   - 确认加密算法是否一致
   - 验证数据是否被正确加密

3. **性能问题**
   - 考虑使用缓存
   - 优化查询语句
   - 调整批处理大小

### 日志配置

```yaml
logging:
  level:
    com.chu7.securtkit: DEBUG
    org.springframework: INFO
```

## 最佳实践

1. **字段选择**: 只对真正敏感的字段进行加密
2. **密钥轮换**: 定期更换加密密钥
3. **监控告警**: 监控加密解密操作的成功率
4. **测试覆盖**: 确保测试覆盖各种场景
5. **文档维护**: 及时更新相关文档

## 更新日志

- v1.0.0: 初始版本，支持基本的 POJO 模式加密
- v1.0.1: 新增多种加密策略支持
- v1.0.2: 优化性能，修复已知问题

## 技术支持

如有问题，请联系开发团队或查看项目文档。
