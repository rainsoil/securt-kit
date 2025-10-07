//package com.chu7.securtkit.test;
//
//import com.chu7.securtkit.cache.TableCache;
//import com.chu7.securtkit.util.ClassScannerUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//// @Component
//public class DatabaseDirectQuery implements CommandLineRunner {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 等待几秒让数据插入完成
//        Thread.sleep(2000);
//
//        // 检查TableCache状态
//        System.out.println("=== TableCache状态检查 ===");
//        Set<String> encryptTables = TableCache.getFieldEncryptTable();
//        System.out.println("需要加密的表: " + encryptTables);
//
//        for (String tableName : encryptTables) {
//            Map<String, com.chu7.securtkit.annotation.FieldEncryptor> fieldMap = TableCache.getTableFieldEncryptInfo(tableName);
//            System.out.println("表 " + tableName + " 的加密字段: " + fieldMap);
//        }
//        System.out.println("========================");
//
//        // 查询数据库中的用户数据
//        System.out.println("=== 查询数据库中的用户数据 ===");
//        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM test_user");
//        for (Map<String, Object> user : users) {
//            System.out.println("用户数据: " + user);
//
//            // 检查各个字段是否被加密
//            String phone = (String) user.get("phone");
//            String email = (String) user.get("email");
//            String idCard = (String) user.get("id_card");
//            String password = (String) user.get("password");
//
//            System.out.println("  手机号: " + phone);
//            System.out.println("  邮箱: " + email);
//            System.out.println("  身份证号: " + idCard);
//            System.out.println("  密码: " + password);
//
//            // 简单判断是否加密（明文通常不会包含特殊字符）
//            if (phone != null && phone.equals("13800138000")) {
//                System.out.println("  手机号未加密!");
//            } else if (phone != null) {
//                System.out.println("  手机号已加密");
//            }
//
//            if (email != null && email.equals("test@example.com")) {
//                System.out.println("  邮箱未加密!");
//            } else if (email != null) {
//                System.out.println("  邮箱已加密");
//            }
//
//            if (idCard != null && idCard.equals("123456789012345678")) {
//                System.out.println("  身份证号未加密!");
//            } else if (idCard != null) {
//                System.out.println("  身份证号已加密");
//            }
//        }
//        System.out.println("========================");
//    }
//}