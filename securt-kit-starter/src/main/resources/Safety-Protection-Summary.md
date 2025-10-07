# 安全防护功能实现总结

## 功能概述

基于Spring Boot Filter实现了完整的安全防护功能，包括：

1. **XSS防护** - 防止跨站脚本攻击
2. **SQL注入防护** - 防止SQL注入攻击
3. **敏感词过滤** - 过滤敏感词汇

## 实现架构

```
securt-kit-starter/
├── src/main/java/com/chu7/securtkit/safety/
│   ├── config/
│   │   ├── SafetyConfig.java              # 安全配置类
│   │   └── SafetyAutoConfiguration.java   # 自动配置类
│   ├── filter/
│   │   ├── SafetyFilter.java              # 主过滤器
│   │   └── SafetyHttpServletRequestWrapper.java # 请求包装器
│   └── util/
│       ├── XssUtil.java                    # XSS防护工具
│       ├── SqlInjectionUtil.java          # SQL注入防护工具
│       └── SensitiveWordUtil.java         # 敏感词过滤工具
└── src/main/resources/
    ├── META-INF/spring.factories          # 自动配置文件
    ├── application-safety-example.yml     # 示例配置
    ├── Safety-Protection-Usage.md         # 使用文档
    └── sensitive-words/                   # 敏感词文件目录
        ├── political.txt                  # 政治敏感词
        ├── violence.txt                   # 暴力敏感词
        ├── pornography.txt               # 色情敏感词
        └── custom.txt                     # 自定义敏感词
```

## 核心功能

### 1. XSS防护 (XssUtil)

**功能特性：**
- 检测常见的XSS攻击模式
- 支持HTML标签和属性白名单
- 支持过滤、编码、阻止三种模式
- 支持URL和参数排除配置

**检测模式：**
- 脚本标签：`<script>`, `<iframe>`, `<object>`
- 事件处理器：`onclick`, `onload`, `onerror`
- JavaScript协议：`javascript:`, `vbscript:`
- 表达式：`expression()`
- 数据URI：`data:text/html`

### 2. SQL注入防护 (SqlInjectionUtil)

**功能特性：**
- 检测危险SQL关键词
- 检测危险SQL模式
- 支持自定义危险关键词
- 支持SQL转义

**检测内容：**
- 危险关键词：`select`, `insert`, `update`, `delete`, `drop`
- 联合查询：`union select`
- 注释：`--`, `/* */`
- 存储过程：`exec`, `execute`
- 系统函数：`@@`, `$`
- 时间延迟：`waitfor delay`, `sleep()`

### 3. 敏感词过滤 (SensitiveWordUtil)

**功能特性：**
- 支持从classpath文件读取敏感词
- 支持多个敏感词文件
- 支持缓存机制
- 支持自定义替换字符

**文件格式：**
```
# 注释行以#开头
敏感词1
敏感词2
敏感词3
```

## 配置说明

### 主配置 (SafetyConfig)

```yaml
securt-kit:
  safety:
    enabled: true                    # 是否启用安全防护
    
    # XSS防护配置
    xss:
      enabled: true
      mode: FILTER                   # FILTER/ENCODE/BLOCK
      exclude-patterns: []           # 排除的URL模式
      exclude-params: []             # 排除的参数名
      allowed-tags: []               # 允许的HTML标签
      allowed-attributes: []         # 允许的HTML属性
    
    # SQL注入防护配置
    sql-injection:
      enabled: true
      exclude-patterns: []           # 排除的URL模式
      exclude-params: []             # 排除的参数名
      dangerous-keywords: []         # 自定义危险关键词
    
    # 敏感词过滤配置
    sensitive-word:
      enabled: true
      exclude-patterns: []           # 排除的URL模式
      exclude-params: []             # 排除的参数名
      word-files: []                 # 敏感词文件路径
      replace-char: "*"              # 替换字符
      cache-enabled: true            # 是否启用缓存
```

## 使用方式

### 1. 自动配置

添加依赖后，安全防护功能会自动启用：

```xml
<dependency>
    <groupId>com.chu7</groupId>
    <artifactId>securt-kit-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置排除规则

```yaml
# 排除特定URL
exclude-patterns:
  - /api/public/**
  - /static/**

# 排除特定参数
exclude-params:
  - content
  - description
```

### 3. 自定义敏感词

创建敏感词文件并配置：

```yaml
sensitive-word:
  word-files:
    - sensitive-words/custom.txt
```

## 技术实现

### 1. 过滤器链

```
请求 → SafetyFilter → 安全检查 → 继续过滤器链 → 响应
```

### 2. 请求处理流程

1. **参数检查** - 检查所有请求参数
2. **请求头检查** - 检查User-Agent等请求头
3. **XSS防护** - 检测和过滤XSS攻击
4. **SQL注入防护** - 检测和阻止SQL注入
5. **敏感词过滤** - 过滤敏感词汇

### 3. 性能优化

- **缓存机制** - 敏感词缓存，避免重复加载
- **排除规则** - 支持URL和参数排除，减少不必要的检查
- **异步处理** - 支持异步处理，不阻塞主流程

## 安全特性

### 1. 多层防护

- **输入验证** - 在请求入口进行安全检查
- **内容过滤** - 过滤危险内容
- **编码处理** - 对危险内容进行编码
- **阻止机制** - 直接阻止危险请求

### 2. 灵活配置

- **白名单机制** - 支持URL和参数白名单
- **自定义规则** - 支持自定义检测规则
- **动态配置** - 支持运行时配置更新

### 3. 监控和日志

- **详细日志** - 记录所有安全事件
- **统计信息** - 提供安全统计信息
- **告警机制** - 支持安全告警

## 扩展性

### 1. 自定义检测器

可以扩展自定义检测器：

```java
public class CustomSecurityDetector {
    public boolean detect(String input) {
        // 自定义检测逻辑
        return false;
    }
}
```

### 2. 自定义处理器

可以扩展自定义处理器：

```java
public class CustomSecurityProcessor {
    public String process(String input) {
        // 自定义处理逻辑
        return input;
    }
}
```

### 3. 插件机制

支持插件机制，可以动态加载安全插件。

## 最佳实践

### 1. 配置建议

- 合理设置排除规则，避免误拦截
- 定期更新敏感词库
- 监控安全日志，及时发现问题

### 2. 性能优化

- 使用缓存机制
- 合理配置排除规则
- 避免过度检查

### 3. 安全加固

- 定期更新安全规则
- 监控异常请求
- 建立安全响应机制

## 总结

本安全防护功能提供了完整的安全防护解决方案，具有以下特点：

1. **功能完整** - 覆盖XSS、SQL注入、敏感词等主要安全威胁
2. **配置灵活** - 支持细粒度的配置和排除规则
3. **性能优化** - 支持缓存和排除机制，性能影响最小
4. **易于使用** - 基于Spring Boot自动配置，开箱即用
5. **可扩展性** - 支持自定义检测器和处理器
6. **监控完善** - 提供详细的日志和统计信息

通过这个安全防护功能，可以有效保护Web应用免受常见的安全攻击。
