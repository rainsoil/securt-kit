package com.chu7.securtkit.test.debug;

import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.test.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 拦截器调试工具
 * 用于检查拦截器和缓存是否正确初始化
 *
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
@Component
public class InterceptorDebugTest implements CommandLineRunner {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== 拦截器调试信息 ===");
        
        // 手动初始化TableCache进行测试
        System.out.println("尝试手动初始化TableCache...");
        try {
            java.util.List<String> packages = java.util.Arrays.asList("com.chu7.securtkit.test");
            TableCache.initByPackages(packages);
            System.out.println("手动初始化TableCache完成");
        } catch (Exception e) {
            System.out.println("手动初始化TableCache失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 检查表缓存
        Set<String> encryptTables = TableCache.getFieldEncryptTable();
        System.out.println("需要加密的表数量: " + encryptTables.size());
        System.out.println("需要加密的表: " + encryptTables);
        
        // 检查用户表的字段加密信息
        java.util.Map<String, com.chu7.securtkit.annotation.FieldEncryptor> userTableInfo = TableCache.getTableFieldEncryptInfo("test_user");
        System.out.println("用户表字段加密信息: " + userTableInfo);
        
        // 检查具体字段的加密注解
        com.chu7.securtkit.annotation.FieldEncryptor phoneEncryptor = TableCache.getFieldEncryptor("test_user", "phone");
        com.chu7.securtkit.annotation.FieldEncryptor emailEncryptor = TableCache.getFieldEncryptor("test_user", "email");
        com.chu7.securtkit.annotation.FieldEncryptor idCardEncryptor = TableCache.getFieldEncryptor("test_user", "id_card");
        
        System.out.println("手机号字段加密注解: " + (phoneEncryptor != null ? phoneEncryptor.getClass().getSimpleName() : "null"));
        System.out.println("邮箱字段加密注解: " + (emailEncryptor != null ? emailEncryptor.getClass().getSimpleName() : "null"));
        System.out.println("身份证字段加密注解: " + (idCardEncryptor != null ? idCardEncryptor.getClass().getSimpleName() : "null"));
        
        // 检查拦截器是否被注册
        System.out.println("\n=== 检查拦截器注册状态 ===");
        try {
            List<org.apache.ibatis.plugin.Interceptor> interceptors = sqlSessionFactory.getConfiguration().getInterceptors();
            System.out.println("已注册的拦截器数量: " + interceptors.size());
            for (org.apache.ibatis.plugin.Interceptor interceptor : interceptors) {
                System.out.println("拦截器: " + interceptor.getClass().getSimpleName());
            }
        } catch (Exception e) {
            System.out.println("检查拦截器注册状态失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== 拦截器调试信息结束 ===\n");
    }
}
