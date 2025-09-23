# 复杂查询加密测试类说明

## 概述

本目录包含了多个测试类，用于验证 `FieldEncryptionService` 在各种复杂SQL场景下的表现。这些测试类模拟了真实的业务场景，包括多表关联、字段别名、聚合查询等复杂情况。

## 测试类列表

### 1. ComplexQueryEncryptionTest.java
**用途**：测试基本的复杂查询场景

**测试内容**：
- 多表关联查询（LEFT JOIN）
- 表别名和字段别名处理
- 子查询场景
- 聚合查询（COUNT, MAX, AVG, GROUP_CONCAT）
- 复杂条件查询（WHERE, ORDER BY, LIMIT）
- Map结果处理（模拟MyBatis返回的Map结果）

**关键特性**：
- 支持字段别名映射
- 处理大小写转换问题
- 验证数据一致性

### 2. AdvancedSqlEncryptionTest.java
**用途**：测试高级SQL场景

**测试内容**：
- 复杂的多表JOIN查询with别名
- CASE WHEN语句
- 窗口函数（ROW_NUMBER, RANK, LAG, LEAD）
- 动态SQL处理
- 嵌套子查询
- UNION查询
- GROUP BY with HAVING
- ORDER BY with LIMIT

**关键特性**：
- 支持复杂的SQL语法
- 处理动态字段映射
- 支持窗口函数和聚合函数

### 3. MyBatisInterceptorIntegrationTest.java
**用途**：测试MyBatis拦截器集成场景

**测试内容**：
- 参数加密（模拟PoJoParamEncryptorInterceptor）
- 结果解密（模拟PoJoResultDecryptorInterceptor）
- 批量操作处理
- 复杂查询结果处理
- Map参数处理
- Wrapper参数处理（MyBatis-Plus）

**关键特性**：
- 模拟真实的MyBatis拦截器场景
- 支持各种参数类型
- 处理Wrapper类参数

## 测试场景说明

### 1. 多表关联查询
```sql
SELECT 
    u.id as user_id,
    u.username as user_name,
    u.phone as user_phone,
    p.real_name as profile_real_name,
    p.id_card as profile_id_card
FROM users u
LEFT JOIN user_profiles p ON u.id = p.user_id
```

**测试要点**：
- 字段别名映射
- 多表字段的加密策略
- 数据一致性验证

### 2. 聚合查询
```sql
SELECT 
    u.username,
    COUNT(o.id) as order_count,
    GROUP_CONCAT(DISTINCT o.customer_name) as customer_names
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.username
```

**测试要点**：
- 聚合字段中的敏感信息处理
- GROUP_CONCAT结果的加密/解密
- 复杂字段映射

### 3. 窗口函数
```sql
SELECT 
    u.id,
    u.username,
    u.phone,
    ROW_NUMBER() OVER (PARTITION BY u.department ORDER BY u.create_time DESC) as row_num,
    LAG(u.phone, 1) OVER (ORDER BY u.id) as prev_phone
FROM users u
```

**测试要点**：
- 窗口函数结果的处理
- 前后行数据的加密/解密
- 复杂字段映射关系

### 4. 动态SQL
**测试要点**：
- 动态表名和字段名
- 不同前缀的字段映射
- 灵活的配置处理

## 数据模型

### 用户表 (users)
```java
@Data
public class User {
    private Long id;
    private String username;
    
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String password;
    
    @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
    private String phone;
    
    @FieldEncryptor(Md5PoJoFieldEncryptorStrategy.class)
    private String email;
    
    private String address;
    private Integer age;
}
```

### 用户详情表 (user_profiles)
```java
@Data
public class UserProfile {
    private Long id;
    private Long userId;
    
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String realName;
    
    @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
    private String idCard;
    
    private String gender;
    private String occupation;
}
```

### 订单表 (orders)
```java
@Data
public class Order {
    private Long id;
    private Long userId;
    private String orderNo;
    
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String customerName;
    
    @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
    private String customerPhone;
    
    private Double amount;
    private String status;
}
```

## 加密策略配置

### 字段映射关系
```java
Map<String, Class<? extends FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
fieldMappings.put("password", AesPoJoFieldEncryptorStrategy.class);
fieldMappings.put("phone", Base64PoJoFieldEncryptorStrategy.class);
fieldMappings.put("email", Md5PoJoFieldEncryptorStrategy.class);
fieldMappings.put("real_name", AesPoJoFieldEncryptorStrategy.class);
fieldMappings.put("id_card", Base64PoJoFieldEncryptorStrategy.class);
fieldMappings.put("customer_name", AesPoJoFieldEncryptorStrategy.class);
```

### 别名处理
```java
// 处理字段别名
fieldMappings.put("user_phone", Base64PoJoFieldEncryptorStrategy.class);
fieldMappings.put("profile_real_name", AesPoJoFieldEncryptorStrategy.class);
fieldMappings.put("order_customer_name", AesPoJoFieldEncryptorStrategy.class);
```

## 运行测试

### 1. 运行单个测试类
```bash
# 运行基本复杂查询测试
mvn spring-boot:run -f securt-kit-test/pom.xml -Dspring-boot.run.main-class=com.chu7.securtkit.test.ComplexQueryEncryptionTest

# 运行高级SQL测试
mvn spring-boot:run -f securt-kit-test/pom.xml -Dspring-boot.run.main-class=com.chu7.securtkit.test.AdvancedSqlEncryptionTest

# 运行MyBatis拦截器集成测试
mvn spring-boot:run -f securt-kit-test/pom.xml -Dspring-boot.run.main-class=com.chu7.securtkit.test.MyBatisInterceptorIntegrationTest
```

### 2. 运行所有测试
```bash
mvn test -f securt-kit-test/pom.xml
```

## 测试验证

### 1. 数据一致性验证
每个测试都会验证加密前后的数据一致性：
```java
// 验证数据一致性
for (int i = 0; i < original.size(); i++) {
    Map<String, Object> orig = original.get(i);
    Map<String, Object> proc = processed.get(i);
    
    for (String key : orig.keySet()) {
        if (orig.get(key) instanceof String && proc.containsKey(key)) {
            assert orig.get(key).equals(proc.get(key)) : 
                String.format("字段 %s 数据不一致", key);
        }
    }
}
```

### 2. 字段映射验证
验证字段映射关系是否正确：
```java
// 验证字段映射
assert fieldMappings.containsKey("password");
assert fieldMappings.containsKey("phone");
assert fieldMappings.containsKey("email");
```

### 3. 加密策略验证
验证加密策略是否正确应用：
```java
// 验证加密策略
FieldEncryptorStrategy<String> strategy = EncryptorInstanceCache.getInstance(strategyClass);
String encrypted = strategy.encryption(original);
String decrypted = strategy.decryption(encrypted);
assert original.equals(decrypted);
```

## 注意事项

### 1. 字段别名处理
- 需要正确处理数据库字段名和Java字段名的映射
- 支持下划线转驼峰的转换
- 处理大小写转换问题

### 2. 复杂查询结果
- 聚合查询结果中的敏感信息需要特殊处理
- 窗口函数结果可能包含前后行的敏感数据
- 子查询结果需要递归处理

### 3. 性能考虑
- 大量数据的批量处理需要考虑性能
- 复杂查询的字段映射需要优化
- 缓存策略需要合理配置

### 4. 错误处理
- 字段映射失败时的错误处理
- 加密策略不存在时的回退机制
- 数据格式不匹配时的处理

## 扩展测试

### 1. 添加新的测试场景
```java
public void testNewScenario() {
    log.info("=== 测试新场景 ===");
    
    // 创建测试数据
    List<Map<String, Object>> data = createTestData();
    
    // 定义字段映射
    Map<String, Class<? extends FieldEncryptorStrategy<String>>> fieldMappings = new HashMap<>();
    // ... 配置字段映射
    
    // 执行加密/解密
    List<Map<String, Object>> encrypted = processMapResults(data, fieldMappings, "加密");
    List<Map<String, Object>> decrypted = processMapResults(encrypted, fieldMappings, "解密");
    
    // 验证结果
    validateDataConsistency(data, decrypted);
    
    log.info("新场景测试通过！");
}
```

### 2. 添加新的数据模型
```java
@Data
public class NewEntity {
    private Long id;
    
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String sensitiveField;
    
    private String normalField;
}
```

### 3. 添加新的加密策略
```java
public class NewEncryptionStrategy implements FieldEncryptorStrategy<String> {
    @Override
    public String encryption(String plaintext) {
        // 实现加密逻辑
        return encryptedText;
    }
    
    @Override
    public String decryption(String ciphertext) {
        // 实现解密逻辑
        return plaintext;
    }
}
```

## 总结

这些测试类全面覆盖了 `FieldEncryptionService` 在各种复杂SQL场景下的使用情况，确保：

1. **功能完整性**：覆盖了所有主要的SQL场景
2. **数据一致性**：验证加密前后的数据一致性
3. **性能稳定性**：测试大量数据和复杂查询的性能
4. **错误处理**：验证各种异常情况的处理
5. **扩展性**：提供了扩展测试的框架和示例

通过这些测试，可以确保 `FieldEncryptionService` 在生产环境中的稳定性和可靠性。
