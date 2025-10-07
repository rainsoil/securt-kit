# 敏感词过滤功能使用示例

## 概述

敏感词过滤功能支持从多个文件读取敏感词，支持内置敏感词，支持白名单URL配置。

## 配置方式

### 1. 基本配置

```yaml
securt-kit:
  safety:
    sensitive-word:
      enabled: true
      enable-builtin: true
      exclude-patterns:
        - /api/admin/**
        - /api/public/**
      word-files:
        - sensitive-words/custom.txt
        - sensitive-words/my-words.txt
      replace-char: "*"
```

### 2. 配置说明

- `enabled`: 是否启用敏感词过滤
- `enable-builtin`: 是否启用内置敏感词（包含政治、暴力、色情等基础敏感词）
- `exclude-patterns`: 白名单URL模式，匹配的URL将跳过敏感词过滤
- `word-files`: 自定义敏感词文件路径列表，支持多个文件
- `replace-char`: 敏感词替换字符，默认为"*"

## 文件结构

```
src/main/resources/
├── sensitive-words/
│   ├── builtin/                  # 内置敏感词文件（自动加载）
│   │   ├── political.txt         # 政治敏感词
│   │   ├── violence.txt          # 暴力敏感词
│   │   └── pornography.txt       # 色情敏感词
│   ├── custom.txt                # 自定义敏感词
│   └── my-words.txt              # 我的敏感词
```

## 敏感词文件格式

敏感词文件支持以下格式：

```
# 这是注释行，以#开头
敏感词1
敏感词2
敏感词3
```

## 使用示例

### 1. 启用内置敏感词

```yaml
sensitive-word:
  enabled: true
  enable-builtin: true  # 启用内置敏感词
  replace-char: "*"
```

内置敏感词包含：
- 政治敏感词：政治、政府、国家、领导人等
- 暴力敏感词：暴力、血腥、恐怖、爆炸等
- 色情敏感词：色情、黄色、成人、裸体等

### 2. 添加自定义敏感词

```yaml
sensitive-word:
  enabled: true
  enable-builtin: true
  word-files:
    - sensitive-words/custom.txt
    - sensitive-words/my-words.txt
  replace-char: "*"
```

### 3. 配置白名单URL

```yaml
sensitive-word:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**      # 管理员接口跳过过滤
    - /api/public/**     # 公开接口跳过过滤
    - /static/**         # 静态资源跳过过滤
  word-files:
    - sensitive-words/custom.txt
  replace-char: "*"
```

### 4. 禁用内置敏感词

```yaml
sensitive-word:
  enabled: true
  enable-builtin: false  # 禁用内置敏感词
  word-files:
    - sensitive-words/custom.txt
  replace-char: "*"
```

## 测试示例

### 1. 测试内置敏感词

```bash
# 发送包含政治敏感词的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "content=这是政治敏感词"

# 结果：政治敏感词会被替换为***
```

### 2. 测试白名单URL

```bash
# 发送到白名单URL的请求
curl -X POST "http://localhost:8080/api/admin/test" \
  -d "content=这是政治敏感词"

# 结果：不会进行敏感词过滤
```

### 3. 测试自定义敏感词

```bash
# 发送包含自定义敏感词的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "content=这是自定义敏感词"

# 结果：自定义敏感词会被替换为***
```

## 高级配置

### 1. 多文件配置

```yaml
sensitive-word:
  enabled: true
  enable-builtin: true
  word-files:
    - sensitive-words/political.txt
    - sensitive-words/violence.txt
    - sensitive-words/pornography.txt
    - sensitive-words/custom.txt
    - sensitive-words/business.txt
  replace-char: "*"
```

### 2. 不同替换字符

```yaml
sensitive-word:
  enabled: true
  enable-builtin: true
  word-files:
    - sensitive-words/custom.txt
  replace-char: "***"  # 使用三个星号替换
```

### 3. 精确白名单配置

```yaml
sensitive-word:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**           # 管理员接口
    - /api/system/**          # 系统接口
    - /api/public/**          # 公开接口
    - /static/**              # 静态资源
    - /actuator/**            # 监控接口
  word-files:
    - sensitive-words/custom.txt
  replace-char: "*"
```

## 注意事项

### 1. 文件路径

- 敏感词文件必须放在 `src/main/resources` 目录下
- 文件路径相对于 classpath 根目录
- 支持子目录结构

### 2. 文件格式

- 每行一个敏感词
- 以 `#` 开头的行为注释，会被忽略
- 空行会被忽略
- 支持中文和英文敏感词

### 3. 性能考虑

- 敏感词文件会在应用启动时加载到内存
- 建议敏感词文件不要过大
- 可以通过白名单URL减少不必要的检查

### 4. 安全考虑

- 内置敏感词文件是只读的，不能修改
- 自定义敏感词文件可以随时更新
- 建议定期检查和更新敏感词库

## 故障排除

### 1. 敏感词不生效

检查配置是否正确：

```yaml
sensitive-word:
  enabled: true  # 确保启用
  enable-builtin: true  # 确保启用内置敏感词
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
ls -la src/main/resources/sensitive-words/

# 检查文件格式
cat sensitive-words/custom.txt
```

## 总结

敏感词过滤功能提供了：

1. **内置敏感词** - 开箱即用的基础敏感词库
2. **自定义敏感词** - 支持从多个文件加载自定义敏感词
3. **白名单URL** - 支持配置跳过敏感词过滤的URL
4. **灵活配置** - 支持启用/禁用内置敏感词
5. **简单易用** - 配置简单，使用方便

通过合理配置，可以实现精确、高效的敏感词过滤功能。
