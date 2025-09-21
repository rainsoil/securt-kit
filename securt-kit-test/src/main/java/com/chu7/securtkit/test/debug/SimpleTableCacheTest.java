package com.chu7.securtkit.test.debug;

import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.test.entity.User;

import java.util.HashSet;
import java.util.Set;

public class SimpleTableCacheTest {

    public static void main(String[] args) {
        System.out.println("=== Simple TableCache Test ===");
        
        // Manually initialize cache
        Set<Class<?>> entityClasses = new HashSet<>();
        entityClasses.add(User.class);
        
        TableCache.init(entityClasses);
        
        // Check cache content
        System.out.println("Encrypted tables: " + TableCache.getFieldEncryptTable());
        
        // Check specific fields
        String tableName = "test_user";
        String[] fields = {"phone", "email", "password", "id_card"};
        
        for (String field : fields) {
            com.chu7.securtkit.annotation.FieldEncryptor encryptor = 
                TableCache.getFieldEncryptor(tableName, field);
            
            if (encryptor != null) {
                System.out.println("Field " + tableName + "." + field + " needs encryption, strategy: " + encryptor.value().getSimpleName());
            } else {
                System.out.println("Field " + tableName + "." + field + " does not need encryption");
            }
        }
        
        System.out.println("=== Test completed ===");
    }
}