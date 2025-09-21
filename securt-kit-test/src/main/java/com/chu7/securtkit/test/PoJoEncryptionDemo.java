//package com.chu7.securtkit.test;
//
//import com.chu7.securtkit.annotation.FieldEncryptor;
//import com.chu7.securtkit.test.entity.User;
//import com.chu7.securtkit.test.mapper.UserMapper;
//import com.chu7.securtkit.test.service.UserService;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * POJO模式加密演示类
// * 展示如何使用POJO模式进行数据加密
// *
// * @author chu7
// * @date 2024/7/9 14:06
// */
//@Component
//public class PoJoEncryptionDemo implements CommandLineRunner {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @Autowired
//    private SqlSessionFactory sqlSessionFactory;
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("=== POJO模式加密演示开始 ===");
//
//        // 检查拦截器注册状态
//        System.out.println("\n=== 检查拦截器注册状态 ===");
//        List<org.apache.ibatis.plugin.Interceptor> interceptors = sqlSessionFactory.getConfiguration().getInterceptors();
//        System.out.println("已注册的拦截器数量: " + interceptors.size());
//        for (org.apache.ibatis.plugin.Interceptor interceptor : interceptors) {
//            System.out.println("拦截器: " + interceptor.getClass().getSimpleName());
//        }
//        System.out.println("========================\n");
//
//        // 演示基本功能
////        demonstrateBasicFunctionality();
////
////        // 演示批量操作
////        demonstrateBatchOperations();
////
////        // 演示条件查询
////        demonstrateConditionalQueries();
////
////        // 演示更新操作
//        demonstrateUpdateOperations();
//
//        System.out.println("=== POJO模式加密演示结束 ===");
//    }
//
//    /**
//     * 演示基本功能
//     */
//    private void demonstrateBasicFunctionality() {
//        System.out.println("\n--- 基本功能演示 ---");
//
//        // 创建用户
//        User user = new User();
//        user.setUsername("张三");
//        user.setPhone("13800138000");
//        user.setEmail("zhangsan@example.com");
//        user.setIdCard("110101199001011234");
//
//        System.out.println("创建用户: " + user.getUsername());
//        System.out.println("手机号: " + user.getPhone());
//        System.out.println("邮箱: " + user.getEmail());
//        System.out.println("身份证: " + user.getIdCard());
//
//        // 保存用户（自动加密）
//        User savedUser = userService.createUser(user.getUsername(), "123456", user.getPhone(), user.getEmail(), user.getIdCard(), 25);
//        System.out.println("用户保存成功，ID: " + savedUser.getId());
//
////        // 查询用户（自动解密）
//        User queriedUser = userMapper.selectById(savedUser.getId());
//        System.out.println("查询用户: " + queriedUser.getUsername());
//        System.out.println("手机号: " + queriedUser.getPhone());
//        System.out.println("邮箱: " + queriedUser.getEmail());
//        System.out.println("身份证: " + queriedUser.getIdCard());
////
////        // 验证数据一致性
//        assert user.getUsername().equals(queriedUser.getUsername());
//        assert user.getPhone().equals(queriedUser.getPhone());
//        assert user.getEmail().equals(queriedUser.getEmail());
//        assert user.getIdCard().equals(queriedUser.getIdCard());
////
//        System.out.println("✓ 数据一致性验证通过");
//    }
//
//    /**
//     * 演示批量操作
//     */
//    private void demonstrateBatchOperations() {
//        System.out.println("\n--- 批量操作演示 ---");
//
//        // 创建多个用户
//        User[] users = {
//                createUser("李四", "13800138001", "lisi@example.com", "110101199001011235"),
//                createUser("王五", "13800138002", "wangwu@example.com", "110101199001011236"),
//                createUser("赵六", "13800138003", "zhaoliu@example.com", "110101199001011237")
//        };
//
//        System.out.println("创建 " + users.length + " 个用户");
//
//        // 批量保存
//        for (User user : users) {
//            userService.createUser(user.getUsername(), "123456", user.getPhone(), user.getEmail(), user.getIdCard(), 25);
//            System.out.println("保存用户: " + user.getUsername());
//        }
//
//        // 查询所有用户
//        List<User> allUsers = userMapper.selectAllUsers();
//        System.out.println("查询到 " + allUsers.size() + " 个用户");
//
//        // 验证每个用户的数据
//        for (User user : allUsers) {
//            System.out.println("用户: " + user.getUsername() +
//                    ", 手机: " + user.getPhone() +
//                    ", 邮箱: " + user.getEmail() +
//                    ", 身份证: " + user.getIdCard());
//        }
//
//        System.out.println("✓ 批量操作演示完成");
//    }
//
//    /**
//     * 演示条件查询
//     */
//    private void demonstrateConditionalQueries() {
//        System.out.println("\n--- 条件查询演示 ---");
//
//        // 创建测试用户
//        User testUser = createUser("测试用户", "13800138999", "test@example.com", "110101199001019999");
//        userService.createUser(testUser.getUsername(), "123456", testUser.getPhone(), testUser.getEmail(), testUser.getIdCard(), 25);
//
//        // 根据手机号查询
//        User userByPhone = userMapper.selectByPhone("13800138999");
//        if (userByPhone != null) {
//            System.out.println("根据手机号查询到用户: " + userByPhone.getUsername());
//            System.out.println("手机号: " + userByPhone.getPhone());
//        }
//
//        // 根据邮箱查询
//        User userByEmail = userMapper.selectByEmail("test@example.com");
//        if (userByEmail != null) {
//            System.out.println("根据邮箱查询到用户: " + userByEmail.getUsername());
//            System.out.println("邮箱: " + userByEmail.getEmail());
//        }
//
//        // 根据身份证查询
//        User userByIdCard = userMapper.selectByIdCard("110101199001019999");
//        if (userByIdCard != null) {
//            System.out.println("根据身份证查询到用户: " + userByIdCard.getUsername());
//        }
//
//        System.out.println("✓ 条件查询演示完成");
//    }
//
//    /**
//     * 演示更新操作
//     */
//    private void demonstrateUpdateOperations() {
//        System.out.println("\n--- 更新操作演示 ---");
//
//        // 创建用户
//        User user = createUser("更新测试", "13800138888", "update@example.com", "110101199001018888");
//        user = userService.createUser(user.getUsername(), "123456", user.getPhone(), user.getEmail(), user.getIdCard(), 25);
//
//        System.out.println("原始用户信息:");
//        System.out.println("用户名: " + user.getUsername());
//        System.out.println("手机号: " + user.getPhone());
//        System.out.println("邮箱: " + user.getEmail());
//        System.out.println("身份证: " + user.getIdCard());
//
//        // 更新用户信息
//        user.setPhone("13800138889");
//        user.setEmail("updated@example.com");
//        user.setIdCard("110101199001018889");
//
//        userService.updateUser(user.getId(), user.getPhone(), user.getEmail(), user.getIdCard());
//        System.out.println("用户信息已更新");
//
//        // 查询更新后的用户
//        User updatedUser = userMapper.selectById(user.getId());
//        System.out.println("更新后的用户信息:");
//        System.out.println("用户名: " + updatedUser.getUsername());
//        System.out.println("手机号: " + updatedUser.getPhone());
//        System.out.println("邮箱: " + updatedUser.getEmail());
//        System.out.println("身份证: " + updatedUser.getIdCard());
//
//        // 验证更新结果
//        assert "13800138889".equals(updatedUser.getPhone());
//        assert "updated@example.com".equals(updatedUser.getEmail());
//        assert "110101199001018889".equals(updatedUser.getIdCard());
//
//        System.out.println("✓ 更新操作演示完成");
//    }
//
//    /**
//     * 创建用户对象
//     */
//    private User createUser(String userName, String phone, String email, String idCard) {
//        User user = new User();
//        user.setUsername(userName);
//        user.setPhone(phone);
//        user.setEmail(email);
//        user.setIdCard(idCard);
//        return user;
//    }
//
//    /**
//     * 演示加密策略
//     */
//    public void demonstrateEncryptionStrategies() {
//        System.out.println("\n--- 加密策略演示 ---");
//
//        String testData = "这是测试数据";
//        System.out.println("原始数据: " + testData);
//
//        // 这里可以演示不同的加密策略
//        // 由于加密策略是通过注解配置的，这里主要展示数据流程
//
//        System.out.println("✓ 加密策略演示完成");
//    }
//}
