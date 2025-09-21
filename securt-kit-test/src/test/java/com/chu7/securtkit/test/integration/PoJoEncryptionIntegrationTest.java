package com.chu7.securtkit.test.integration;

import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.mapper.UserMapper;
import com.chu7.securtkit.test.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * POJO模式加密集成测试
 * 测试完整的加密解密流程
 *
 * @author chu7
 * @date 2024/7/9 14:06
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PoJoEncryptionIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 测试用户插入和查询的加密解密流程
     */
    @Test
    public void testUserInsertAndSelect() {
        // 准备测试数据
        User user = new User();
        user.setUsername("张三");
        user.setPhone("13800138000");
        user.setEmail("zhangsan@example.com");
        user.setIdCard("110101199001011234");

        // 插入用户（应该自动加密敏感字段）
        int insertResult = userMapper.insert(user);
        assertEquals(1, insertResult);
        assertNotNull(user.getId());

        // 查询用户（应该自动解密敏感字段）
        User queriedUser = userMapper.selectById(user.getId());
        assertNotNull(queriedUser);
        assertEquals("张三", queriedUser.getUsername());
        assertEquals("13800138000", queriedUser.getPhone());
        assertEquals("zhangsan@example.com", queriedUser.getEmail());
        assertEquals("110101199001011234", queriedUser.getIdCard());
    }

    /**
     * 测试批量插入和查询
     */
    @Test
    public void testBatchInsertAndSelect() {
        // 准备测试数据
        User user1 = new User();
        user1.setUsername("李四");
        user1.setPhone("13800138001");
        user1.setEmail("lisi@example.com");
        user1.setIdCard("110101199001011235");

        User user2 = new User();
        user2.setUsername("王五");
        user2.setPhone("13800138002");
        user2.setEmail("wangwu@example.com");
        user2.setIdCard("110101199001011236");

        // 插入用户
        int result1 = userMapper.insert(user1);
        int result2 = userMapper.insert(user2);
        assertEquals(1, result1);
        assertEquals(1, result2);

        // 查询所有用户
        List<User> users = userMapper.selectList(null);
        assertTrue(users.size() >= 2);

        // 验证数据是否正确解密
        User foundUser1 = users.stream()
                .filter(u -> "李四".equals(u.getUsername()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundUser1);
        assertEquals("13800138001", foundUser1.getPhone());
        assertEquals("lisi@example.com", foundUser1.getEmail());
        assertEquals("110101199001011235", foundUser1.getIdCard());

        User foundUser2 = users.stream()
                .filter(u -> "王五".equals(u.getUsername()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundUser2);
        assertEquals("13800138002", foundUser2.getPhone());
        assertEquals("wangwu@example.com", foundUser2.getEmail());
        assertEquals("110101199001011236", foundUser2.getIdCard());
    }

    /**
     * 测试条件查询
     */
    @Test
    public void testConditionalQuery() {
        // 准备测试数据
        User user = new User();
        user.setUsername("赵六");
        user.setPhone("13800138003");
        user.setEmail("zhaoliu@example.com");
        user.setIdCard("110101199001011237");

        // 插入用户
        userMapper.insert(user);

        // 根据手机号查询
        User queriedByPhone = userMapper.selectByPhone("13800138003");
        assertNotNull(queriedByPhone);
        assertEquals("赵六", queriedByPhone.getUsername());
        assertEquals("13800138003", queriedByPhone.getPhone());

        // 根据邮箱查询
        User queriedByEmail = userMapper.selectByEmail("zhaoliu@example.com");
        assertNotNull(queriedByEmail);
        assertEquals("赵六", queriedByEmail.getUsername());
        assertEquals("zhaoliu@example.com", queriedByEmail.getEmail());
    }

    /**
     * 测试更新操作
     */
    @Test
    public void testUpdate() {
        // 准备测试数据
        User user = new User();
        user.setUsername("孙七");
        user.setPhone("13800138004");
        user.setEmail("sunqi@example.com");
        user.setIdCard("110101199001011238");

        // 插入用户
        userMapper.insert(user);

        // 更新用户信息
        user.setPhone("13800138005");
        user.setEmail("sunqi_new@example.com");
        int updateResult = userMapper.updateById(user);
        assertEquals(1, updateResult);

        // 查询验证更新结果
        User updatedUser = userMapper.selectById(user.getId());
        assertNotNull(updatedUser);
        assertEquals("孙七", updatedUser.getUsername());
        assertEquals("13800138005", updatedUser.getPhone());
        assertEquals("sunqi_new@example.com", updatedUser.getEmail());
        assertEquals("110101199001011238", updatedUser.getIdCard());
    }

    /**
     * 测试删除操作
     */
    @Test
    public void testDelete() {
        // 准备测试数据
        User user = new User();
        user.setUsername("周八");
        user.setPhone("13800138006");
        user.setEmail("zhouba@example.com");
        user.setIdCard("110101199001011239");

        // 插入用户
        userMapper.insert(user);
        Long userId = user.getId();

        // 删除用户
        int deleteResult = userMapper.deleteById(userId);
        assertEquals(1, deleteResult);

        // 验证删除结果
        User deletedUser = userMapper.selectById(userId);
        assertNull(deletedUser);
    }

    /**
     * 测试Service层方法
     */
    @Test
    public void testServiceMethods() {
        // 准备测试数据
        User user = new User();
        user.setUsername("吴九");
        user.setPhone("13800138007");
        user.setEmail("wujiu@example.com");
        user.setIdCard("110101199001011240");

        // 使用Service保存用户
        User savedUser = userService.createUser(user.getUsername(), "123456", user.getPhone(), user.getEmail(), user.getIdCard(), 25);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());

        // 使用Service查询用户
        User foundUser = userMapper.selectById(savedUser.getId());
        assertNotNull(foundUser);
        assertEquals("吴九", foundUser.getUsername());
        assertEquals("13800138007", foundUser.getPhone());
        assertEquals("wujiu@example.com", foundUser.getEmail());
        assertEquals("110101199001011240", foundUser.getIdCard());

        // 使用Service查询所有用户
        List<User> allUsers = userService.getAllUsers();
        assertTrue(allUsers.size() >= 1);

        // 验证查询结果中的用户数据是否正确解密
        User serviceUser = allUsers.stream()
                .filter(u -> "吴九".equals(u.getUsername()))
                .findFirst()
                .orElse(null);
        assertNotNull(serviceUser);
        assertEquals("13800138007", serviceUser.getPhone());
        assertEquals("wujiu@example.com", serviceUser.getEmail());
        assertEquals("110101199001011240", serviceUser.getIdCard());
    }

    /**
     * 测试空值和特殊字符处理
     */
    @Test
    public void testNullAndSpecialCharacters() {
        // 准备测试数据
        User user = new User();
        user.setUsername("郑十");
        user.setPhone("13800138008");
        user.setEmail("zhengshi@example.com");
        user.setIdCard("110101199001011241");

        // 插入用户
        userMapper.insert(user);

        // 查询用户
        User queriedUser = userMapper.selectById(user.getId());
        assertNotNull(queriedUser);
        assertEquals("郑十", queriedUser.getUsername());
        assertEquals("13800138008", queriedUser.getPhone());
        assertEquals("zhengshi@example.com", queriedUser.getEmail());
        assertEquals("110101199001011241", queriedUser.getIdCard());

        // 测试更新为空值
        user.setPhone(null);
        user.setEmail("");
        userMapper.updateById(user);

        // 查询验证
        User updatedUser = userMapper.selectById(user.getId());
        assertNotNull(updatedUser);
        assertEquals("郑十", updatedUser.getUsername());
        assertNull(updatedUser.getPhone());
        assertEquals("", updatedUser.getEmail());
        assertEquals("110101199001011241", updatedUser.getIdCard());
    }
}
