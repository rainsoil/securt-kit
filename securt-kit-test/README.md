# Securt-Kit POJO模式加解密功能

## 功能概述

Securt-Kit提供了POJO模式的字段加解密功能，通过MyBatis拦截器实现无侵入式的数据加解密。该功能基于Java库的加解密算法，通过动态修改Mapper的入参和响应来实现字段自动加解密。

## 核心特性

- **无侵入性**：业务代码零侵入，仅需在实体类字段上标注注解
- **自动加解密**：入参自动加密，响应自动解密
- **策略模式**：支持自定义加解密算法
- **高性能**：智能跳过不需要处理的SQL
- **易扩展**：支持多种加解密策略共存

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.chu7.securtkit</groupId>
    <artifactId>securt-kit-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置属性

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

### 3. 实体类标注

```java
@Data
@TableName(value = "tb_user")
public class UserEntity {
    
    private Long id;
    
    @TableField(value = "user_name")
    private String userName;
    
    @TableField(value = "phone")
    @FieldEncryptor // 标识这个字段需要加密
    private String phone;
    
    @TableField(value = "email")
    @PoJoResultEncryptor // 标识这个字段需要解密
    private String email;
}
```

### 4. 使用示例

```java
@Mapper
public interface UserMapper {
    
    @Insert("INSERT INTO tb_user(user_name, phone, email) VALUES(#{userName}, #{phone}, #{email})")
    int insert(UserEntity user);
    
    @Select("SELECT * FROM tb_user WHERE phone = #{phone}")
    UserEntity selectByPhone(String phone);
}
```

## 注解说明

### @FieldEncryptor

用于标识需要加密的字段，可以指定自定义的加密策略。

```java
@FieldEncryptor // 使用默认策略
private String phone;

@FieldEncryptor(CustomEncryptorStrategy.class) // 使用自定义策略
private String sensitiveData;
```

### @PoJoResultEncryptor

用于标识需要解密的响应字段，主要用于POJO模式不兼容场景的强制解密。

```java
@PoJoResultEncryptor
private String email;
```

## 自定义加解密策略

### 1. 实现策略接口

```java
@Component
public class CustomEncryptorStrategy implements FieldEncryptorStrategy<String> {
    
    @Override
    public String encryption(String plaintext) {
        // 自定义加密逻辑
        return encrypt(plaintext);
    }
    
    @Override
    public String decryption(String ciphertext) {
        // 自定义解密逻辑
        return decrypt(ciphertext);
    }
}
```

### 2. 在字段上使用

```java
@FieldEncryptor(CustomEncryptorStrategy.class)
private String sensitiveData;
```

## 配置说明

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `securt-kit.encryptor.enabled` | boolean | true | 是否启用加密器 |
| `securt-kit.encryptor.mode` | string | pojo | 加密模式 |
| `securt-kit.encryptor.secretKey` | string | - | 加密密钥 |
| `securt-kit.encryptor.scanEntityPackages` | list | [] | 扫描的实体类包路径 |
| `securt-kit.encryptor.cacheCapacity` | int | 100 | SQL解析缓存容量 |

## 注意事项

1. **密钥安全**：请妥善保管加密密钥，建议使用环境变量或配置中心管理
2. **性能考虑**：SQL解析有性能开销，建议启用缓存
3. **兼容性**：确保与MyBatis版本兼容
4. **测试覆盖**：建议编写完整的测试用例

## 测试示例

```java
@SpringBootTest
@TestPropertySource(properties = {
    "securt-kit.encryptor.enabled=true",
    "securt-kit.encryptor.mode=pojo",
    "securt-kit.encryptor.secretKey=test-secret-key-123456"
})
public class EncryptorTest {
    
    @Resource
    private UserMapper userMapper;
    
    @Test
    public void testInsertAndSelect() {
        UserEntity user = new UserEntity();
        user.setUserName("测试用户");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        
        // 插入用户（phone字段会被自动加密）
        int result = userMapper.insert(user);
        assertEquals(1, result);
        
        // 根据手机号查询用户（phone字段会被自动解密）
        UserEntity foundUser = userMapper.selectByPhone("13800138000");
        assertNotNull(foundUser);
        assertEquals("13800138000", foundUser.getPhone());
    }
}
```

## 技术实现

- **拦截器**：基于MyBatis拦截器实现
- **SQL解析**：使用JSQLParser解析SQL语句
- **缓存机制**：LRU缓存提高性能
- **策略模式**：支持多种加解密算法
- **反射操作**：动态处理字段值

## 版本信息

- 当前版本：1.0.0-SNAPSHOT
- 最低Java版本：8
- 最低Spring Boot版本：2.7.18
- 最低MyBatis版本：3.5.13