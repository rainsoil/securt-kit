# securt-kit 数据库字段加密解密工具

## 项目简介

`securt-kit` 是一个基于 Spring Boot + MyBatis 的数据库字段加密解密解决方案，支持对指定表的指定字段进行自动加密存储和查询解密。

## 核心特性

✅ **零侵入性** - 业务代码完全无需修改，仅需注解标注  
✅ **双重模式** - 支持 DB 模式（数据库函数）和 POJO 模式（Java库）  
✅ **自动拦截** - 通过 MyBatis 拦截器自动处理加密解密  
✅ **智能缓存** - 缓存表字段信息，避免重复解析  
✅ **兼容性强** - 同时支持 MyBatis 和 MyBatis Plus  
✅ **配置灵活** - 支持配置文件和注解两种方式  

## 快速开始

### 1. 添加依赖

#### Maven
```xml
<dependency>
    <groupId>com.chu7.securtkit</groupId>
    <artifactId>securt-kit-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### Gradle
```gradle
implementation 'com.chu7.securtkit:securt-kit-starter:1.0-SNAPSHOT'
```

### 2. 配置加密

#### application.yml
```yaml
securt-kit:
  encrypt:
    enabled: true
    algorithm: AES
    key: ${ENCRYPT_KEY:your-secret-key-16-chars}
    patternType: DB  # DB 或 POJO
    fields:
      user:
        - phone
        - email
        - id_card
    scanEntityPackages:
      - com.example.entity
```

#### application.properties
```properties
securt-kit.encrypt.enabled=true
securt-kit.encrypt.algorithm=AES
securt-kit.encrypt.key=your-secret-key-16-chars
securt-kit.encrypt.patternType=DB
securt-kit.encrypt.fields.user[0]=phone
securt-kit.encrypt.fields.user[1]=email
securt-kit.encrypt.fields.user[2]=id_card
securt-kit.encrypt.scanEntityPackages[0]=com.example.entity
```

### 3. 实体类标注

```java
@Data
@TableName(value = "user")
public class UserEntity {
    
    private Long id;
    
    private String username;
    
    @EncryptField
    private String phone;
    
    @EncryptField
    private String email;
    
    @EncryptField(algorithm = "AES")
    private String idCard;
    
    private String address;
}
```

### 4. 正常使用

```java
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public void createUser(UserEntity user) {
        // phone、email、idCard 字段会自动加密
        userMapper.insert(user);
    }
    
    public UserEntity getUserById(Long id) {
        // phone、email、idCard 字段会自动解密
        return userMapper.selectById(id);
    }
    
    public List<UserEntity> getUsersByPhone(String phone) {
        // 查询条件会自动加密
        return userMapper.selectByPhone(phone);
    }
}
```

## 加密模式说明

### DB 模式（推荐）

- **特点**: 依赖数据库的加解密函数，通过 SQL 改写实现
- **优点**: 
  - 支持密文模糊查询
  - 加解密场景适应性强
  - 支持列运算的字符加解密
- **缺点**: 需要数据库支持相应的加密函数

#### SQL 改写示例
```sql
-- 原始SQL
SELECT phone, email, username FROM user WHERE phone = ?

-- 改写后
SELECT 
  CAST(AES_DECRYPT(FROM_BASE64(phone), 'key') AS CHAR) AS phone,
  CAST(AES_DECRYPT(FROM_BASE64(email), 'key') AS CHAR) AS email,
  username 
FROM user 
WHERE phone = TO_BASE64(AES_ENCRYPT(?, 'key'))
```

### POJO 模式

- **特点**: 依赖 Java 库的加解密算法，通过参数和结果拦截实现
- **优点**: 
  - 加解密算法可选择性强
  - 不依赖数据库函数
- **缺点**: 
  - 不支持密文模糊查询
  - 部分场景不兼容

## 配置说明

### 核心配置项

| 配置项 | 说明 | 默认值 | 必填 |
|--------|------|--------|------|
| `enabled` | 是否启用加密功能 | `true` | 否 |
| `algorithm` | 默认加密算法 | `AES` | 否 |
| `key` | 加密密钥 | - | 是 |
| `patternType` | 加密模式 | `DB` | 否 |
| `fields` | 需要加密的字段配置 | - | 否 |
| `scanEntityPackages` | 扫描实体类包路径 | - | 否 |
| `excludeTables` | 排除加密的表 | - | 否 |
| `enableCache` | 是否启用缓存 | `true` | 否 |

### 字段配置

```yaml
securt-kit:
  encrypt:
    fields:
      # 表名 -> 字段列表
      user:
        - phone
        - email
        - id_card
      order:
        - customer_name
        - customer_phone
        - address
```

### 注解配置

```java
@EncryptField                    // 使用默认AES算法
private String phone;

@EncryptField(algorithm = "AES") // 指定AES算法
private String email;

@EncryptField(algorithm = "DES") // 指定DES算法
private String idCard;

@EncryptField(enabled = false)   // 禁用加密
private String remark;
```

## 扩展功能

### 自定义加密算法

```java
@Component
public class CustomEncryptStrategy implements EncryptStrategy {
    
    @Override
    public String encrypt(String plainText, String key) {
        // 实现自定义加密逻辑
        return customEncrypt(plainText, key);
    }
    
    @Override
    public String decrypt(String cipherText, String key) {
        // 实现自定义解密逻辑
        return customDecrypt(cipherText, key);
    }
    
    @Override
    public String getAlgorithm() {
        return "CUSTOM";
    }
    
    @Override
    public boolean supports(String algorithm) {
        return "CUSTOM".equalsIgnoreCase(algorithm);
    }
}
```

### 自定义脱敏策略

```java
@Component
public class CustomDesensitizeStrategy implements DesensitizeStrategy {
    
    @Override
    public String desensitize(String value, String type) {
        // 实现自定义脱敏逻辑
        return customDesensitize(value, type);
    }
    
    @Override
    public boolean supports(String type) {
        return "CUSTOM".equalsIgnoreCase(type);
    }
}
```

## 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=FieldEncryptorTest

# 运行特定测试方法
mvn test -Dtest=FieldEncryptorTest#testAesEncryptStrategy
```

### 测试配置

测试使用 H2 内存数据库，配置文件位于 `src/test/resources/application-test.yml`。

## 注意事项

1. **密钥管理**: 密钥不应硬编码在代码中，建议使用环境变量或配置中心
2. **数据库兼容**: DB 模式需要数据库支持相应的加密函数
3. **性能考虑**: 大量数据加密解密可能影响性能，建议使用缓存
4. **字段类型**: 确保数据库字段类型支持存储加密后的数据

## 技术架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   应用层        │    │   拦截器层      │    │   加密层        │
│                 │    │                 │    │                 │
│ - Controller    │───▶│ - SqlInterceptor│───▶│ - Encryptor     │
│ - Service       │    │ - TypeHandler   │    │ - Decryptor     │
│ - Mapper        │    │ - Plugin        │    │ - KeyManager    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   配置层        │
                       │                 │
                       │ - Properties    │
                       │ - Annotations   │
                       └─────────────────┘
```

## 项目结构

```
securt-kit/
├── securt-kit-core/           # 核心功能模块
│   ├── annotation/            # 注解定义
│   ├── config/                # 配置类
│   ├── core/                  # 核心功能
│   ├── interceptor/           # 拦截器实现
│   ├── visitor/               # 访问者模式实现
│   ├── strategy/              # 加密策略
│   ├── cache/                 # 缓存管理
│   └── util/                  # 工具类
├── securt-kit-starter/        # 自动配置启动器
└── docs/                      # 文档
```

## 版本历史

- **1.0-SNAPSHOT**: 初始版本，支持基本的字段加密解密功能

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。

## 许可证

本项目采用 MIT 许可证。
