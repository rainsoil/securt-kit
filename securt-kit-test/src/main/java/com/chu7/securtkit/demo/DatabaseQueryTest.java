//package com.chu7.securtkit.test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
//// @Component
//public class DatabaseQueryTest implements CommandLineRunner {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 每5秒查询一次数据库
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(5000);
//                    System.out.println("=== 查询数据库中的用户数据 ===");
//                    List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM test_user");
//                    for (Map<String, Object> user : users) {
//                        System.out.println("用户数据: " + user);
//                    }
//                    System.out.println("========================");
//                } catch (InterruptedException e) {
//                    break;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//}