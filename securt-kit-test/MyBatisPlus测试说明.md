# MyBatis-Plus POJO加密测试说明

本文档说明如何使用提供的测试类来验证POJO模式下的字段加密功能。

## 测试类说明

### 1. SimpleMyBatisPlusTest.java
**功能**: 完整的单元测试类，包含各种测试场景
**特点**: 
- 使用JUnit 5框架
- 包含断言验证
- 测试各种CRUD操作
- 验证加密解密功能

**运行方式**:
```bash
# 运行单个测试类
mvn test -Dtest=SimpleMyBatisPlusTest

# 运行特定测试方法
mvn test -Dtest=SimpleMyBatisPlusTest#testBasicInsertAndSelect
```

### 2. PojoEncryptionDemo.java
**功能**: 演示类，展示POJO加密的各种功能
**特点**:
- 实现CommandLineRunner接口
- 启动时自动运行
- 详细的日志输出
- 适合学习和调试

**运行方式**:
```bash
# 运行演示程序
mvn spring-boot:run -f securt-kit-test/pom.xml
```

## 测试场景

### 1. 基本CRUD操作测试
- **插入操作**: 验证敏感字段在插入时被正确加密
- **查询操作**: 验证敏感字段在查询时被正确解密
- **更新操作**: 验证敏感字段在更新时被重新加密
- **删除操作**: 验证删除功能正常工作

### 2. 加密策略测试
- **密码字段**: 使用MD5加密（32位十六进制字符串）
- **手机号字段**: 使用DES加密（Base64编码）
- **邮箱字段**: 使用AES加密（十六进制编码）
- **身份证字段**: 使用Base64加密

### 3. 数据一致性测试
- **多次查询**: 验证多次查询结果的一致性
- **不同查询方式**: 验证通过不同条件查询的结果一致性
- **加密解密**: 验证加密后存储、解密后读取的数据一致性

### 4. 批量操作测试
- **批量插入**: 验证批量插入时的字段加密
- **批量查询**: 验证批量查询时的字段解密
- **批量更新**: 验证批量更新时的字段重新加密

## 预期结果

### 插入操作
```
原始密码: 123456
加密后密码: e10adc3949ba59abbe56e057f20f883e

原始手机号: 13800138888
加密后手机号: 42064beb768e1de14f79782d792c60b5

原始邮箱: demo@example.com
加密后邮箱: 6f9f1b7ead31000970098dd70976d910e01a319ab10fc33bb5b25842dc063c27

原始身份证: 110101199001018888
加密后身份证: MTEwMTAxMTk5MDAxMDE4ODg4
```

### 查询操作
```
查询后手机号: 13800138888 (解密后)
查询后邮箱: demo@example.com (解密后)
查询后身份证: 110101199001018888 (解密后)
```

## 配置要求

### 1. 数据库配置
确保在 `application-test.yml` 中配置了正确的数据库连接：
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: SA
    password: 
```

### 2. 加密配置
确保在 `application-test.yml` 中启用了POJO模式：
```yaml
securt-kit:
  encryptor:
    pattern-type: pojo
    secret-key: mySecretKey123456
    scan-entity-package: com.chu7.securtkit.test
    enable-pojo-param-interceptor: true
    enable-pojo-result-interceptor: true
```

### 3. 实体类配置
确保 `User` 实体类正确配置了加密注解：
```java
@FieldEncryptor(value = Md5PoJoFieldEncryptorStrategy.class)
private String password;

@FieldEncryptor(value = DefaultPoJoFieldEncryptorPattern.class)
private String phone;

@FieldEncryptor(value = AesPoJoFieldEncryptorStrategy.class)
private String email;

@FieldEncryptor(value = Base64PoJoFieldEncryptorStrategy.class)
private String idCard;
```

## 故障排除

### 1. 字段没有被加密
**可能原因**:
- 拦截器没有正确注册
- 配置属性名称错误
- 实体类注解配置错误

**解决方案**:
- 检查拦截器注册状态
- 验证配置文件
- 确认注解配置

### 2. 字段没有被解密
**可能原因**:
- 解密拦截器没有工作
- 加密策略配置错误
- 数据库中的数据不是加密格式

**解决方案**:
- 检查解密拦截器
- 验证加密策略
- 确认数据格式

### 3. 编译错误
**可能原因**:
- 依赖版本不兼容
- 方法签名不匹配
- 导入包错误

**解决方案**:
- 检查依赖版本
- 更新方法调用
- 修正导入语句

## 扩展测试

### 1. 添加新的测试场景
可以在现有测试类中添加新的测试方法：
```java
@Test
public void testCustomScenario() {
    // 自定义测试逻辑
}
```

### 2. 测试其他实体类
可以创建其他实体类来测试不同的加密配置：
```java
@Entity
@TableName("other_table")
public class OtherEntity {
    @FieldEncryptor(value = CustomEncryptorStrategy.class)
    private String sensitiveField;
}
```

### 3. 性能测试
可以添加性能测试来验证加密解密的性能影响：
```java
@Test
public void testPerformance() {
    long startTime = System.currentTimeMillis();
    // 执行大量操作
    long endTime = System.currentTimeMillis();
    log.info("操作耗时: {}ms", endTime - startTime);
}
```

## 总结

这些测试类提供了完整的POJO加密功能验证，包括：
- 基本CRUD操作
- 各种加密策略
- 数据一致性
- 批量操作
- 错误处理

通过运行这些测试，可以确保POJO加密功能正常工作，敏感数据得到有效保护。
