# 基于规则文件的安全防护功能实现总结

## 功能概述

已成功实现基于规则文件的安全防护功能，支持：

1. **规则文件配置** - 支持多个规则文件，每个规则文件支持多个规则
2. **内置规则** - 支持启用或禁用内置规则
3. **多种规则类型** - XSS防护、SQL注入防护、敏感词过滤
4. **灵活配置** - 支持URL模式排除、参数名排除、规则优先级
5. **多种动作** - FILTER、ENCODE、BLOCK、REPLACE

## 实现架构

```
securt-kit-starter/src/main/java/com/chu7/securtkit/safety/
├── config/
│   ├── SafetyConfig.java              # 安全配置类（支持规则配置）
│   └── SafetyAutoConfiguration.java   # 自动配置类
├── filter/
│   ├── SafetyFilter.java              # 主安全过滤器（支持规则系统）
│   └── SafetyHttpServletRequestWrapper.java # 请求包装器
├── rule/
│   ├── SecurityRule.java              # 安全规则类
│   ├── SecurityRuleSet.java          # 安全规则集类
│   ├── BuiltinRules.java             # 内置规则类
│   ├── SecurityRuleLoader.java       # 规则加载器
│   └── SecurityRuleProcessor.java    # 规则处理器
└── util/
    ├── XssUtil.java                   # XSS防护工具
    ├── SqlInjectionUtil.java         # SQL注入防护工具
    └── SensitiveWordUtil.java        # 敏感词过滤工具
```

## 核心功能

### 1. 规则文件配置

**支持格式：**
- YAML格式（推荐）
- JSON格式

**规则文件结构：**
```yaml
name: "规则集名称"
description: "规则集描述"
version: "1.0.0"
enabled: true

rules:
  - id: "规则ID"
    name: "规则名称"
    description: "规则描述"
    type: "XSS"  # XSS、SQL_INJECTION、SENSITIVE_WORD
    enabled: true
    priority: 10  # 优先级，数字越小优先级越高
    action: "FILTER"  # FILTER、ENCODE、BLOCK、REPLACE
    replacement: "***"  # 替换内容
    patterns:
      - "正则表达式1"
      - "正则表达式2"
    exclude-patterns:
      - "/api/public/**"
    exclude-params:
      - "content"
    parameters:
      key1: "value1"

global-config:
  default-action: "FILTER"
  case-sensitive: false
  log-level: "WARN"
```

### 2. 内置规则

**XSS防护内置规则：**
- 脚本标签检测
- 事件处理器检测
- 危险属性检测

**SQL注入防护内置规则：**
- 危险关键词检测
- 联合查询检测
- 注释检测
- 布尔盲注检测

**敏感词过滤内置规则：**
- 政治敏感词
- 暴力敏感词
- 色情敏感词

### 3. 规则处理器

**支持的动作类型：**
- `FILTER`: 过滤危险内容，保留安全内容
- `ENCODE`: 对危险内容进行HTML编码
- `BLOCK`: 直接阻止包含危险内容的请求
- `REPLACE`: 将危险内容替换为指定内容

**规则优先级：**
- 数字越小优先级越高
- 优先级高的规则先执行
- 相同优先级的规则按加载顺序执行

## 配置示例

### 主配置

```yaml
securt-kit:
  safety:
    enabled: true
    
    # 规则配置（推荐使用）
    rules:
      enabled: true
      enable-builtin: true
      rule-files:
        - security-rules/xss-rules.yml
        - security-rules/sql-injection-rules.yml
        - security-rules/sensitive-word-rules.yml
        - security-rules/combined-rules.yml
      cache:
        enabled: true
        expire-time: 3600
        max-size: 1000
    
    # 传统配置（兼容性）
    xss:
      enabled: true
      mode: FILTER
      # ... 其他配置
```

### 规则文件示例

**XSS防护规则：**
```yaml
name: "Custom XSS Protection Rules"
description: "Custom XSS protection rules"
version: "1.0.0"
enabled: true

rules:
  - id: "xss-custom-script"
    name: "Custom Script Detection"
    description: "Detect custom script patterns"
    type: "XSS"
    enabled: true
    priority: 5
    action: "FILTER"
    patterns:
      - "<script[^>]*>.*?</script>"
      - "javascript\\s*:"
    exclude-patterns:
      - "/api/public/**"
    exclude-params:
      - "content"
```

**SQL注入防护规则：**
```yaml
name: "Custom SQL Injection Protection Rules"
description: "Custom SQL injection protection rules"
version: "1.0.0"
enabled: true

rules:
  - id: "sql-custom-keywords"
    name: "Custom Dangerous Keywords"
    description: "Detect custom dangerous SQL keywords"
    type: "SQL_INJECTION"
    enabled: true
    priority: 5
    action: "BLOCK"
    patterns:
      - "\\bselect\\b"
      - "\\binsert\\b"
      - "\\bupdate\\b"
      - "\\bdelete\\b"
    exclude-patterns:
      - "/api/admin/**"
    exclude-params:
      - "sql"
```

**敏感词过滤规则：**
```yaml
name: "Custom Sensitive Word Filtering Rules"
description: "Custom sensitive word filtering rules"
version: "1.0.0"
enabled: true

rules:
  - id: "sensitive-custom-political"
    name: "Custom Political Sensitive Words"
    description: "Filter custom political sensitive words"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 5
    action: "REPLACE"
    replacement: "***"
    patterns:
      - "政治敏感词"
      - "政府敏感词"
    exclude-params:
      - "adminContent"
```

## 使用方式

### 1. 启用规则文件配置

```yaml
securt-kit:
  safety:
    rules:
      enabled: true
      enable-builtin: true
      rule-files:
        - security-rules/xss-rules.yml
        - security-rules/sql-injection-rules.yml
        - security-rules/sensitive-word-rules.yml
```

### 2. 禁用规则文件配置（使用传统配置）

```yaml
securt-kit:
  safety:
    rules:
      enabled: false
    xss:
      enabled: true
      # ... 传统配置
```

### 3. 创建自定义规则文件

在 `src/main/resources/security-rules/` 目录下创建规则文件，支持YAML和JSON格式。

## 技术特性

### 1. 规则加载

- 支持多个规则文件同时加载
- 支持内置规则和文件规则混合使用
- 支持规则缓存机制
- 支持规则验证和错误处理

### 2. 规则处理

- 支持规则优先级控制
- 支持多种动作类型
- 支持排除配置（URL模式、参数名）
- 支持正则表达式匹配

### 3. 性能优化

- 规则缓存机制
- 规则优先级排序
- 排除配置减少不必要的检查
- 异步处理支持

### 4. 兼容性

- 完全兼容旧配置方式
- 支持渐进式迁移
- 向后兼容保证

## 扩展性

### 1. 自定义规则类型

可以扩展新的规则类型：

```java
public class CustomSecurityRule extends SecurityRule {
    // 自定义规则逻辑
}
```

### 2. 自定义动作类型

可以扩展新的动作类型：

```java
public class CustomSecurityRuleProcessor extends SecurityRuleProcessor {
    // 自定义动作处理逻辑
}
```

### 3. 自定义规则加载器

可以扩展新的规则加载器：

```java
public class CustomSecurityRuleLoader extends SecurityRuleLoader {
    // 自定义规则加载逻辑
}
```

## 最佳实践

### 1. 规则设计

- 使用合理的优先级数字
- 编写精确的正则表达式
- 合理配置排除规则
- 定期检查和更新规则

### 2. 性能优化

- 启用规则缓存
- 避免过于复杂的正则表达式
- 合理配置排除规则
- 定期清理不需要的规则

### 3. 安全加固

- 定期更新规则库
- 监控规则执行情况
- 建立安全响应机制
- 定期进行安全测试

## 总结

基于规则文件的安全防护功能提供了：

1. **灵活性** - 支持多种规则类型和动作类型
2. **可扩展性** - 支持自定义规则和处理器
3. **性能优化** - 支持缓存和排除机制
4. **兼容性** - 完全兼容旧配置方式
5. **易用性** - 支持YAML/JSON格式配置
6. **可维护性** - 支持规则文件管理和更新

通过这个规则系统，可以实现精确、高效、灵活的安全防护，满足各种复杂的安全需求。
