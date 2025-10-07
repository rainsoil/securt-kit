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
//import java.util.Map;
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
//        demonstrateBasicFunctionality();
////
////        // 演示批量操作
////        demonstrateBatchOperations();
////
////        // 演示条件查询
////        demonstrateConditionalQueries();
////
////        // 演示更新操作
////        demonstrateUpdateOperations();
//
//        // 复杂SQL测试
//        testComplexSql();
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
//
//    /**
//     * 复杂SQL测试方法
//     */
//    public void testComplexSql() {
//        System.out.println("\n=== 复杂SQL加密测试 ===");
//
//        try {
//            // 测试1: 带子查询的复杂UPDATE
////            testComplexUpdate();
//
////            // 测试2: 带聚合函数的复杂SELECT
////            testComplexSelect();
////
////            // 测试3: 带别名的复杂查询
////            testComplexSelectWithAlias();
////
////            // 测试4: 分页查询
//            testComplexSelectWithLimit();
////
//            System.out.println("✓ 所有复杂SQL测试完成");
//
//        } catch (Exception e) {
//            System.err.println("复杂SQL测试过程中出现错误: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 测试带子查询的复杂UPDATE
//     */
//    private void testComplexUpdate() {
//        System.out.println("\n--- 测试带子查询的复杂UPDATE ---");
//
//        // 先创建一个测试用户
//        User user = userService.createUser("复杂UPDATE测试", "123456", "13900139000", "complex_update@example.com", "110101199001019999", 30);
//        System.out.println("创建测试用户ID: " + user.getId());
//
//        // 使用复杂子查询更新
//        int updateResult = userMapper.updateUserWithSubquery(
//            user.getId(),
//            "复杂UPDATE测试",
//            "13900139001",
//            "updated_complex@example.com",
//            "110101199001019998"
//        );
//        System.out.println("更新影响行数: " + updateResult);
//
//        if (updateResult > 0) {
//            // 验证更新后的数据
//            User updatedUser = userMapper.selectById(user.getId());
//            System.out.println("更新后数据验证:");
//            System.out.println("- 手机号: " + updatedUser.getPhone());
//            System.out.println("- 邮箱: " + updatedUser.getEmail());
//            System.out.println("- 身份证: " + updatedUser.getIdCard());
//            System.out.println("✓ 复杂UPDATE测试成功");
//        } else {
//            System.out.println("❌ 复杂UPDATE测试失败 - 没有更新任何记录");
//        }
//    }
//
//    /**
//     * 测试带聚合函数的复杂SELECT
//     */
//    private void testComplexSelect() {
//        System.out.println("\n--- 测试带聚合函数的复杂SELECT ---");
//
//        // 创建一些测试数据
//        userService.createUser("聚合测试1", "123456", "13800138001", "agg1@example.com", "110101199001011001", 25);
//        userService.createUser("聚合测试2", "123456", "13800138002", "agg2@example.com", "110101199001011002", 30);
//        userService.createUser("聚合测试3", "123456", "13800138003", "agg3@example.com", "110101199001011003", 35);
//
//        // 查询用户统计信息
//        Map<String, Object> stats = userMapper.selectUserStatistics();
//        System.out.println("用户统计信息:");
//        System.out.println("- 总用户数: " + stats.get("total_users"));
//        System.out.println("- 平均年龄: " + stats.get("avg_age"));
//        System.out.println("- 最大年龄: " + stats.get("max_age"));
//        System.out.println("- 最小年龄: " + stats.get("min_age"));
//        System.out.println("- 唯一手机号数量: " + stats.get("unique_phones"));
//
//        // 按年龄分组查询
//        List<Map<String, Object>> ageGroups = userMapper.selectUsersByAgeGroup();
//        System.out.println("年龄分组统计:");
//        for (Map<String, Object> group : ageGroups) {
//            System.out.println("- " + group.get("age_group") + ": " + group.get("user_count") + "人");
//        }
//
//        System.out.println("✓ 复杂SELECT测试成功");
//    }
//
//    /**
//     * 测试带别名的复杂查询
//     */
//    private void testComplexSelectWithAlias() {
//        System.out.println("\n--- 测试带别名的复杂查询 ---");
//
//        // 创建测试数据
//        userService.createUser("别名测试", "123456", "13700137001", "alias@example.com", "110101199001011601", 28);
//
//        // 使用别名查询
//        List<Map<String, Object>> usersWithAlias = userMapper.selectUsersWithAlias(20);
//        System.out.println("带别名的查询结果 (年龄>20的用户):");
//
//        int count = 0;
//        for (Map<String, Object> userMap : usersWithAlias) {
//            count++;
//            System.out.println("用户" + count + ":");
//            System.out.println("  - ID: " + userMap.get("user_id"));
//            System.out.println("  - 姓名: " + userMap.get("user_name"));
//            System.out.println("  - 手机: " + userMap.get("user_phone"));
//            System.out.println("  - 邮箱: " + userMap.get("user_email"));
//            System.out.println("  - 身份证: " + userMap.get("user_id_card"));
//            System.out.println("  - 年龄: " + userMap.get("user_age"));
//
//            if (count >= 5) { // 只显示前5个
//                System.out.println("  ... (显示前5个用户)");
//                break;
//            }
//        }
//
//        System.out.println("总共查询到 " + usersWithAlias.size() + " 个用户");
//        System.out.println("✓ 带别名的复杂查询测试成功");
//    }
//
//    /**
//     * 测试分页查询
//     */
//    private void testComplexSelectWithLimit() {
//        System.out.println("\n--- 测试分页查询 ---");
//
//        // 创建多个测试用户
//        for (int i = 0; i < 5; i++) {
//            userService.createUser("分页测试" + i, "123456", "1320013200" + i, "page" + i + "@example.com", "11010119900101150" + i, 20 + i);
//        }
//
//        // 分页查询（第一页，每页3条）
//        List<User> pagedUsers = userMapper.selectUsersWithLimit(0, 3);
//        System.out.println("分页查询结果（第一页，每页3条）:");
//
//        for (int i = 0; i < pagedUsers.size(); i++) {
//            User user = pagedUsers.get(i);
//            System.out.println("第" + (i+1) + "条:");
//            System.out.println("  - 姓名: " + user.getUsername());
//            System.out.println("  - 年龄: " + user.getAge());
//            System.out.println("  - 手机: " + user.getPhone());
//            System.out.println("  - 邮箱: " + user.getEmail());
//        }
//
//        System.out.println("✓ 分页查询测试成功");
//    }
//}
