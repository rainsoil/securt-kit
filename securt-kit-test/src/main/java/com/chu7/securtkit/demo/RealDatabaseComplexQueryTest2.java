package com.chu7.securtkit.test;

import com.chu7.securtkit.service.FieldEncryptionService;
import com.chu7.securtkit.test.mapper.UserMapper;
import com.chu7.securtkit.test.mapper.UserProfileMapper;
import com.chu7.securtkit.test.mapper.OrderMapper;
import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.entity.UserProfile;
import com.chu7.securtkit.test.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 真实数据库复杂查询加密测试类
 * 测试与数据库交互的复杂查询场景下的字段加密功能
 * 
 * @author chu7
 * @date 2025/9/23
 */
@Slf4j
@SpringBootApplication
public class RealDatabaseComplexQueryTest2 {

    public static void main(String[] args) {
        SpringApplication.run(RealDatabaseComplexQueryTest2.class, args);
    }

    @Component
    public static class RealDatabaseTestRunner implements CommandLineRunner {

        @Autowired
        private UserMapper userMapper;
        
        @Autowired
        private UserProfileMapper userProfileMapper;
        
        @Autowired
        private OrderMapper orderMapper;
        
        @Autowired
        private FieldEncryptionService fieldEncryptionService;

        @Override
        public void run(String... args) throws Exception {
            log.info("=== 开始真实数据库复杂查询测试 ===");
            
            // 清理测试数据
            orderMapper.delete(null);
            userProfileMapper.delete(null);
            userMapper.delete(null);
            
            // 准备测试数据
            prepareTestData();
            
//            // 执行各种复杂查询测试
//            testBasicQuery();
//            testAliasQuery();
//            testStatisticsQuery();
//            testComplexConditions();
//            testCaseWhenQuery();
//            testSubQuery();
//            testAggregations();
//            testWindowFunctions();
            
            // 执行多表关联查询测试
            testTwoTableJoin();
            testThreeTableJoin();
            testMultiTableWithAggregation();
            testMultiTableWithSubQuery();
            testMultiTableWithCaseWhen();
            testMultiTableWithWindowFunction();
            
            testFieldEncryptionService();
            
            log.info("=== 真实数据库复杂查询测试完成 ===");
        }

        /**
         * 准备测试数据
         */
        private void prepareTestData() {
            log.info("--- 准备测试数据 ---");
            
            // 创建测试用户
            User user1 = createUser(1L, "user1", "password123", "13800138001", "user1@example.com", "110101199001011234", 25);
            User user2 = createUser(2L, "user2", "password456", "13800138002", "user2@example.com", "310101199002021234", 30);
            User user3 = createUser(3L, "user3", "password789", "13800138003", "user3@example.com", "440101199003031234", 35);
            
            // 插入用户
            userMapper.insert(user1);
            userMapper.insert(user2);
            userMapper.insert(user3);
            
            // 创建用户详情
            UserProfile profile1 = createUserProfile(1L, 1L, "张三", "110101199001011234", "M", "软件工程师", "北京市朝阳区", "高级开发工程师");
            UserProfile profile2 = createUserProfile(2L, 2L, "李四", "310101199002021234", "F", "产品经理", "上海市浦东区", "资深产品经理");
            UserProfile profile3 = createUserProfile(3L, 3L, "王五", "440101199003031234", "M", "设计师", "广州市天河区", "UI/UX设计师");
            
            // 插入用户详情
            userProfileMapper.insert(profile1);
            userProfileMapper.insert(profile2);
            userProfileMapper.insert(profile3);
            
            // 创建订单
            Order order1 = createOrder(1L, 1L, "ORD001", "张三", "13800138001", 1000.00, "已完成", "iPhone 15", 1);
            Order order2 = createOrder(2L, 1L, "ORD002", "张三", "13800138001", 2000.00, "已完成", "MacBook Pro", 1);
            Order order3 = createOrder(3L, 2L, "ORD003", "李四", "13800138002", 1500.00, "进行中", "iPad Air", 1);
            Order order4 = createOrder(4L, 2L, "ORD004", "李四", "13800138002", 800.00, "已完成", "AirPods", 2);
            Order order5 = createOrder(5L, 3L, "ORD005", "王五", "13800138003", 3000.00, "已完成", "Mac Studio", 1);
            Order order6 = createOrder(6L, 3L, "ORD006", "王五", "13800138003", 1200.00, "已完成", "Apple Watch", 1);
            Order order7 = createOrder(7L, 3L, "ORD007", "王五", "13800138003", 500.00, "已完成", "Magic Mouse", 1);
            
            // 插入订单
            orderMapper.insert(order1);
            orderMapper.insert(order2);
            orderMapper.insert(order3);
            orderMapper.insert(order4);
            orderMapper.insert(order5);
            orderMapper.insert(order6);
            orderMapper.insert(order7);
            
            log.info("测试数据准备完成，插入了3个用户、3个用户详情、7个订单");
        }

        /**
         * 测试1：基本查询（测试MyBatis拦截器）
         */
        public void testBasicQuery() {
            log.info("=== 测试基本查询 ===");
            
            // 查询所有用户（会触发MyBatis拦截器进行解密）
            List<User> users = userMapper.selectList(null);
            log.info("查询到用户数量: {}", users.size());
            
            for (User user : users) {
                log.debug("用户信息: ID={}, 用户名={}, 密码={}, 手机={}, 邮箱={}, 身份证={}", 
                    user.getId(), user.getUsername(), user.getPassword(), 
                    user.getPhone(), user.getEmail(), user.getIdCard());
            }
            
            log.info("基本查询测试完成");
        }

        /**
         * 测试2：带别名的查询
         */
        public void testAliasQuery() {
            log.info("=== 测试带别名的查询 ===");
            
            // 执行带别名的查询（会触发MyBatis拦截器进行解密）
            List<Map<String, Object>> usersWithAlias = userMapper.selectUsersWithAlias(20);
            log.info("带别名查询结果数量: {}", usersWithAlias.size());
            
            for (Map<String, Object> result : usersWithAlias) {
                log.debug("别名查询结果: {}", result);
            }
            
            log.info("带别名查询测试完成");
        }

        /**
         * 测试3：统计查询
         */
        public void testStatisticsQuery() {
            log.info("=== 测试统计查询 ===");
            
            // 执行统计查询
            Map<String, Object> statistics = userMapper.selectUserStatistics();
            log.info("统计查询结果: {}", statistics);
            
            log.info("统计查询测试完成");
        }

        /**
         * 测试4：复杂条件查询
         */
        public void testComplexConditions() {
            log.info("=== 测试复杂条件查询 ===");
            
            // 执行复杂条件查询
            List<Map<String, Object>> complexResults = userMapper.selectUsersWithComplexConditions(25, 40);
            log.info("复杂条件查询结果数量: {}", complexResults.size());
            
            for (Map<String, Object> result : complexResults) {
                log.debug("复杂条件查询结果: {}", result);
            }
            
            log.info("复杂条件查询测试完成");
        }

        /**
         * 测试5：FieldEncryptionService直接使用
         */
        public void testFieldEncryptionService() {
            log.info("=== 测试FieldEncryptionService直接使用 ===");
            
            // 查询一个用户
            List<User> users = userMapper.selectList(null);
            if (!users.isEmpty()) {
                User user = users.get(0);
                log.debug("原始用户数据: {}", user);
                
                // 使用FieldEncryptionService进行加密
                User encryptedUser = fieldEncryptionService.encryptObject(user);
                log.debug("加密后用户数据: {}", encryptedUser);
                
                // 使用FieldEncryptionService进行解密
                User decryptedUser = fieldEncryptionService.decryptObject(encryptedUser);
                log.debug("解密后用户数据: {}", decryptedUser);
                
                // 验证数据一致性
                assert user.getPassword().equals(decryptedUser.getPassword());
                assert user.getPhone().equals(decryptedUser.getPhone());
                assert user.getEmail().equals(decryptedUser.getEmail());
                assert user.getIdCard().equals(decryptedUser.getIdCard());
                
                log.info("FieldEncryptionService测试通过！");
            }
            
            log.info("FieldEncryptionService测试完成");
        }

        /**
         * 测试6：CASE WHEN查询
         */
        public void testCaseWhenQuery() {
            log.info("=== 测试CASE WHEN查询 ===");
            
            // 执行CASE WHEN查询
            List<Map<String, Object>> ageGroupResults = userMapper.selectUsersWithAgeGroup();
            log.info("年龄分组查询结果数量: {}", ageGroupResults.size());
            
            for (Map<String, Object> result : ageGroupResults) {
                log.debug("年龄分组结果: {}", result);
            }
            
            log.info("CASE WHEN查询测试完成");
        }

        /**
         * 测试7：子查询
         */
        public void testSubQuery() {
            log.info("=== 测试子查询 ===");
            
            // 执行子查询
            List<Map<String, Object>> subQueryResults = userMapper.selectUsersWithSubQuery(25);
            log.info("子查询结果数量: {}", subQueryResults.size());
            
            for (Map<String, Object> result : subQueryResults) {
                log.debug("子查询结果: {}", result);
            }
            
            log.info("子查询测试完成");
        }

        /**
         * 测试8：聚合函数
         */
        public void testAggregations() {
            log.info("=== 测试聚合函数 ===");
            
            // 执行聚合查询
            Map<String, Object> aggregations = userMapper.selectUserAggregations();
            log.info("聚合查询结果: {}", aggregations);
            
            log.info("聚合函数测试完成");
        }

        /**
         * 测试9：窗口函数
         */
        public void testWindowFunctions() {
            log.info("=== 测试窗口函数 ===");
            
            // 执行窗口函数查询
            List<Map<String, Object>> windowResults = userMapper.selectUsersWithWindowFunctions();
            log.info("窗口函数查询结果数量: {}", windowResults.size());
            
            for (Map<String, Object> result : windowResults) {
                log.debug("窗口函数结果: {}", result);
            }
            
            log.info("窗口函数测试完成");
        }

        /**
         * 测试10：两表关联查询（用户表 + 用户详情表）
         */
        public void testTwoTableJoin() {
            log.info("=== 测试两表关联查询 ===");
            
            // 执行两表关联查询
            List<Map<String, Object>> joinResults = userMapper.selectUsersWithProfile(20);
            log.info("两表关联查询结果数量: {}", joinResults.size());
            
            for (Map<String, Object> result : joinResults) {
                log.debug("两表关联查询结果: {}", result);
            }
            
            log.info("两表关联查询测试完成");
        }

        /**
         * 测试11：三表关联查询（用户表 + 用户详情表 + 订单表）
         */
        public void testThreeTableJoin() {
            log.info("=== 测试三表关联查询 ===");
            
            // 执行三表关联查询
            List<Map<String, Object>> joinResults = userMapper.selectUsersWithProfileAndOrders(25, 40);
            log.info("三表关联查询结果数量: {}", joinResults.size());
            
            for (Map<String, Object> result : joinResults) {
                log.debug("三表关联查询结果: {}", result);
            }
            
            log.info("三表关联查询测试完成");
        }

        /**
         * 测试12：多表关联 + 聚合函数查询
         */
        public void testMultiTableWithAggregation() {
            log.info("=== 测试多表关联 + 聚合函数查询 ===");
            
            // 执行多表关联聚合查询
            List<Map<String, Object>> aggregationResults = userMapper.selectUsersWithOrderStatistics(20);
            log.info("多表关联聚合查询结果数量: {}", aggregationResults.size());
            
            for (Map<String, Object> result : aggregationResults) {
                log.debug("多表关联聚合查询结果: {}", result);
            }
            
            log.info("多表关联聚合查询测试完成");
        }

        /**
         * 测试13：多表关联 + 子查询
         */
        public void testMultiTableWithSubQuery() {
            log.info("=== 测试多表关联 + 子查询 ===");
            
            // 执行多表关联子查询
            List<Map<String, Object>> subQueryResults = userMapper.selectUsersWithOrderSubQuery(1000.0);
            log.info("多表关联子查询结果数量: {}", subQueryResults.size());
            
            for (Map<String, Object> result : subQueryResults) {
                log.debug("多表关联子查询结果: {}", result);
            }
            
            log.info("多表关联子查询测试完成");
        }

        /**
         * 测试14：多表关联 + CASE WHEN查询
         */
        public void testMultiTableWithCaseWhen() {
            log.info("=== 测试多表关联 + CASE WHEN查询 ===");
            
            // 执行多表关联CASE WHEN查询
            List<Map<String, Object>> caseWhenResults = userMapper.selectUsersWithOrderLevel(20);
            log.info("多表关联CASE WHEN查询结果数量: {}", caseWhenResults.size());
            
            for (Map<String, Object> result : caseWhenResults) {
                log.debug("多表关联CASE WHEN查询结果: {}", result);
            }
            
            log.info("多表关联CASE WHEN查询测试完成");
        }

        /**
         * 测试15：多表关联 + 窗口函数查询
         */
        public void testMultiTableWithWindowFunction() {
            log.info("=== 测试多表关联 + 窗口函数查询 ===");
            
            // 执行多表关联窗口函数查询
            List<Map<String, Object>> windowResults = userMapper.selectUsersWithOrderRanking(20);
            log.info("多表关联窗口函数查询结果数量: {}", windowResults.size());
            
            for (Map<String, Object> result : windowResults) {
                log.debug("多表关联窗口函数查询结果: {}", result);
            }
            
            log.info("多表关联窗口函数查询测试完成");
        }

        /**
         * 创建测试用户
         */
        private User createUser(Long id, String username, String password, String phone, String email, String idCard, Integer age) {
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setPassword(password);
            user.setPhone(phone);
            user.setEmail(email);
            user.setIdCard(idCard);
            user.setAge(age);
            return user;
        }

        /**
         * 创建用户详情
         */
        private UserProfile createUserProfile(Long id, Long userId, String realName, String idCard, 
                                            String gender, String occupation, String address, String remark) {
            UserProfile profile = new UserProfile();
            profile.setId(id);
            profile.setUserId(userId);
            profile.setRealName(realName);
            profile.setIdCard(idCard);
            profile.setGender(gender);
            profile.setOccupation(occupation);
            profile.setAddress(address);
            profile.setRemark(remark);
            return profile;
        }

        /**
         * 创建订单
         */
        private Order createOrder(Long id, Long userId, String orderNo, String customerName, 
                                String customerPhone, Double amount, String status, String productName, Integer quantity) {
            Order order = new Order();
            order.setId(id);
            order.setUserId(userId);
            order.setOrderNo(orderNo);
            order.setCustomerName(customerName);
            order.setCustomerPhone(customerPhone);
            order.setAmount(new java.math.BigDecimal(amount.toString()));
            order.setStatus(status);
            order.setProductName(productName);
            order.setQuantity(quantity);
            order.setCreateTime(java.time.LocalDateTime.now());
            order.setUpdateTime(java.time.LocalDateTime.now());
            return order;
        }

    }
}
