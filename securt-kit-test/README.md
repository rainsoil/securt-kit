# SecurtKit 测试项目

本项目是 SecurtKit 的测试项目，用于验证字段加密解密功能的正确性。

## 技术栈

- Spring Boot 2.7.18
- MyBatis Plus 3.5.3.1
- H2 Database
- JUnit 5

## 功能特性

1. **数据库模式加密**: 使用数据库函数进行加密解密
2. **POJO模式加密**: 在应用层进行加密解密
3. **预处理SQL支持**: 支持 `INSERT INTO user (id,age) VALUES (?,?)` 格式
4. **复杂SQL支持**: 支持JOIN、子查询等复杂场景

## 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=BasicEncryptTest
mvn test -Dtest=MyBatisPlusIntegrationTest
mvn test -Dtest=PreparedStatementSqlTest
```

## 配置说明

测试环境使用H2内存数据库，配置文件位于：
- `src/main/resources/application.yml` - 主配置
- `src/main/resources/application-test.yml` - 测试配置
- `src/main/resources/db/schema.sql` - 数据库表结构
- `src/main/resources/db/data.sql` - 测试数据 