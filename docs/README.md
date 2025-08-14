# 数据库字段加密解密方案

## 概述

这是一个基于Spring Boot 2.x和MyBatis/MyBatis Plus的数据库字段加密解密解决方案，支持对指定表的指定字段进行自动加密存储和查询解密。

## 核心特性

- ✅ 支持配置文件方式配置需要加密的字段
- ✅ 支持注解方式标记需要加密的字段
- ✅ 自动拦截SQL执行，在插入/更新时加密，查询时解密
- ✅ 支持多种加密算法（AES、DES等）
- ✅ 支持自定义加密密钥管理
- ✅ 兼容MyBatis和MyBatis Plus
- ✅ 对业务代码无侵入，透明处理

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.chu7.securtkit</groupId>
    <artifactId>securt-kit-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置加密

```yaml
# application.yml
securt-kit:
  encrypt:
    enabled: true
    algorithm: AES
    key: ${ENCRYPT_KEY:your-secret-key-32-chars}
    fields:
      user_info:
        - phone
        - email
        - id_card
```

### 3. 使用注解

```java
public class UserInfo {
    @EncryptField
    private String phone;
    
    @EncryptField(algorithm = "AES")
    private String email;
    
    private String name; // 不加密字段
}
```

### 4. 正常使用

```java
@Service
public class UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    public void createUser(UserInfo userInfo) {
        // phone和email字段会自动加密
        userInfoMapper.insert(userInfo);
    }
    
    public UserInfo getUserById(Long id) {
        // phone和email字段会自动解密
        return userInfoMapper.selectById(id);
    }
}
```

## 文档

- [字段加密解密方案设计](./字段加密解密方案设计.md) - 完整的设计方案
- [实现指南](./实现指南.md) - 详细的实现代码
- [快速开始指南](./快速开始指南.md) - 快速上手教程

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

## 配置说明

### 配置文件方式

```yaml
securt-kit:
  encrypt:
    enabled: true
    algorithm: AES
    key: ${ENCRYPT_KEY:your-secret-key-32-chars}
    fields:
      user_info:
        - phone
        - email
        - id_card
      order_info:
        - customer_name
        - customer_phone
    exclude-tables:
      - system_config
```

### 注解方式

```java
@EncryptField                    // 使用默认AES算法
private String phone;

@EncryptField(algorithm = "AES") // 指定AES算法
private String email;

@EncryptField(algorithm = "DES") // 指定DES算法
private String idCard;
```

## 安全考虑

1. **密钥管理**: 密钥不应硬编码在代码中，建议使用环境变量或配置中心
2. **加密算法**: 默认使用AES-256加密算法，支持自定义
3. **性能优化**: 提供缓存机制，支持批量操作优化
4. **密钥轮换**: 支持密钥自动轮换机制

## 扩展功能

1. **多种加密算法**: 支持AES、DES、RSA等
2. **自定义加密器**: 实现Encryptor接口自定义加密算法
3. **自定义密钥管理**: 实现KeyManager接口自定义密钥管理
4. **缓存优化**: 支持加密解密结果缓存
5. **监控告警**: 支持加密解密操作监控

## 注意事项

1. 确保数据库字段类型支持存储加密后的数据（建议使用TEXT或VARCHAR）
2. 密钥长度要符合加密算法要求（AES需要16、24或32字节）
3. 大量数据加密解密可能影响性能，建议使用缓存
4. 注意与现有MyBatis配置的兼容性

## 示例项目

完整的示例项目请参考 `快速开始指南.md` 文档，包含：

- 完整的项目配置
- 实体类定义
- Mapper接口
- Service层实现
- Controller层实现
- 测试用例

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 许可证

本项目采用MIT许可证。 