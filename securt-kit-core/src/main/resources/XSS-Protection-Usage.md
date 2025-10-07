# XSS攻击防护功能使用指南

## 功能概述

securt-kit提供了完整的XSS（跨站脚本攻击）防护功能，包括：

- **XSS检测器**：检测输入中是否包含XSS攻击模式
- **XSS处理器**：对输入进行过滤、编码等处理
- **XSS过滤器**：提供统一的XSS防护接口
- **白名单/黑名单**：支持灵活的标签和属性过滤规则

## 核心组件

### 1. XSSConfig - 配置类
```java
@ConfigurationProperties(prefix = "securt-kit.security.xss")
public class XSSConfig {
    private boolean enabled = true;                    // 是否启用XSS防护
    private XSSMode mode = XSSMode.FILTER;            // 防护模式：FILTER/ENCODE/BOTH
    private XSSPolicy policy = XSSPolicy.STRICT;      // 防护策略：STRICT/MODERATE/LOOSE
    private List<String> whitelist = new ArrayList<>(); // 白名单标签
    private List<String> blacklist = new ArrayList<>(); // 黑名单标签
    private boolean whitelistMode = false;            // 是否启用白名单模式
    private boolean logEnabled = true;                // 是否记录日志
    private boolean throwException = false;           // 是否抛出异常
}
```

### 2. XSSUtils - 工具类
```java
// 检测XSS攻击
boolean containsXSS = XSSUtils.containsXSS(input);

// 过滤XSS内容
String filtered = XSSUtils.filterXSS(input);

// 编码HTML字符
String encoded = XSSUtils.encodeHTML(input);

// 解码HTML字符
String decoded = XSSUtils.decodeHTML(input);

// 白名单过滤
String whitelistFiltered = XSSUtils.filterByWhitelist(input, whitelist);

// 黑名单过滤
String blacklistFiltered = XSSUtils.filterByBlacklist(input, blacklist);
```

### 3. XSSDetector - 检测器
```java
@Autowired
private XSSDetector xssDetector;

// 检测XSS攻击
XSSDetector.XSSDetectionResult result = xssDetector.detectXSS(input);
if (!result.isSafe()) {
    System.out.println("检测到XSS攻击: " + result.getThreats());
}
```

### 4. XSSProcessor - 处理器
```java
@Autowired
private XSSProcessor xssProcessor;

// 处理XSS攻击
String processed = xssProcessor.processXSS(input);

// 处理请求参数
String paramProcessed = xssProcessor.processParameter("paramName", paramValue);

// 处理请求头
String headerProcessed = xssProcessor.processHeader("User-Agent", headerValue);

// 处理响应内容
String responseProcessed = xssProcessor.processResponse(responseContent);
```

### 5. XSSProtectionFilter - 过滤器
```java
@Autowired
private XSSProtectionFilter xssFilter;

// 处理参数
String processed = xssFilter.processParameter("paramName", paramValue);

// 处理请求头
String headerProcessed = xssFilter.processHeader("User-Agent", headerValue);

// 处理响应
String responseProcessed = xssFilter.processResponse(responseContent);

// 检查是否包含XSS
boolean containsXSS = xssFilter.containsXSS(input);
```

## 配置示例

### application.yml配置
```yaml
securt-kit:
  security:
    enabled: true
    xss:
      enabled: true
      mode: FILTER                    # FILTER/ENCODE/BOTH
      policy: STRICT                  # STRICT/MODERATE/LOOSE
      whitelist-mode: false           # true: 白名单模式, false: 黑名单模式
      log-enabled: true
      throw-exception: false
      
      # 白名单标签
      whitelist:
        - "b"
        - "i"
        - "u"
        - "strong"
        - "em"
        - "p"
        - "br"
        - "div"
        - "span"
      
      # 黑名单标签
      blacklist:
        - "script"
        - "iframe"
        - "object"
        - "embed"
        - "form"
        - "input"
        - "button"
      
      # 白名单属性
      whitelist-attributes:
        - "class"
        - "id"
        - "style"
        - "title"
        - "alt"
        - "src"
        - "href"
      
      # 黑名单属性
      blacklist-attributes:
        - "onclick"
        - "onload"
        - "onerror"
        - "onmouseover"
        - "onfocus"
        - "onblur"
        - "onchange"
        - "onsubmit"
```

## 使用示例

### 1. 基本使用
```java
@RestController
public class TestController {
    
    @Autowired
    private XSSProtectionFilter xssFilter;
    
    @PostMapping("/test")
    public String test(@RequestParam String content) {
        // 处理用户输入
        String processedContent = xssFilter.processParameter("content", content);
        
        // 检查是否包含XSS攻击
        if (xssFilter.containsXSS(content)) {
            return "检测到XSS攻击，已处理";
        }
        
        return "处理后的内容: " + processedContent;
    }
}
```

### 2. 自定义配置
```java
@Configuration
public class XSSConfigCustom {
    
    @Bean
    @Primary
    public XSSConfig customXSSConfig() {
        XSSConfig config = new XSSConfig();
        config.setEnabled(true);
        config.setMode(XSSConfig.XSSMode.BOTH);
        config.setPolicy(XSSConfig.XSSPolicy.MODERATE);
        config.setWhitelistMode(true);
        config.setWhitelist(Arrays.asList("b", "i", "p", "div"));
        config.setBlacklistAttributes(Arrays.asList("onclick", "onload"));
        config.setLogEnabled(true);
        config.setThrowException(false);
        return config;
    }
}
```

### 3. 手动处理
```java
@Service
public class ContentService {
    
    @Autowired
    private XSSProcessor xssProcessor;
    
    public String processUserContent(String content) {
        try {
            // 处理内容
            String processed = xssProcessor.processXSS(content);
            
            // 记录处理日志
            log.info("用户内容已处理: {} -> {}", content, processed);
            
            return processed;
        } catch (SecurityException e) {
            log.warn("检测到XSS攻击: {}", e.getMessage());
            throw new BusinessException("内容包含非法字符");
        }
    }
}
```

## 防护策略说明

### 1. 防护模式（XSSMode）
- **FILTER**：过滤模式，移除恶意标签和属性
- **ENCODE**：编码模式，将HTML字符编码为实体
- **BOTH**：混合模式，先过滤再编码

### 2. 防护策略（XSSPolicy）
- **STRICT**：严格模式，检测所有可能的XSS攻击
- **MODERATE**：中等模式，检测常见的XSS攻击
- **LOOSE**：宽松模式，只检测明显的XSS攻击

### 3. 过滤模式
- **白名单模式**：只保留白名单中的标签和属性
- **黑名单模式**：移除黑名单中的标签和属性

## 注意事项

1. **性能考虑**：XSS检测和处理会消耗一定的CPU资源，建议在高并发场景下进行性能测试
2. **误报处理**：某些合法的HTML内容可能被误判为XSS攻击，需要调整配置
3. **日志记录**：建议开启日志记录，便于监控和调试
4. **异常处理**：根据业务需求决定是否在检测到XSS时抛出异常
5. **配置调优**：根据实际业务场景调整白名单、黑名单和防护策略

## 测试验证

运行测试类验证功能：
```bash
mvn test -Dtest=XSSProtectionTest
```

测试包括：
- XSS检测功能测试
- XSS过滤功能测试
- XSS编码功能测试
- 白名单/黑名单模式测试
- 不同防护策略测试
- 不同处理模式测试
