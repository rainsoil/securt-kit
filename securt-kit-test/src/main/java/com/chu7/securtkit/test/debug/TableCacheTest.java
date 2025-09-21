package com.chu7.securtkit.test.debug;

import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.test.entity.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TableCacheTest {

    public static void main(String[] args) {
        System.out.println("=== TableCache Test ===");
        
        // 手动初始化缓存
        Set<Class<?>> entityClasses = new HashSet<>();
        entityClasses.add(User.class);
        
        TableCache.init(entityClasses);
        
        // 检查缓存内容
        System.out.println("加密表数量: " + TableCache.getFieldEncryptTable().size());
        System.out.println("加密表列表: " + TableCache.getFieldEncryptTable());
        
        // 检查特定字段
        String tableName = "test_user";
        String[] fields = {"phone", "email", "password", "id_card"};
        
        for (String field : fields) {
            com.chu7.securtkit.annotation.FieldEncryptor encryptor = 
                TableCache.getFieldEncryptor(tableName, field);
            
            if (encryptor != null) {
                System.out.println("字段 " + tableName + "." + field + " 需要加密, 策略: " + encryptor.value().getSimpleName());
            } else {
                System.out.println("字段 " + tableName + "." + field + " 不需要加密");
            }
        }
        
        System.out.println("=== 测试完成 ===");
    }
}