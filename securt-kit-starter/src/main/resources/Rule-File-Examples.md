# 规则文件使用示例

## 概述

本文档提供了安全防护规则文件的详细使用示例，包括XSS防护、SQL注入防护、敏感词过滤等各种场景的规则配置。

## 规则文件结构

### 基本结构

```yaml
name: "规则集名称"
description: "规则集描述"
version: "1.0.0"
enabled: true

rules:
  - id: "规则ID"
    name: "规则名称"
    description: "规则描述"
    type: "规则类型"
    enabled: true
    priority: 10
    action: "动作类型"
    patterns:
      - "正则表达式"
    exclude-patterns:
      - "排除URL模式"
    exclude-params:
      - "排除参数名"
    parameters:
      key: "value"

global-config:
  default-action: "FILTER"
  case-sensitive: false
  log-level: "WARN"
```

## XSS防护规则示例

### 基础XSS防护

```yaml
name: "Basic XSS Protection"
description: "Basic XSS protection rules"
version: "1.0.0"
enabled: true

rules:
  - id: "xss-script-tags"
    name: "Script Tags Detection"
    description: "Detect and filter script tags"
    type: "XSS"
    enabled: true
    priority: 10
    action: "FILTER"
    patterns:
      - "<script[^>]*>.*?</script>"
      - "<script[^>]*>"
      - "<iframe[^>]*>.*?</iframe>"
      - "<object[^>]*>.*?</object>"
    exclude-patterns:
      - "/api/public/**"
      - "/static/**"
    exclude-params:
      - "content"
      - "description"

  - id: "xss-event-handlers"
    name: "Event Handlers Detection"
    description: "Detect and filter event handlers"
    type: "XSS"
    enabled: true
    priority: 20
    action: "FILTER"
    patterns:
      - "on\\w+\\s*="
      - "onclick\\s*="
      - "onload\\s*="
      - "onerror\\s*="
    exclude-params:
      - "eventHandler"

  - id: "xss-javascript-protocol"
    name: "JavaScript Protocol Detection"
    description: "Detect JavaScript protocol"
    type: "XSS"
    enabled: true
    priority: 30
    action: "BLOCK"
    patterns:
      - "javascript\\s*:"
      - "vbscript\\s*:"
      - "data\\s*:\\s*text/html"
    exclude-patterns:
      - "/api/admin/**"
```

### 高级XSS防护

```yaml
name: "Advanced XSS Protection"
description: "Advanced XSS protection with encoding"
version: "1.0.0"
enabled: true

rules:
  - id: "xss-dangerous-attributes"
    name: "Dangerous Attributes Detection"
    description: "Detect dangerous attributes"
    type: "XSS"
    enabled: true
    priority: 10
    action: "FILTER"
    patterns:
      - "src\\s*=\\s*[\"']?javascript:"
      - "href\\s*=\\s*[\"']?javascript:"
      - "style\\s*=\\s*[\"']?.*expression\\s*\\("
    exclude-params:
      - "styleContent"

  - id: "xss-html-encoding"
    name: "HTML Encoding"
    description: "Encode HTML special characters"
    type: "XSS"
    enabled: true
    priority: 20
    action: "ENCODE"
    patterns:
      - "<"
      - ">"
      - "\""
      - "'"
    exclude-params:
      - "htmlContent"

global-config:
  default-action: "FILTER"
  case-sensitive: false
  log-level: "INFO"
```

## SQL注入防护规则示例

### 基础SQL注入防护

```yaml
name: "Basic SQL Injection Protection"
description: "Basic SQL injection protection rules"
version: "1.0.0"
enabled: true

rules:
  - id: "sql-dangerous-keywords"
    name: "Dangerous Keywords Detection"
    description: "Detect dangerous SQL keywords"
    type: "SQL_INJECTION"
    enabled: true
    priority: 10
    action: "BLOCK"
    patterns:
      - "\\bselect\\b"
      - "\\binsert\\b"
      - "\\bupdate\\b"
      - "\\bdelete\\b"
      - "\\bdrop\\b"
      - "\\bcreate\\b"
      - "\\balter\\b"
    exclude-patterns:
      - "/api/admin/**"
      - "/api/system/**"
    exclude-params:
      - "sql"
      - "query"

  - id: "sql-union-queries"
    name: "Union Queries Detection"
    description: "Detect union-based SQL injection"
    type: "SQL_INJECTION"
    enabled: true
    priority: 20
    action: "BLOCK"
    patterns:
      - "union\\s+select"
      - "union\\s+all\\s+select"
    exclude-params:
      - "unionQuery"

  - id: "sql-comments"
    name: "SQL Comments Detection"
    description: "Detect SQL comments"
    type: "SQL_INJECTION"
    enabled: true
    priority: 30
    action: "FILTER"
    patterns:
      - "--.*"
      - "/\\*.*?\\*/"
    exclude-params:
      - "comment"
```

### 高级SQL注入防护

```yaml
name: "Advanced SQL Injection Protection"
description: "Advanced SQL injection protection rules"
version: "1.0.0"
enabled: true

rules:
  - id: "sql-boolean-blind"
    name: "Boolean Blind Injection"
    description: "Detect boolean-based blind SQL injection"
    type: "SQL_INJECTION"
    enabled: true
    priority: 10
    action: "BLOCK"
    patterns:
      - "and\\s+\\d+\\s*=\\s*\\d+"
      - "or\\s+\\d+\\s*=\\s*\\d+"
      - "and\\s+\\d+\\s*=\\s*\\d+\\s*--"
      - "or\\s+\\d+\\s*=\\s*\\d+\\s*--"
    exclude-params:
      - "booleanQuery"

  - id: "sql-time-delay"
    name: "Time Delay Injection"
    description: "Detect time-based SQL injection"
    type: "SQL_INJECTION"
    enabled: true
    priority: 20
    action: "BLOCK"
    patterns:
      - "waitfor\\s+delay"
      - "sleep\\s*\\("
      - "benchmark\\s*\\("
    exclude-params:
      - "timeQuery"

  - id: "sql-error-injection"
    name: "Error-based Injection"
    description: "Detect error-based SQL injection"
    type: "SQL_INJECTION"
    enabled: true
    priority: 30
    action: "BLOCK"
    patterns:
      - "extractvalue\\s*\\("
      - "updatexml\\s*\\("
      - "exp\\s*\\("
      - "pow\\s*\\("
    exclude-params:
      - "errorQuery"
```

## 敏感词过滤规则示例

### 基础敏感词过滤

```yaml
name: "Basic Sensitive Word Filtering"
description: "Basic sensitive word filtering rules"
version: "1.0.0"
enabled: true

rules:
  - id: "sensitive-political"
    name: "Political Sensitive Words"
    description: "Filter political sensitive words"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 10
    action: "REPLACE"
    replacement: "***"
    patterns:
      - "政治"
      - "政府"
      - "国家"
      - "领导人"
      - "主席"
      - "总理"
    exclude-patterns:
      - "/api/admin/**"
      - "/api/system/**"
    exclude-params:
      - "adminContent"
      - "systemLog"

  - id: "sensitive-violence"
    name: "Violence Sensitive Words"
    description: "Filter violence sensitive words"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 20
    action: "REPLACE"
    replacement: "***"
    patterns:
      - "暴力"
      - "血腥"
      - "恐怖"
      - "爆炸"
      - "枪击"
      - "刀砍"
    exclude-params:
      - "violenceContent"

  - id: "sensitive-pornography"
    name: "Pornography Sensitive Words"
    description: "Filter pornography sensitive words"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 30
    action: "REPLACE"
    replacement: "***"
    patterns:
      - "色情"
      - "黄色"
      - "成人"
      - "裸体"
    exclude-params:
      - "pornContent"
```

### 高级敏感词过滤

```yaml
name: "Advanced Sensitive Word Filtering"
description: "Advanced sensitive word filtering with custom replacement"
version: "1.0.0"
enabled: true

rules:
  - id: "sensitive-business"
    name: "Business Sensitive Words"
    description: "Filter business sensitive words"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 10
    action: "REPLACE"
    replacement: "[敏感词]"
    patterns:
      - "商业敏感词"
      - "金融敏感词"
      - "投资敏感词"
      - "股票敏感词"
    exclude-params:
      - "businessContent"

  - id: "sensitive-personal"
    name: "Personal Sensitive Words"
    description: "Filter personal sensitive words"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 20
    action: "REPLACE"
    replacement: "***"
    patterns:
      - "个人敏感词"
      - "隐私敏感词"
      - "身份敏感词"
    exclude-params:
      - "personalContent"

  - id: "sensitive-custom"
    name: "Custom Sensitive Words"
    description: "Filter custom sensitive words"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 30
    action: "REPLACE"
    replacement: "***"
    patterns:
      - "自定义敏感词1"
      - "自定义敏感词2"
      - "自定义敏感词3"
    exclude-params:
      - "customContent"
```

## 综合规则示例

### 综合安全防护

```yaml
name: "Comprehensive Security Rules"
description: "Comprehensive security rules for all types of protection"
version: "1.0.0"
enabled: true

rules:
  # XSS规则
  - id: "comprehensive-xss"
    name: "Comprehensive XSS Protection"
    description: "Comprehensive XSS protection"
    type: "XSS"
    enabled: true
    priority: 10
    action: "FILTER"
    patterns:
      - "<script[^>]*>.*?</script>"
      - "javascript\\s*:"
      - "vbscript\\s*:"
      - "on\\w+\\s*="
    exclude-patterns:
      - "/api/public/**"
    exclude-params:
      - "content"

  # SQL注入规则
  - id: "comprehensive-sql"
    name: "Comprehensive SQL Injection Protection"
    description: "Comprehensive SQL injection protection"
    type: "SQL_INJECTION"
    enabled: true
    priority: 20
    action: "BLOCK"
    patterns:
      - "\\bselect\\b"
      - "\\binsert\\b"
      - "\\bupdate\\b"
      - "\\bdelete\\b"
      - "union\\s+select"
    exclude-patterns:
      - "/api/admin/**"
    exclude-params:
      - "sql"

  # 敏感词规则
  - id: "comprehensive-sensitive"
    name: "Comprehensive Sensitive Word Filtering"
    description: "Comprehensive sensitive word filtering"
    type: "SENSITIVE_WORD"
    enabled: true
    priority: 30
    action: "REPLACE"
    replacement: "***"
    patterns:
      - "敏感词1"
      - "敏感词2"
      - "敏感词3"
    exclude-params:
      - "adminContent"

global-config:
  default-action: "FILTER"
  case-sensitive: false
  log-level: "INFO"
```

## 规则配置最佳实践

### 1. 规则优先级

- 使用合理的优先级数字（10, 20, 30...）
- 重要的规则使用较小的优先级数字
- 相同类型的规则使用相近的优先级

### 2. 排除配置

- 合理配置排除模式，避免误拦截
- 使用参数排除避免正常业务被拦截
- 定期检查和更新排除配置

### 3. 动作选择

- `FILTER`: 适用于需要保留部分内容的场景
- `ENCODE`: 适用于需要显示内容但防止执行的场景
- `BLOCK`: 适用于完全不允许的场景
- `REPLACE`: 适用于敏感词过滤场景

### 4. 正则表达式

- 使用精确的正则表达式，避免误匹配
- 测试正则表达式的正确性
- 考虑大小写敏感性

### 5. 性能优化

- 合理使用缓存配置
- 避免过于复杂的正则表达式
- 定期清理不需要的规则

## 故障排除

### 1. 规则不生效

- 检查规则文件格式是否正确
- 检查规则是否启用
- 检查排除配置是否过于宽泛

### 2. 误拦截

- 检查正则表达式是否过于宽泛
- 添加适当的排除配置
- 调整规则优先级

### 3. 性能问题

- 检查正则表达式复杂度
- 启用缓存配置
- 优化规则数量

## 总结

规则文件配置提供了灵活、强大的安全防护能力，支持：

- 多种规则类型（XSS、SQL注入、敏感词）
- 多种动作类型（过滤、编码、阻止、替换）
- 灵活的排除配置
- 规则优先级控制
- 缓存机制优化

通过合理配置规则文件，可以实现精确、高效的安全防护。
