package com.chu7.securtkit.test;

import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

/**
 * POJO加密功能演示
 * 展示各种操作中的加密解密效果
 * 
 * @author chu7
 * @date 2025-09-21
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PojoEncryptionDemo implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== POJO加密功能演示开始 ===");
        
        // 清理测试数据
        cleanupTestData();
        
        // 1. 基本CRUD操作演示
        demonstrateBasicCrud();
        
        // 2. 加密策略演示
        demonstrateEncryptionStrategies();
        
        // 3. 批量操作演示
        demonstrateBatchOperations();
        
        // 4. 数据一致性演示
        demonstrateDataConsistency();
        
        log.info("=== POJO加密功能演示结束 ===");
    }

    /**
     * 基本CRUD操作演示
     */
    private void demonstrateBasicCrud() {
        log.info("\n--- 基本CRUD操作演示 ---");
        
        // 1. 创建用户
        User user = userService.createUser("演示用户", "123456", "13800138888", 
                "demo@example.com", "110101199001018888", 25);
        
        log.info("创建用户: {}", user);
        log.info("原始密码: 123456, 加密后: {}", user.getPassword());
        log.info("原始手机号: 13800138888, 加密后: {}", user.getPhone());
        log.info("原始邮箱: demo@example.com, 加密后: {}", user.getEmail());
        log.info("原始身份证: 110101199001018888, 加密后: {}", user.getIdCard());
        
        // 2. 查询用户
        User foundUser = userService.findByPhone("13800138888");
        log.info("查询用户: {}", foundUser);
        log.info("解密后手机号: {}", foundUser.getPhone());
        log.info("解密后邮箱: {}", foundUser.getEmail());
        log.info("解密后身份证: {}", foundUser.getIdCard());
        
        // 3. 更新用户
        User updatedUser = userService.updateUser(user.getId(), "13900139999", 
                "updated@example.com", "110101199001019999");
        
        log.info("更新用户: {}", updatedUser);
        log.info("更新后加密手机号: {}", updatedUser.getPhone());
        log.info("更新后加密邮箱: {}", updatedUser.getEmail());
        log.info("更新后加密身份证: {}", updatedUser.getIdCard());
        
        // 4. 验证更新后的解密
        User finalUser = userService.findByPhone("13900139999");
        log.info("最终查询用户: {}", finalUser);
        log.info("最终解密手机号: {}", finalUser.getPhone());
        log.info("最终解密邮箱: {}", finalUser.getEmail());
        log.info("最终解密身份证: {}", finalUser.getIdCard());
        
        // 5. 删除用户
        boolean deleteResult = userService.deleteUser(user.getId());
        log.info("删除用户结果: {}", deleteResult);
    }

    /**
     * 加密策略演示
     */
    private void demonstrateEncryptionStrategies() {
        log.info("\n--- 加密策略演示 ---");
        
        User user = userService.createUser("策略测试用户", "123456", "13800138887", 
                "strategy@example.com", "110101199001018887", 25);
        
        log.info("用户信息: {}", user);
        
        // 分析不同字段的加密结果
        String password = user.getPassword();
        String phone = user.getPhone();
        String email = user.getEmail();
        String idCard = user.getIdCard();
        
        log.info("密码加密分析:");
        log.info("  原始: 123456");
        log.info("  加密后: {}", password);
        log.info("  长度: {} (MD5加密后应该是32位)", password.length());
        log.info("  格式: {} (应该是十六进制)", password.matches("[a-f0-9]+") ? "正确" : "错误");
        
        log.info("手机号加密分析:");
        log.info("  原始: 13800138887");
        log.info("  加密后: {}", phone);
        log.info("  长度: {} (DES加密后长度会变化)", phone.length());
        log.info("  是否不同: {}", !phone.equals("13800138887") ? "是" : "否");
        
        log.info("邮箱加密分析:");
        log.info("  原始: strategy@example.com");
        log.info("  加密后: {}", email);
        log.info("  长度: {} (AES加密后长度会变化)", email.length());
        log.info("  是否不同: {}", !email.equals("strategy@example.com") ? "是" : "否");
        
        log.info("身份证加密分析:");
        log.info("  原始: 110101199001018887");
        log.info("  加密后: {}", idCard);
        log.info("  长度: {} (Base64加密后长度会变化)", idCard.length());
        log.info("  是否不同: {}", !idCard.equals("110101199001018887") ? "是" : "否");
        
        // 清理
        userService.deleteUser(user.getId());
    }

    /**
     * 批量操作演示
     */
    private void demonstrateBatchOperations() {
        log.info("\n--- 批量操作演示 ---");
        
        // 1. 批量创建用户
        User user1 = userService.createUser("批量用户1", "123456", "13800138001", 
                "batch1@example.com", "110101199001010001", 25);
        User user2 = userService.createUser("批量用户2", "123456", "13800138002", 
                "batch2@example.com", "110101199001010002", 26);
        User user3 = userService.createUser("批量用户3", "123456", "13800138003", 
                "batch3@example.com", "110101199001010003", 27);
        
        log.info("批量创建用户完成");
        log.info("用户1: {}", user1);
        log.info("用户2: {}", user2);
        log.info("用户3: {}", user3);
        
        // 2. 批量查询
        List<User> allUsers = userService.getAllUsers();
        log.info("查询到所有用户数量: {}", allUsers.size());
        
        // 3. 验证每个用户的加密情况
        allUsers.forEach(user -> {
            if (user.getUsername().startsWith("批量用户")) {
                log.info("用户: {}, 密码加密: {}, 手机号加密: {}, 邮箱加密: {}, 身份证加密: {}", 
                        user.getUsername(),
                        !user.getPassword().equals("123456") ? "是" : "否",
                        !user.getPhone().startsWith("1380013800") ? "是" : "否",
                        !user.getEmail().startsWith("batch") ? "是" : "否",
                        !user.getIdCard().startsWith("11010119900101000") ? "是" : "否");
            }
        });
        
        // 4. 批量更新
        user1.setPhone("13900139001");
        user1.setEmail("updated_batch1@example.com");
        user1.setIdCard("110101199001019001");
        user1.setUpdateTime(new Date());
        
        User updatedUser1 = userService.updateUser(user1.getId(), user1.getPhone(), 
                user1.getEmail(), user1.getIdCard());
        log.info("批量更新用户1: {}", updatedUser1);
        
        // 5. 批量删除
        boolean delete1 = userService.deleteUser(user1.getId());
        boolean delete2 = userService.deleteUser(user2.getId());
        boolean delete3 = userService.deleteUser(user3.getId());
        
        log.info("批量删除结果: 用户1={}, 用户2={}, 用户3={}", delete1, delete2, delete3);
    }

    /**
     * 数据一致性演示
     */
    private void demonstrateDataConsistency() {
        log.info("\n--- 数据一致性演示 ---");
        
        // 1. 创建用户
        User user = userService.createUser("一致性测试", "123456", "13800138886", 
                "consistency@example.com", "110101199001018886", 25);
        
        log.info("创建用户: {}", user);
        
        // 2. 多次查询验证数据一致性
        for (int i = 1; i <= 5; i++) {
            User queriedUser = userService.findByPhone("13800138886");
            log.info("第{}次查询结果: 手机号={}, 邮箱={}, 身份证={}", 
                    i, queriedUser.getPhone(), queriedUser.getEmail(), queriedUser.getIdCard());
            
            // 验证数据一致性
            if (!"13800138886".equals(queriedUser.getPhone())) {
                log.error("第{}次查询手机号不一致!", i);
            }
            if (!"consistency@example.com".equals(queriedUser.getEmail())) {
                log.error("第{}次查询邮箱不一致!", i);
            }
            if (!"110101199001018886".equals(queriedUser.getIdCard())) {
                log.error("第{}次查询身份证不一致!", i);
            }
        }
        
        // 3. 通过不同方式查询验证一致性
        User byPhone = userService.findByPhone("13800138886");
        User byEmail = userService.findByEmail("consistency@example.com");
        
        log.info("通过手机号查询: {}", byPhone);
        log.info("通过邮箱查询: {}", byEmail);
        
        if (byPhone != null && byEmail != null) {
            boolean phoneConsistent = byPhone.getPhone().equals(byEmail.getPhone());
            boolean emailConsistent = byPhone.getEmail().equals(byEmail.getEmail());
            boolean idCardConsistent = byPhone.getIdCard().equals(byEmail.getIdCard());
            
            log.info("数据一致性检查: 手机号={}, 邮箱={}, 身份证={}", 
                    phoneConsistent ? "一致" : "不一致",
                    emailConsistent ? "一致" : "不一致",
                    idCardConsistent ? "一致" : "不一致");
        }
        
        // 清理
        userService.deleteUser(user.getId());
    }

    /**
     * 清理测试数据
     */
    private void cleanupTestData() {
        log.info("清理测试数据...");
        try {
            List<User> allUsers = userService.getAllUsers();
            for (User user : allUsers) {
                if (user.getUsername().contains("演示") || 
                    user.getUsername().contains("策略") || 
                    user.getUsername().contains("批量") || 
                    user.getUsername().contains("一致性")) {
                    userService.deleteUser(user.getId());
                }
            }
            log.info("测试数据清理完成");
        } catch (Exception e) {
            log.warn("清理测试数据时出现异常: {}", e.getMessage());
        }
    }
}
