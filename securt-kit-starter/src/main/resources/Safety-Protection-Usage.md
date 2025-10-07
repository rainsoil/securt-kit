# 安全防护功能使用文档

## 概述

securt-kit 提供了基于 Spring Boot Filter 的安全防护功能，包括：

1. **XSS防护** - 防止跨站脚本攻击
2. **SQL注入防护** - 防止SQL注入攻击  
3. **敏感词过滤** - 过滤敏感词汇

## 功能特性

- ✅ 支持规则文件配置，支持多个规则文件
- ✅ 支持内置规则，可启用或禁用
- ✅ 支持各自白名单设置
- ✅ 敏感词支持从文件中读取，支持多个文件
- ✅ 支持classpath下的敏感词文件
- ✅ 支持URL模式排除
- ✅ 支持参数名排除
- ✅ 支持自定义配置
- ✅ 支持缓存机制
- ✅ 支持规则优先级
- ✅ 支持多种动作类型（FILTER、ENCODE、BLOCK、REPLACE）

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.chu7</groupId>
    <artifactId>securt-kit-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置文件

在 `application.yml` 中添加配置：

#### 方式一：使用规则文件配置（推荐）

```yaml
securt-kit:
  safety:
    enabled: true
    
    # 规则配置（推荐使用）
    rules:
      # 是否启用规则文件配置
      enabled: true
      # 是否启用内置规则
      enable-builtin: true
      # 规则文件路径列表
      rule-files:
        - security-rules/xss-rules.yml
        - security-rules/sql-injection-rules.yml
        - security-rules/sensitive-word-rules.yml
        - security-rules/combined-rules.yml
      # 规则缓存配置
      cache:
        enabled: true
        expire-time: 3600
        max-size: 1000
```

#### 方式二：使用传统配置（兼容性）

```yaml
securt-kit:
  safety:
    enabled: true
    
    # 规则配置
    rules:
      enabled: false  # 禁用规则文件，使用传统配置
    
    # XSS防护配置
    xss:
      enabled: true
      mode: FILTER  # FILTER(过滤)、ENCODE(编码)、BLOCK(阻止)
      exclude-patterns:
        - /api/public/**
        - /static/**
      exclude-params:
        - content
        - description
      allowed-tags:
        - p
        - br
        - strong
        - em
      allowed-attributes:
        - href
        - src
        - alt
    
    # SQL注入防护配置
    sql-injection:
      enabled: true
      exclude-patterns:
        - /api/admin/**
      exclude-params:
        - sql
        - query
      dangerous-keywords:
        - drop
        - delete
        - union
    
    # 敏感词过滤配置
    sensitive-word:
      enabled: true
      # 是否启用内置敏感词
      enable-builtin: true
      # 排除的URL模式（白名单）
      exclude-patterns:
        - /api/admin/**
        - /api/system/**
        - /api/public/**
      # 敏感词文件路径列表（classpath下）
      word-files:
        - sensitive-words/custom.txt
        - sensitive-words/my-words.txt
      # 替换字符
      replace-char: "*"
```

### 3. 创建规则文件

在 `src/main/resources` 下创建规则文件：

```
src/main/resources/
├── security-rules/
│   ├── xss-rules.yml              # XSS防护规则
│   ├── sql-injection-rules.yml   # SQL注入防护规则
│   ├── sensitive-word-rules.yml  # 敏感词过滤规则
│   └── combined-rules.yml        # 综合规则
├── sensitive-words/
│   ├── builtin/                  # 内置敏感词文件
│   │   ├── political.txt         # 政治敏感词
│   │   ├── violence.txt          # 暴力敏感词
│   │   └── pornography.txt       # 色情敏感词
│   ├── custom.txt                # 自定义敏感词
│   └── my-words.txt              # 我的敏感词
```

#### 规则文件格式

规则文件支持YAML和JSON格式，推荐使用YAML格式：

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
    replacement: "***"  # 替换内容（当动作为REPLACE时使用）
    patterns:
      - "正则表达式1"
      - "正则表达式2"
    exclude-patterns:
      - "/api/public/**"
      - "/static/**"
    exclude-params:
      - "content"
      - "description"
    parameters:
      key1: "value1"
      key2: "value2"

global-config:
  default-action: "FILTER"
  case-sensitive: false
  log-level: "WARN"
```

#### 敏感词文件格式

```
# 这是注释行，以#开头
敏感词1
敏感词2
敏感词3
```

## 配置详解

### 规则配置

```yaml
rules:
  # 是否启用规则文件配置
  enabled: true
  # 是否启用内置规则
  enable-builtin: true
  # 规则文件路径列表
  rule-files:
    - security-rules/xss-rules.yml
    - security-rules/sql-injection-rules.yml
    - security-rules/sensitive-word-rules.yml
  # 规则缓存配置
  cache:
    enabled: true
    expire-time: 3600  # 缓存过期时间（秒）
    max-size: 1000     # 最大缓存大小
```

**规则类型说明：**
- `XSS`: XSS防护规则
- `SQL_INJECTION`: SQL注入防护规则
- `SENSITIVE_WORD`: 敏感词过滤规则

**动作类型说明：**
- `FILTER`: 过滤危险内容，保留安全内容
- `ENCODE`: 对危险内容进行HTML编码
- `BLOCK`: 直接阻止包含危险内容的请求
- `REPLACE`: 将危险内容替换为指定内容

**优先级说明：**
- 数字越小优先级越高
- 优先级高的规则先执行
- 相同优先级的规则按加载顺序执行

### XSS防护配置

```yaml
xss:
  enabled: true                    # 是否启用XSS防护
  mode: FILTER                     # 防护模式
  exclude-patterns:               # 排除的URL模式
    - /api/public/**
    - /static/**
  exclude-params:                 # 排除的参数名
    - content
    - description
  allowed-tags:                   # 允许的HTML标签白名单
    - p
    - br
    - strong
  allowed-attributes:             # 允许的HTML属性白名单
    - href
    - src
    - alt
```

**防护模式说明：**
- `FILTER`: 过滤危险内容，保留安全内容
- `ENCODE`: 对危险内容进行HTML编码
- `BLOCK`: 直接阻止包含危险内容的请求

### SQL注入防护配置

```yaml
sql-injection:
  enabled: true                   # 是否启用SQL注入防护
  exclude-patterns:              # 排除的URL模式
    - /api/admin/**
  exclude-params:                # 排除的参数名
    - sql
    - query
  dangerous-keywords:            # 自定义危险关键词
    - drop
    - delete
    - union
```

### 敏感词过滤配置

```yaml
sensitive-word:
  enabled: true                  # 是否启用敏感词过滤
  enable-builtin: true          # 是否启用内置敏感词
  exclude-patterns:             # 排除的URL模式（白名单）
    - /api/admin/**
    - /api/system/**
    - /api/public/**
  word-files:                   # 自定义敏感词文件路径列表
    - sensitive-words/custom.txt
    - sensitive-words/my-words.txt
  replace-char: "*"             # 替换字符
```

**配置说明：**
- `enabled`: 是否启用敏感词过滤
- `enable-builtin`: 是否启用内置敏感词（包含政治、暴力、色情等基础敏感词）
- `exclude-patterns`: 白名单URL模式，匹配的URL将跳过敏感词过滤
- `word-files`: 自定义敏感词文件路径列表，支持多个文件
- `replace-char`: 敏感词替换字符，默认为"*"

## 使用示例

### 1. 基本使用

启动应用后，安全防护过滤器会自动生效，对所有请求进行安全检查。

### 2. 测试XSS防护

```bash
# 发送包含XSS攻击的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "content=<script>alert('xss')</script>"

# 结果：XSS内容会被过滤或阻止
```

### 3. 测试SQL注入防护

```bash
# 发送包含SQL注入的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "name=admin' OR '1'='1"

# 结果：SQL注入内容会被检测并阻止
```

### 4. 测试敏感词过滤

```bash
# 发送包含敏感词的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "content=这是政治敏感词"

# 结果：敏感词会被替换为*号
```

## 高级配置

### 1. 自定义敏感词文件

创建自定义敏感词文件：

```txt
# custom-sensitive-words.txt
自定义敏感词1
自定义敏感词2
```

在配置中引用：

```yaml
sensitive-word:
  word-files:
    - sensitive-words/custom.txt
    - custom-sensitive-words.txt
```

### 2. 动态配置

支持通过配置中心动态更新配置，无需重启应用。

### 3. 监控和日志

```yaml
logging:
  level:
    com.chu7.securtkit.safety: DEBUG
```

## 注意事项

1. **性能影响**: 安全防护会增加请求处理时间，建议合理配置排除规则
2. **误报处理**: 如果正常业务被误拦截，可以通过排除规则解决
3. **敏感词更新**: 敏感词文件更新后需要重启应用或清除缓存
4. **配置优先级**: URL模式排除 > 参数名排除 > 全局配置

## 故障排除

### 1. 正常请求被拦截

检查排除配置是否正确：

```yaml
# 检查URL模式
exclude-patterns:
  - /api/public/**  # 确保路径正确

# 检查参数名
exclude-params:
  - content  # 确保参数名正确
```

### 2. 敏感词文件加载失败

检查文件路径和格式：

```bash
# 检查文件是否存在
ls -la src/main/resources/sensitive-words/

# 检查文件格式
cat sensitive-words/custom.txt
```

### 3. 配置不生效

检查配置格式和属性名：

```yaml
# 确保配置层级正确
securt-kit:
  safety:
    enabled: true
    xss:
      enabled: true
```

## API参考

### SafetyConfig

安全防护主配置类

### XssConfig

XSS防护配置类

### SqlInjectionConfig

SQL注入防护配置类

### SensitiveWordConfig

敏感词过滤配置类

### SafetyFilter

安全防护过滤器

### SafetyHttpServletRequestWrapper

安全请求包装器

## 更新日志

- v1.0.0: 初始版本，支持XSS防护、SQL注入防护、敏感词过滤
