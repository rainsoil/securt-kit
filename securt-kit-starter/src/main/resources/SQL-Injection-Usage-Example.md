# SQL注入防护功能使用示例

## 概述

SQL注入防护功能支持从多个规则文件读取SQL注入规则，支持内置规则，支持白名单URL配置。

## 配置方式

### 1. 基本配置

```yaml
securt-kit:
  safety:
    sql-injection:
      enabled: true
      enable-builtin: true
      exclude-patterns:
        - /api/admin/**
        - /api/public/**
      rule-files:
        - sql-rules/custom-keywords.txt
        - sql-rules/custom-patterns.txt
      dangerous-keywords:
        - drop
        - delete
        - update
        - insert
        - select
        - union
```

### 2. 配置说明

- `enabled`: 是否启用SQL注入防护
- `enable-builtin`: 是否启用内置SQL注入规则（包含关键词、模式、函数等基础规则）
- `exclude-patterns`: 白名单URL模式，匹配的URL将跳过SQL注入防护
- `rule-files`: 自定义SQL注入规则文件路径列表，支持多个文件
- `dangerous-keywords`: 危险SQL关键词（兼容旧配置）

## 文件结构

```
src/main/resources/
├── sql-rules/
│   ├── builtin/                  # 内置SQL注入规则文件（自动加载）
│   │   ├── keywords.txt          # SQL关键词规则
│   │   ├── patterns.txt          # SQL注入模式规则
│   │   └── functions.txt         # SQL函数规则
│   ├── custom-keywords.txt       # 自定义关键词规则
│   └── custom-patterns.txt       # 自定义模式规则
```

## 规则文件格式

SQL注入规则文件支持以下格式：

```
# 这是注释行，以#开头
select
insert
update
delete
union select
and 1=1
or 1=1
```

## 使用示例

### 1. 启用内置规则

```yaml
sql-injection:
  enabled: true
  enable-builtin: true  # 启用内置规则
```

内置规则包含：
- **关键词规则**：select、insert、update、delete、drop、create等
- **模式规则**：union select、and 1=1、or 1=1等
- **函数规则**：char()、ascii()、substring()等

### 2. 添加自定义规则

```yaml
sql-injection:
  enabled: true
  enable-builtin: true
  rule-files:
    - sql-rules/custom-keywords.txt
    - sql-rules/custom-patterns.txt
```

### 3. 配置白名单URL

```yaml
sql-injection:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**      # 管理员接口跳过防护
    - /api/system/**     # 系统接口跳过防护
    - /api/public/**     # 公开接口跳过防护
  rule-files:
    - sql-rules/custom-keywords.txt
```

### 4. 禁用内置规则

```yaml
sql-injection:
  enabled: true
  enable-builtin: false  # 禁用内置规则
  rule-files:
    - sql-rules/custom-keywords.txt
```

## 规则文件示例

### 1. 自定义关键词规则文件

```txt
# custom-keywords.txt
# 自定义SQL关键词规则

# 业务相关关键词
user_table
order_table
product_table
payment_table

# 系统相关关键词
admin_user
system_config
log_table
audit_table
```

### 2. 自定义模式规则文件

```txt
# custom-patterns.txt
# 自定义SQL注入模式规则

# 业务相关模式
user_id = 1
order_id = 1
product_id = 1

# 系统相关模式
admin = true
system = true
debug = true
```

## 测试示例

### 1. 测试内置规则

```bash
# 发送包含SQL关键词的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "name=admin' OR '1'='1"

# 结果：会被检测为SQL注入攻击并阻止
```

### 2. 测试白名单URL

```bash
# 发送到白名单URL的请求
curl -X POST "http://localhost:8080/api/admin/test" \
  -d "sql=SELECT * FROM users"

# 结果：不会进行SQL注入防护
```

### 3. 测试自定义规则

```bash
# 发送包含自定义规则的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "content=user_id = 1"

# 结果：如果匹配自定义规则，会被检测为SQL注入攻击
```

## 高级配置

### 1. 多文件配置

```yaml
sql-injection:
  enabled: true
  enable-builtin: true
  rule-files:
    - sql-rules/keywords.txt
    - sql-rules/patterns.txt
    - sql-rules/functions.txt
    - sql-rules/custom.txt
    - sql-rules/business.txt
```

### 2. 精确白名单配置

```yaml
sql-injection:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**           # 管理员接口
    - /api/system/**          # 系统接口
    - /api/public/**          # 公开接口
    - /static/**              # 静态资源
    - /actuator/**            # 监控接口
  rule-files:
    - sql-rules/custom.txt
```

### 3. 混合配置

```yaml
sql-injection:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**
  rule-files:
    - sql-rules/custom.txt
  dangerous-keywords:
    - custom_keyword1
    - custom_keyword2
```

## 规则类型说明

### 1. 关键词规则

包含基本的SQL关键词：
- 数据操作：select、insert、update、delete
- 数据定义：create、drop、alter
- 数据控制：grant、revoke
- 其他：union、or、and、where等

### 2. 模式规则

包含常见的SQL注入模式：
- 联合查询：union select、union all select
- 布尔盲注：and 1=1、or 1=1
- 时间延迟：waitfor delay、sleep()
- 错误注入：extractvalue()、updatexml()

### 3. 函数规则

包含SQL函数：
- 字符串函数：char()、ascii()、substring()
- 数学函数：abs()、round()、floor()
- 日期函数：now()、current_timestamp
- 聚合函数：count()、sum()、avg()

## 注意事项

### 1. 文件路径

- SQL注入规则文件必须放在 `src/main/resources` 目录下
- 文件路径相对于 classpath 根目录
- 支持子目录结构

### 2. 文件格式

- 每行一个规则
- 以 `#` 开头的行为注释，会被忽略
- 空行会被忽略
- 支持大小写不敏感匹配

### 3. 性能考虑

- 规则文件会在应用启动时加载到内存
- 建议规则文件不要过大
- 可以通过白名单URL减少不必要的检查

### 4. 安全考虑

- 内置规则文件是只读的，不能修改
- 自定义规则文件可以随时更新
- 建议定期检查和更新规则库

## 故障排除

### 1. 规则不生效

检查配置是否正确：

```yaml
sql-injection:
  enabled: true  # 确保启用
  enable-builtin: true  # 确保启用内置规则
```

### 2. 白名单不生效

检查URL模式是否正确：

```yaml
exclude-patterns:
  - /api/admin/**  # 确保路径正确
```

### 3. 文件加载失败

检查文件路径和格式：

```bash
# 检查文件是否存在
ls -la src/main/resources/sql-rules/

# 检查文件格式
cat sql-rules/custom-keywords.txt
```

### 4. 误拦截

检查规则是否过于宽泛：

```txt
# 过于宽泛的规则
user
id
name

# 更精确的规则
user_id = 1
admin = true
```

## 总结

SQL注入防护功能提供了：

1. **内置规则** - 开箱即用的基础SQL注入规则库
2. **自定义规则** - 支持从多个文件加载自定义规则
3. **白名单URL** - 支持配置跳过SQL注入防护的URL
4. **灵活配置** - 支持启用/禁用内置规则
5. **简单易用** - 配置简单，使用方便

通过合理配置，可以实现精确、高效的SQL注入防护功能。
