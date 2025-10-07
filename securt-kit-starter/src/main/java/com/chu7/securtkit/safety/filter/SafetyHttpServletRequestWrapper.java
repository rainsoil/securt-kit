package com.chu7.securtkit.safety.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 安全请求包装器
 *
 * 职责：
 * - 缓存并提供可重复读取的请求体（JSON Body 等）。
 * - 暴露 set/getCachedBody 方法，供过滤器在净化后回写。
 *
 * 非职责：
 * - 不做安全检测与清洗（这些逻辑在 UnifiedSecurityProtectionFilter + Processor 中完成）。
 */
public class SafetyHttpServletRequestWrapper extends HttpServletRequestWrapper {
    

    private Map<String, String[]> parameterMap;
    private Map<String, String> headerMap;
    private byte[] cachedBody;
    
    public SafetyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.parameterMap = new HashMap<String, String[]>(request.getParameterMap());
        this.headerMap = new HashMap<>();
    }



    /**
     * 设置缓存的请求体字节（用于过滤后写回）。
     */
    public void setCachedBody(byte[] newBody) {
        this.cachedBody = newBody == null ? new byte[0] : newBody;
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
        if (cachedBody == null) {
            cacheInputStream();
        }
        return new CachedBodyServletInputStream(cachedBody);
    }
    
    /**
     * 缓存输入流并处理JSON body
     */
    private void cacheInputStream() throws IOException {
        // 读取原始body
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        try (InputStream inputStream = super.getInputStream()) {
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        }
        cachedBody = buffer.toByteArray();
        

    }
    




    // 不在包装器内做任何安全过滤，统一由过滤器与处理器负责
    
    /**
     * 缓存的ServletInputStream实现
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream cachedBodyInputStream;
        
        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }
        
        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
        
        @Override
        public int read(byte[] b) throws IOException {
            return cachedBodyInputStream.read(b);
        }
        
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return cachedBodyInputStream.read(b, off, len);
        }
        
        @Override
        public long skip(long n) throws IOException {
            return cachedBodyInputStream.skip(n);
        }
        
        @Override
        public int available() throws IOException {
            return cachedBodyInputStream.available();
        }
        
        @Override
        public void close() throws IOException {
            cachedBodyInputStream.close();
        }
        
        @Override
        public synchronized void mark(int readlimit) {
            cachedBodyInputStream.mark(readlimit);
        }
        
        @Override
        public synchronized void reset() throws IOException {
            cachedBodyInputStream.reset();
        }
        
        @Override
        public boolean markSupported() {
            return cachedBodyInputStream.markSupported();
        }
    }
}
