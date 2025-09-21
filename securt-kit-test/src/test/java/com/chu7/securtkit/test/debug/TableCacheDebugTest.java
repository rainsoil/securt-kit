package com.chu7.securtkit.test.debug;

import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.test.SecurtKitTestApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Set;

/**
 * 调试TableCache初始化问题
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
@SpringBootTest(classes = SecurtKitTestApplication.class)
@ActiveProfiles("test")
public class TableCacheDebugTest {

    @Test
    public void testTableCacheInitialization() {
        log.info("=== 调试TableCache初始化 ===");
        
        // 检查缓存内容
        Map<String, Map<String, com.chu7.securtkit.annotation.FieldEncryptor>> tableInfo = 
            TableCache.getTableFieldEncryptInfo();
        
        Set<String> encryptTables = TableCache.getFieldEncryptTable();
        
        log.info("加密表数量: {}", tableInfo.size());
        log.info("需要加密的表: {}", encryptTables);
        
        // 打印详细信息
        for (Map.Entry<String, Map<String, com.chu7.securtkit.annotation.FieldEncryptor>> entry : tableInfo.entrySet()) {
            log.info("表名: {}", entry.getKey());
            for (Map.Entry<String, com.chu7.securtkit.annotation.FieldEncryptor> fieldEntry : entry.getValue().entrySet()) {
                log.info("  字段: {} -> 注解: {}", fieldEntry.getKey(), fieldEntry.getValue());
            }
        }
        
        // 检查特定表和字段
        String tableName = "test_user";
        String fieldName = "phone";
        
        com.chu7.securtkit.annotation.FieldEncryptor encryptor = 
            TableCache.getFieldEncryptor(tableName, fieldName);
        
        if (encryptor != null) {
            log.info("找到加密注解: {} for {}.{}", encryptor, tableName, fieldName);
        } else {
            log.warn("未找到加密注解 for {}.{}", tableName, fieldName);
        }
    }
}