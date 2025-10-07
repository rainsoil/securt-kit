package com.chu7.securtkit.safety.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 安全请求包装器
 * 用于处理请求参数和请求头
 */
public class SafetyHttpServletRequestWrapper extends HttpServletRequestWrapper {
    
    private Map<String, String[]> parameterMap;
    private Map<String, String> headerMap;
    
    public SafetyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.parameterMap = new HashMap<String, String[]>(request.getParameterMap());
        this.headerMap = new HashMap<>();
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }
    
    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        return (values != null && values.length > 0) ? values[0] : null;
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }
    
    /**
     * 设置参数值
     */
    public void setParameter(String name, String value) {
        parameterMap.put(name, new String[]{value});
    }
    
    /**
     * 设置参数值数组
     */
    public void setParameterValues(String name, String[] values) {
        parameterMap.put(name, values);
    }
    
    @Override
    public String getHeader(String name) {
        // 优先返回自定义设置的请求头
        String customHeader = headerMap.get(name);
        if (customHeader != null) {
            return customHeader;
        }
        return super.getHeader(name);
    }
    
    /**
     * 设置请求头
     */
    public void setHeader(String name, String value) {
        headerMap.put(name, value);
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return super.getInputStream();
    }
}
