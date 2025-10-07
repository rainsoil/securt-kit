package com.chu7.securtkit.integration;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.filter.SafetyHttpServletRequestWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JSON XSS防护测试
 * 测试POST请求JSON body的XSS防护功能
 */
public class JsonXssProtectionTest {

    @Mock
    private HttpServletRequest mockRequest;

    private SafetyConfig safetyConfig;
    private SafetyHttpServletRequestWrapper wrapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化安全配置
        safetyConfig = new SafetyConfig();
        SafetyConfig.XssConfig xssConfig = new SafetyConfig.XssConfig();
        xssConfig.setEnabled(true);
        
        SafetyConfig.XssConfig.IpWhitelistConfig ipWhitelistConfig = new SafetyConfig.XssConfig.IpWhitelistConfig();
        ipWhitelistConfig.setEnabled(false);
        xssConfig.setIpWhitelist(ipWhitelistConfig);
        
        SafetyConfig.XssConfig.XssKeywordsConfig keywordsConfig = new SafetyConfig.XssConfig.XssKeywordsConfig();
        keywordsConfig.setEnabled(true);
        keywordsConfig.setEnableBuiltin(true);
        keywordsConfig.setKeywords(java.util.Arrays.asList("<script>", "javascript:", "onclick"));
        xssConfig.setKeywords(keywordsConfig);
        
        safetyConfig.setXss(xssConfig);
    }

    @Test
    public void testJsonXssAttackDetection() throws IOException {
        // 准备包含XSS攻击的JSON请求
        String maliciousJson = "{\"name\":\"<script>alert('xss')</script>\",\"email\":\"test@example.com\"}";
        
        // 模拟请求
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());
        when(mockRequest.getInputStream()).thenReturn(
            new ByteArrayInputStream(maliciousJson.getBytes(StandardCharsets.UTF_8))
        );
        
        // 创建包装器
        wrapper = new SafetyHttpServletRequestWrapper(mockRequest);
        wrapper.setSafetyConfig(safetyConfig);
        
        // 测试：应该检测到XSS攻击并抛出异常
        assertThrows(SecurityException.class, () -> {
            wrapper.getInputStream();
        });
    }

    @Test
    public void testJsonXssFiltering() throws IOException {
        // 准备包含XSS攻击的JSON请求
        String maliciousJson = "{\"name\":\"<script>alert('xss')</script>\",\"email\":\"test@example.com\"}";
        String expectedFilteredJson = "{\"name\":\"alert('xss')\",\"email\":\"test@example.com\"}";
        
        // 模拟请求
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());
        when(mockRequest.getInputStream()).thenReturn(
            new ByteArrayInputStream(maliciousJson.getBytes(StandardCharsets.UTF_8))
        );
        
        // 创建包装器
        wrapper = new SafetyHttpServletRequestWrapper(mockRequest);
        wrapper.setSafetyConfig(safetyConfig);
        
        // 测试：应该过滤XSS内容
        try (java.io.InputStream inputStream = wrapper.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String actualJson = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            
            // 验证XSS内容被过滤
            assertFalse(actualJson.contains("<script>"));
            assertFalse(actualJson.contains("</script>"));
        }
    }

    @Test
    public void testJsonWithMultipleXssAttacks() throws IOException {
        // 准备包含多个XSS攻击的JSON请求
        String maliciousJson = "{\"name\":\"<script>alert('xss')</script>\",\"description\":\"javascript:alert('xss')\",\"onclick\":\"onclick=alert('xss')\"}";
        
        // 模拟请求
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());
        when(mockRequest.getInputStream()).thenReturn(
            new ByteArrayInputStream(maliciousJson.getBytes(StandardCharsets.UTF_8))
        );
        
        // 创建包装器
        wrapper = new SafetyHttpServletRequestWrapper(mockRequest);
        wrapper.setSafetyConfig(safetyConfig);
        
        // 测试：应该检测到XSS攻击并抛出异常
        assertThrows(SecurityException.class, () -> {
            wrapper.getInputStream();
        });
    }

    @Test
    public void testJsonWithSafeContent() throws IOException {
        // 准备安全的JSON请求
        String safeJson = "{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30}";
        
        // 模拟请求
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());
        when(mockRequest.getInputStream()).thenReturn(
            new ByteArrayInputStream(safeJson.getBytes(StandardCharsets.UTF_8))
        );
        
        // 创建包装器
        wrapper = new SafetyHttpServletRequestWrapper(mockRequest);
        wrapper.setSafetyConfig(safetyConfig);
        
        // 测试：应该正常处理，不抛出异常
        assertDoesNotThrow(() -> {
            try (java.io.InputStream inputStream = wrapper.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                String actualJson = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                
                // 验证内容没有被修改
                assertEquals(safeJson, actualJson);
            }
        });
    }

    @Test
    public void testNonJsonRequest() throws IOException {
        // 准备非JSON请求
        String formData = "name=John&email=john@example.com";
        
        // 模拟请求
        when(mockRequest.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());
        when(mockRequest.getInputStream()).thenReturn(
            new ByteArrayInputStream(formData.getBytes(StandardCharsets.UTF_8))
        );
        
        // 创建包装器
        wrapper = new SafetyHttpServletRequestWrapper(mockRequest);
        wrapper.setSafetyConfig(safetyConfig);
        
        // 测试：应该正常处理，不进行JSON过滤
        assertDoesNotThrow(() -> {
            try (java.io.InputStream inputStream = wrapper.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                String actualData = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                
                // 验证内容没有被修改
                assertEquals(formData, actualData);
            }
        });
    }

    @Test
    public void testJsonWithNestedObjects() throws IOException {
        // 准备包含嵌套对象的JSON请求
        String nestedJson = "{\"user\":{\"name\":\"<script>alert('xss')</script>\",\"profile\":{\"bio\":\"javascript:alert('xss')\"}}}";
        
        // 模拟请求
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());
        when(mockRequest.getInputStream()).thenReturn(
            new ByteArrayInputStream(nestedJson.getBytes(StandardCharsets.UTF_8))
        );
        
        // 创建包装器
        wrapper = new SafetyHttpServletRequestWrapper(mockRequest);
        wrapper.setSafetyConfig(safetyConfig);
        
        // 测试：应该检测到XSS攻击并抛出异常
        assertThrows(SecurityException.class, () -> {
            wrapper.getInputStream();
        });
    }

    @Test
    public void testJsonWithArrays() throws IOException {
        // 准备包含数组的JSON请求
        String arrayJson = "{\"tags\":[\"<script>alert('xss')</script>\",\"javascript:alert('xss')\",\"safe-tag\"]}";
        
        // 模拟请求
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());
        when(mockRequest.getInputStream()).thenReturn(
            new ByteArrayInputStream(arrayJson.getBytes(StandardCharsets.UTF_8))
        );
        
        // 创建包装器
        wrapper = new SafetyHttpServletRequestWrapper(mockRequest);
        wrapper.setSafetyConfig(safetyConfig);
        
        // 测试：应该检测到XSS攻击并抛出异常
        assertThrows(SecurityException.class, () -> {
            wrapper.getInputStream();
        });
    }
}
