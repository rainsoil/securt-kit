# XSS防护功能使用示例

## 概述

XSS防护功能支持从多个规则文件读取XSS规则，支持内置规则，支持白名单URL配置。

## 配置方式

### 1. 基本配置

```yaml
securt-kit:
  safety:
    xss:
      enabled: true
      enable-builtin: true
      exclude-patterns:
        - /api/admin/**
        - /api/public/**
      rule-files:
        - xss-rules/custom-scripts.txt
        - xss-rules/custom-events.txt
```

### 2. 配置说明

- `enabled`: 是否启用XSS防护
- `enable-builtin`: 是否启用内置XSS规则（包含脚本标签、事件处理器、协议、属性等基础规则）
- `exclude-patterns`: 白名单URL模式，匹配的URL将跳过XSS防护
- `rule-files`: 自定义XSS规则文件路径列表，支持多个文件

## 文件结构

```
src/main/resources/
├── xss-rules/
│   ├── builtin/                  # 内置XSS规则文件（自动加载）
│   │   ├── script-tags.txt       # 脚本标签规则
│   │   ├── event-handlers.txt    # 事件处理器规则
│   │   ├── protocols.txt         # 协议规则
│   │   └── attributes.txt        # 属性规则
│   ├── custom-scripts.txt        # 自定义脚本规则
│   └── custom-events.txt         # 自定义事件规则
```

## 规则文件格式

XSS规则文件支持以下格式：

```
# 这是注释行，以#开头
<script
</script>
onclick
onload
javascript:
vbscript:
src=
href=
```

## 使用示例

### 1. 启用内置规则

```yaml
xss:
  enabled: true
  enable-builtin: true  # 启用内置规则
```

内置规则包含：
- **脚本标签规则**：<script>、</script>、<iframe>、<object>等
- **事件处理器规则**：onclick、onload、onerror、onmouseover等
- **协议规则**：javascript:、vbscript:、data:text/html等
- **属性规则**：src=、href=、style=、background=等

### 2. 添加自定义规则

```yaml
xss:
  enabled: true
  enable-builtin: true
  rule-files:
    - xss-rules/custom-scripts.txt
    - xss-rules/custom-events.txt
```

### 3. 配置白名单URL

```yaml
xss:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**      # 管理员接口跳过防护
    - /api/system/**     # 系统接口跳过防护
    - /api/public/**     # 公开接口跳过防护
    - /static/**         # 静态资源跳过防护
  rule-files:
    - xss-rules/custom-scripts.txt
```

### 4. 禁用内置规则

```yaml
xss:
  enabled: true
  enable-builtin: false  # 禁用内置规则
  rule-files:
    - xss-rules/custom-scripts.txt
```

## 规则文件示例

### 1. 自定义脚本规则文件

```txt
# custom-scripts.txt
# 自定义XSS脚本规则

# 业务相关脚本
<script type="text/javascript">
<script language="javascript">
<script language="vbscript">

# 内联脚本
onclick="alert('xss')"
onload="alert('xss')"
onerror="alert('xss')"

# 表达式
expression(
-ms-expression
-moz-expression
```

### 2. 自定义事件规则文件

```txt
# custom-events.txt
# 自定义XSS事件规则

# 业务相关事件
onclick="javascript:"
onload="javascript:"
onerror="javascript:"

# 鼠标事件
onmouseover="alert('xss')"
onmouseout="alert('xss')"
onmousedown="alert('xss')"

# 键盘事件
onkeydown="alert('xss')"
onkeyup="alert('xss')"
onkeypress="alert('xss')"
```

## 测试示例

### 1. 测试内置规则

```bash
# 发送包含脚本标签的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "content=<script>alert('xss')</script>"

# 结果：会被检测为XSS攻击并阻止
```

### 2. 测试白名单URL

```bash
# 发送到白名单URL的请求
curl -X POST "http://localhost:8080/api/admin/test" \
  -d "content=<script>alert('xss')</script>"

# 结果：不会进行XSS防护
```

### 3. 测试自定义规则

```bash
# 发送包含自定义规则的请求
curl -X POST "http://localhost:8080/api/test" \
  -d "content=onclick=\"alert('xss')\""

# 结果：如果匹配自定义规则，会被检测为XSS攻击
```

## 高级配置

### 1. 多文件配置

```yaml
xss:
  enabled: true
  enable-builtin: true
  rule-files:
    - xss-rules/scripts.txt
    - xss-rules/events.txt
    - xss-rules/protocols.txt
    - xss-rules/attributes.txt
    - xss-rules/custom.txt
    - xss-rules/business.txt
```

### 2. 精确白名单配置

```yaml
xss:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**           # 管理员接口
    - /api/system/**          # 系统接口
    - /api/public/**          # 公开接口
    - /static/**              # 静态资源
    - /css/**                 # CSS文件
    - /js/**                  # JavaScript文件
    - /images/**              # 图片文件
    - /actuator/**            # 监控接口
  rule-files:
    - xss-rules/custom.txt
```

### 3. 混合配置

```yaml
xss:
  enabled: true
  enable-builtin: true
  exclude-patterns:
    - /api/admin/**
  rule-files:
    - xss-rules/custom.txt
```

## 规则类型说明

### 1. 脚本标签规则

包含危险的HTML标签：
- 脚本标签：<script>、</script>、<iframe>、<object>
- 表单标签：<form>、<input>、<textarea>
- 样式标签：<style>、<link>
- 其他标签：<meta>、<base>、<embed>、<applet>

### 2. 事件处理器规则

包含JavaScript事件处理器：
- 鼠标事件：onclick、onmouseover、onmouseout
- 键盘事件：onkeydown、onkeyup、onkeypress
- 表单事件：onchange、onsubmit、onreset
- 窗口事件：onload、onunload、onresize

### 3. 协议规则

包含危险的协议：
- JavaScript协议：javascript:、vbscript:
- 数据协议：data:text/html、data:application/javascript
- 其他协议：file:、ftp:、gopher:

### 4. 属性规则

包含危险的HTML属性：
- 链接属性：src=、href=
- 样式属性：style=、background=、color=
- 事件属性：onclick=、onload=、onerror=
- 其他属性：id=、class=、name=

## 注意事项

### 1. 文件路径

- XSS规则文件必须放在 `src/main/resources` 目录下
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
xss:
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
ls -la src/main/resources/xss-rules/

# 检查文件格式
cat xss-rules/custom-scripts.txt
```

### 4. 误拦截

检查规则是否过于宽泛：

```txt
# 过于宽泛的规则
script
onclick
href

# 更精确的规则
<script
onclick="
href="javascript:
```

## 总结

XSS防护功能提供了：

1. **内置规则** - 开箱即用的基础XSS规则库
2. **自定义规则** - 支持从多个文件加载自定义规则
3. **白名单URL** - 支持配置跳过XSS防护的URL
4. **灵活配置** - 支持启用/禁用内置规则
5. **简单易用** - 配置简单，使用方便

通过合理配置，可以实现精确、高效的XSS防护功能。
