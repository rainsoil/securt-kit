package com.chu7.securtkit.test.integration;

import com.chu7.securtkit.test.SecurtKitTestApplication;
import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * POJO模式加解密集成测试
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
@SpringBootTest(classes = SecurtKitTestApplication.class)
@ActiveProfiles("test")
@Transactional
public class PoJoEncryptDecryptIntegrationTest {

    @Autowired
    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    public void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("123456");           // 会被MD5加密
        testUser.setPhone("13800138000");         // 会被DES加密
        testUser.setEmail("test@example.com");    // 会被AES加密
        testUser.setIdCard("110101199001011234"); // 会被Base64编码
        testUser.setAge(25);
        testUser.setCreateTime(new Date());
        testUser.setUpdateTime(new Date());
    }

    @Test
    public void testInsertAndSelect() {
        log.info("=== 测试插入和查询功能 ===");
        
        // 1. 插入用户（入参会被自动加密）
        log.info("插入用户，原始数据：{}", testUser);
        int insertResult = userMapper.insertUser(testUser);
        assertEquals(1, insertResult);
        assertNotNull(testUser.getId());
        log.info("插入成功，用户ID：{}", testUser.getId());

        // 2. 根据ID查询用户（结果会被自动解密）
        User foundUser = userMapper.selectById(testUser.getId());
        assertNotNull(foundUser);
        log.info("查询到的用户：{}", foundUser);

        // 3. 验证解密后的数据是否正确
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        assertEquals(testUser.getPhone(), foundUser.getPhone());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        assertEquals(testUser.getIdCard(), foundUser.getIdCard());
        assertEquals(testUser.getAge(), foundUser.getAge());
        
        // 注意：密码使用MD5单向加密，无法解密，所以会返回加密后的值
        assertNotEquals(testUser.getPassword(), foundUser.getPassword());
        assertEquals(32, foundUser.getPassword().length()); // MD5固定32位

        log.info("✅ 插入和查询测试通过");
    }

    @Test
    public void testSelectByEncryptedFields() {
        log.info("=== 测试根据加密字段查询 ===");

        // 先插入用户
        userMapper.insertUser(testUser);

        // 1. 根据手机号查询（手机号在查询时会被自动加密）
        User userByPhone = userMapper.selectByPhone(testUser.getPhone());
        assertNotNull(userByPhone);
        assertEquals(testUser.getPhone(), userByPhone.getPhone());
        log.info("根据手机号查询成功：{}", userByPhone.getPhone());

        // 2. 根据邮箱查询（邮箱在查询时会被自动加密）
        User userByEmail = userMapper.selectByEmail(testUser.getEmail());
        assertNotNull(userByEmail);
        assertEquals(testUser.getEmail(), userByEmail.getEmail());
        log.info("根据邮箱查询成功：{}", userByEmail.getEmail());

        // 3. 根据身份证号查询（身份证号在查询时会被自动加密）
        User userByIdCard = userMapper.selectByIdCard(testUser.getIdCard());
        assertNotNull(userByIdCard);
        assertEquals(testUser.getIdCard(), userByIdCard.getIdCard());
        log.info("根据身份证号查询成功：{}", userByIdCard.getIdCard());

        log.info("✅ 根据加密字段查询测试通过");
    }

    @Test
    public void testComplexQuery() {
        log.info("=== 测试复杂查询条件 ===");

        // 先插入用户
        userMapper.insertUser(testUser);

        // 根据多个加密字段查询
        User user = userMapper.selectByPhoneAndEmail(testUser.getPhone(), testUser.getEmail());
        assertNotNull(user);
        assertEquals(testUser.getPhone(), user.getPhone());
        assertEquals(testUser.getEmail(), user.getEmail());

        log.info("复杂查询结果：{}", user);
        log.info("✅ 复杂查询测试通过");
    }

    @Test
    public void testUpdateOperation() {
        log.info("=== 测试更新操作 ===");

        // 先插入用户
        userMapper.insertUser(testUser);

        // 修改用户信息
        testUser.setPhone("13900139000");
        testUser.setEmail("updated@example.com");
        testUser.setIdCard("110101199002022345");
        testUser.setUpdateTime(new Date());

        // 更新用户（新的加密数据会被自动加密）
        int updateResult = userMapper.updateUser(testUser);
        assertEquals(1, updateResult);

        // 查询更新后的用户
        User updatedUser = userMapper.selectById(testUser.getId());
        assertNotNull(updatedUser);
        assertEquals(testUser.getPhone(), updatedUser.getPhone());
        assertEquals(testUser.getEmail(), updatedUser.getEmail());
        assertEquals(testUser.getIdCard(), updatedUser.getIdCard());

        log.info("更新后的用户：{}", updatedUser);
        log.info("✅ 更新操作测试通过");
    }

    @Test
    public void testListQuery() {
        log.info("=== 测试列表查询 ===");

        // 插入多个用户
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("password" + i);
            user.setPhone("1380013800" + i);
            user.setEmail("user" + i + "@example.com");
            user.setIdCard("11010119900101123" + i);
            user.setAge(20 + i);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            userMapper.insertUser(user);
        }

        // 查询所有用户（所有结果都会被自动解密）
        List<User> allUsers = userMapper.selectAllUsers();
        assertNotNull(allUsers);
        assertTrue(allUsers.size() >= 3);

        log.info("查询到 {} 个用户", allUsers.size());
        for (User user : allUsers) {
            log.info("用户：{}", user);
            // 验证所有字段都被正确解密
            assertNotNull(user.getPhone());
            assertNotNull(user.getEmail());
            assertNotNull(user.getIdCard());
        }

        log.info("✅ 列表查询测试通过");
    }

    @Test
    public void testNullValueHandling() {
        log.info("=== 测试空值处理 ===");

        // 创建包含空值的用户
        User userWithNulls = new User();
        userWithNulls.setUsername("nulltest");
        userWithNulls.setPassword("password");
        userWithNulls.setPhone(null);        // 空值
        userWithNulls.setEmail("");          // 空字符串
        userWithNulls.setIdCard(null);       // 空值
        userWithNulls.setAge(30);
        userWithNulls.setCreateTime(new Date());
        userWithNulls.setUpdateTime(new Date());

        // 插入用户
        int result = userMapper.insertUser(userWithNulls);
        assertEquals(1, result);

        // 查询用户
        User foundUser = userMapper.selectById(userWithNulls.getId());
        assertNotNull(foundUser);
        assertNull(foundUser.getPhone());
        assertEquals("", foundUser.getEmail());
        assertNull(foundUser.getIdCard());

        log.info("空值处理结果：{}", foundUser);
        log.info("✅ 空值处理测试通过");
    }

    @Test
    public void testPerformance() {
        log.info("=== 测试性能 ===");

        long startTime = System.currentTimeMillis();

        // 批量插入用户测试性能
        for (int i = 1; i <= 100; i++) {
            User user = new User();
            user.setUsername("perfuser" + i);
            user.setPassword("password" + i);
            user.setPhone("138001380" + String.format("%02d", i));
            user.setEmail("perfuser" + i + "@example.com");
            user.setIdCard("110101199001011" + String.format("%03d", i));
            user.setAge(20 + (i % 50));
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            userMapper.insertUser(user);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("插入100个用户耗时：{} ms", duration);
        log.info("平均每个用户耗时：{} ms", duration / 100.0);

        // 查询性能测试
        startTime = System.currentTimeMillis();
        List<User> allUsers = userMapper.selectAllUsers();
        endTime = System.currentTimeMillis();

        log.info("查询{}个用户耗时：{} ms", allUsers.size(), endTime - startTime);
        log.info("✅ 性能测试完成");
    }
}