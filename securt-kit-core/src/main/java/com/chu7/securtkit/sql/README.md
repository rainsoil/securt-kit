# SQL解析器模块

## 概述

SQL解析器模块是security-kit项目的核心组件之一，专门负责解析SQL语句并识别需要处理的字段。该模块基于JSqlParser实现，提供了统一的SQL解析和字段处理接口。

## 核心组件

### 1. FieldContext（字段上下文）
存储字段的解析信息，包括：
- 表名和字段名
- 字段位置（SELECT、WHERE、INSERT等）
- SQL类型
- 是否需要加密/解密
- 字段值（如果有）

### 2. SqlAnalysisResult（SQL分析结果）
存储SQL解析的完整信息，包括：
- 原始SQL和解析后的Statement
- 所有字段上下文
- 表字段映射
- 需要加密/解密的字段列表

### 3. SqlAnalyzer（SQL解析器接口）
定义SQL解析的标准方法：
- `analyze(String sql)` - 解析SQL语句
- `analyze(String sql, Object parameters)` - 解析带参数的SQL
- `needProcess(String sql)` - 判断是否需要处理

### 4. FieldProcessor（字段处理器接口）
定义字段处理的标准方法：
- `process(List<FieldContext> fieldContexts)` - 处理字段列表
- `process(FieldContext fieldContext)` - 处理单个字段
- `supports(FieldContext fieldContext)` - 判断是否支持

### 5. UnifiedFieldInterceptor（统一字段拦截器）
协调SQL解析和字段处理的核心组件：
- 拦截SQL执行
- 调用SQL解析器
- 调用字段处理器
- 应用处理结果

## 使用方法

### 1. 基本使用

```java
@Autowired
private UnifiedFieldInterceptor interceptor;

// 拦截并处理SQL
String processedSql = interceptor.intercept("SELECT * FROM users WHERE id = ?");
```

### 2. 自定义SQL解析器

```java
@Component
public class CustomSqlAnalyzer implements SqlAnalyzer {
    @Override
    public SqlAnalysisResult analyze(String sql) {
        // 实现自定义解析逻辑
    }
    
    // 实现其他方法...
}
```

### 3. 自定义字段处理器

```java
@Component
public class CustomFieldProcessor implements FieldProcessor {
    @Override
    public ProcessResult process(List<FieldContext> fieldContexts) {
        // 实现自定义处理逻辑
    }
    
    // 实现其他方法...
}
```

## 配置

模块支持Spring Boot自动配置，默认会注册：
- `DefaultSqlAnalyzer` - 默认SQL解析器
- `DefaultFieldProcessor` - 默认字段处理器
- `UnifiedFieldInterceptor` - 统一字段拦截器

如果需要自定义实现，只需要实现对应的接口并添加`@Component`注解即可。

## 扩展点

### 1. 字段识别策略
可以通过实现`SqlAnalyzer`接口来自定义字段识别策略，支持：
- 不同SQL类型的字段识别
- 复杂SQL语句的解析
- 自定义字段过滤规则

### 2. 字段处理策略
可以通过实现`FieldProcessor`接口来自定义字段处理策略，支持：
- 不同的加密/解密算法
- 字段值的转换逻辑
- SQL表达式的修改

### 3. 拦截器扩展
可以通过继承`UnifiedFieldInterceptor`来扩展拦截逻辑，支持：
- 自定义拦截规则
- 处理结果的定制化应用
- 性能优化和缓存

## 注意事项

1. **性能考虑**：SQL解析是CPU密集型操作，建议在生产环境中启用缓存
2. **错误处理**：解析失败时会返回原始SQL，确保系统稳定性
3. **扩展性**：模块设计为可扩展的，可以根据实际需求定制实现
4. **兼容性**：基于JSqlParser 4.9+，支持大部分标准SQL语法

## 未来规划

1. **缓存优化**：实现LRU缓存机制，提高解析性能
2. **语法支持**：扩展对更多SQL语法的支持
3. **性能监控**：添加性能指标和监控
4. **插件机制**：支持插件化的字段处理策略 