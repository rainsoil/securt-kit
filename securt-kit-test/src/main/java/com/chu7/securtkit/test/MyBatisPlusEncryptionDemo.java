package com.chu7.securtkit.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * MyBatis-Plus POJO加密功能演示
 * 展示各种MyBatis-Plus操作中的加密解密效果
 * 
 * @author chu7
 * @date 2025-09-21
 */
@Component
@Slf4j
public class MyBatisPlusEncryptionDemo implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== MyBatis-Plus POJO加密功能演示开始 ===");
        
        // 清理测试数据
        cleanupTestData();
        
        // 1. 基本CRUD操作演示
//        demonstrateBasicCrud();
        
//        // 2. 批量操作演示
        demonstrateBatchOperations();
//
//        // 3. 分页查询演示
//        demonstratePagination();
//
//        // 4. 条件查询演示
//        demonstrateConditionalQuery();
//
//        // 5. 更新操作演示
//        demonstrateUpdateOperations();
        
        log.info("=== MyBatis-Plus POJO加密功能演示结束 ===");
    }

    /**
     * 基本CRUD操作演示
     */
    private void demonstrateBasicCrud() {
        log.info("\n--- 基本CRUD操作演示 ---");
        
        // 1. 插入操作
        User user1 = createTestUser("MyBatisPlus用户1", "13800138001", "user1@example.com", "110101199001010001");
        boolean insertResult = userService.save(user1);
        log.info("插入结果: {}, 用户ID: {}", insertResult, user1.getId());
        
        // 2. 根据ID查询
        User savedUser = userService.getById(user1.getId());
        log.info("查询结果: {}", savedUser);
        
        // 3. 更新操作
        savedUser.setPhone("13900139001");
        savedUser.setEmail("updated1@example.com");
        savedUser.setIdCard("110101199001019999");
        savedUser.setUpdateTime(new Date());
        
        boolean updateResult = userService.updateById(savedUser);
        log.info("更新结果: {}", updateResult);
        
        // 4. 再次查询验证
        User updatedUser = userService.getById(user1.getId());
        log.info("更新后查询结果: {}", updatedUser);
        
        // 5. 删除操作
        boolean deleteResult = userService.removeById(user1.getId());
        log.info("删除结果: {}", deleteResult);
    }

    /**
     * 批量操作演示
     */
    private void demonstrateBatchOperations() {
        log.info("\n--- 批量操作演示 ---");
        
        // 1. 批量插入
        List<User> users = Arrays.asList(
            createTestUser("批量用户1", "13800138010", "batch1@example.com", "110101199001010010"),
            createTestUser("批量用户2", "13800138011", "batch2@example.com", "110101199001010011"),
            createTestUser("批量用户3", "13800138012", "batch3@example.com", "110101199001010012")
        );
        
        boolean batchInsertResult = userService.saveBatch(users);
        log.info("批量插入结果: {}", batchInsertResult);
        
//        // 2. 批量查询
//        List<User> allUsers = userService.list();
//        log.info("批量查询结果数量: {}", allUsers.size());
//        allUsers.forEach(user -> log.info("用户: {}", user));
//
//        // 3. 批量更新
//        users.forEach(user -> {
//            user.setPhone("139" + user.getPhone().substring(3));
//            user.setEmail("updated_" + user.getEmail());
//            user.setIdCard("110101199001019" + user.getIdCard().substring(14));
//            user.setUpdateTime(new Date());
//        });
//
//        boolean batchUpdateResult = userService.updateBatchById(users);
//        log.info("批量更新结果: {}", batchUpdateResult);
//
//        // 4. 验证批量更新结果
//        List<User> updatedUsers = userService.list(new LambdaQueryWrapper<User>()
//                .like(User::getUsername, "批量用户"));
//        log.info("批量更新后查询结果数量: {}", updatedUsers.size());
//        updatedUsers.forEach(user -> log.info("更新后用户: {}", user));
    }

    /**
     * 分页查询演示
     */
    private void demonstratePagination() {
        log.info("\n--- 分页查询演示 ---");
        
        // 1. 基本分页查询
        Page<User> page = new Page<>(1, 2); // 第1页，每页2条
        IPage<User> result = userService.page(page, new LambdaQueryWrapper<User>()
                .like(User::getUsername, "批量用户")
                .orderByDesc(User::getCreateTime));
        
        log.info("分页查询结果:");
        log.info("当前页: {}, 每页大小: {}, 总记录数: {}, 总页数: {}", 
                result.getCurrent(), result.getSize(), result.getTotal(), result.getPages());
        
        result.getRecords().forEach(user -> log.info("分页用户: {}", user));
        
//        // 2. 第二页查询
//        if (result.hasNext()) {
//            Page<User> page2 = new Page<>(2, 2);
//            IPage<User> result2 = userService.page(page2, new LambdaQueryWrapper<User>()
//                    .like(User::getUsername, "批量用户")
//                    .orderByDesc(User::getCreateTime));
//
//            log.info("第二页查询结果:");
//            log.info("当前页: {}, 每页大小: {}, 总记录数: {}",
//                    result2.getCurrent(), result2.getSize(), result2.getTotal());
//
//            result2.getRecords().forEach(user -> log.info("第二页用户: {}", user));
//        }
    }

    /**
     * 条件查询演示
     */
    private void demonstrateConditionalQuery() {
        log.info("\n--- 条件查询演示 ---");
        
        // 1. 等值查询
        List<User> equalUsers = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "批量用户1"));
        log.info("等值查询结果数量: {}", equalUsers.size());
        equalUsers.forEach(user -> log.info("等值查询用户: {}", user));
        
        // 2. 模糊查询
        List<User> likeUsers = userService.list(new LambdaQueryWrapper<User>()
                .like(User::getUsername, "批量")
                .orderByDesc(User::getCreateTime));
        log.info("模糊查询结果数量: {}", likeUsers.size());
        likeUsers.forEach(user -> log.info("模糊查询用户: {}", user));
        
        // 3. 范围查询
        List<User> rangeUsers = userService.list(new LambdaQueryWrapper<User>()
                .between(User::getAge, 20, 30)
                .orderByAsc(User::getAge));
        log.info("范围查询结果数量: {}", rangeUsers.size());
        rangeUsers.forEach(user -> log.info("范围查询用户: {}", user));
        
        // 4. 多条件查询
        List<User> multiUsers = userService.list(new LambdaQueryWrapper<User>()
                .like(User::getUsername, "批量")
                .gt(User::getAge, 20)
                .isNotNull(User::getPhone)
                .orderByDesc(User::getCreateTime));
        log.info("多条件查询结果数量: {}", multiUsers.size());
        multiUsers.forEach(user -> log.info("多条件查询用户: {}", user));
    }

    /**
     * 更新操作演示
     */
    private void demonstrateUpdateOperations() {
        log.info("\n--- 更新操作演示 ---");
        
        // 1. 根据ID更新
        List<User> users = userService.list(new LambdaQueryWrapper<User>()
                .like(User::getUsername, "批量用户")
                .last("LIMIT 1"));
        
        if (!users.isEmpty()) {
            User user = users.get(0);
            user.setPhone("13900139999");
            user.setEmail("final_update@example.com");
            user.setIdCard("110101199001019999");
            user.setUpdateTime(new Date());
            
            log.info("更新前用户信息: {}", user);
            boolean updateResult = userService.updateById(user);
            log.info("根据ID更新结果: {}", updateResult);
            
            // 验证更新结果
            User updatedUser = userService.getById(user.getId());
            log.info("更新后用户信息: {}", updatedUser);
        }


        // 2. 条件更新
        boolean conditionUpdateResult = userService.update(new User(), 
                new LambdaUpdateWrapper<User>()
                        .like(User::getUsername, "批量用户2")
                        .set(User::getPhone, "13900139002")
                        .set(User::getEmail, "condition_update@example.com")
                        .set(User::getIdCard, "110101199001019998")
                        .set(User::getUpdateTime, LocalDateTime.now()));
        log.info("条件更新结果: {}", conditionUpdateResult);
        
        // 3. 验证条件更新结果
        List<User> updatedUsers = userService.list(new LambdaQueryWrapper<User>()
                .like(User::getUsername, "批量用户2"));
        log.info("条件更新后查询结果数量: {}", updatedUsers.size());
        updatedUsers.forEach(user -> log.info("条件更新后用户: {}", user));
    }

    /**
     * 创建测试用户
     */
    private User createTestUser(String username, String phone, String email, String idCard) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("123456");
        user.setPhone(phone);
        user.setEmail(email);
        user.setIdCard(idCard);
        user.setAge(25);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        return user;
    }

    /**
     * 清理测试数据
     */
    private void cleanupTestData() {
        log.info("清理测试数据...");
        userService.remove(new LambdaQueryWrapper<User>()
                .like(User::getUsername, "MyBatisPlus")
                .or()
                .like(User::getUsername, "批量用户"));
        log.info("测试数据清理完成");
    }
}
