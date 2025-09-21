package com.chu7.securtkit.test.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户服务类 - 演示在业务逻辑中使用加解密功能
 *
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建用户
     */
    public User createUser(String username, String password, String phone, String email, String idCard, Integer age) {
        log.info("创建用户：{}", username);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setEmail(email);
        user.setIdCard(idCard);
        user.setAge(age);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        int result = userMapper.insertUser(user);
        if (result > 0) {
            log.info("用户创建成功，ID：{}", user.getId());
            return user;
        } else {
            throw new RuntimeException("用户创建失败");
        }
    }

    /**
     * 根据手机号查询用户
     */
    public User findByPhone(String phone) {
        log.info("根据手机号查询用户：{}", phone);
        return userMapper.selectByPhone(phone);
    }

    /**
     * 根据邮箱查询用户
     */
    public User findByEmail(String email) {
        log.info("根据邮箱查询用户：{}", email);
        return userMapper.selectByEmail(email);
    }

    /**
     * 用户登录验证（模拟）
     */
    public boolean validateLogin(String phone, String password) {
        log.info("用户登录验证：{}", phone);

        User user = findByPhone(phone);
        if (user != null) {
            // 注意：由于密码使用MD5加密，这里需要对输入的密码也进行MD5加密后比较
            // 实际项目中建议使用更安全的密码验证方式
            log.info("找到用户，验证密码...");
            return true; // 简化实现
        }
        return false;
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long id, String phone, String email, String idCard) {
        log.info("更新用户信息，ID：{}", id);

        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPhone(phone);
            user.setEmail(email);
            user.setIdCard(idCard);
            user.setUpdateTime(new Date());

            int result = userMapper.updateUser(user);
            if (result > 0) {
                log.info("用户信息更新成功");
                return userMapper.selectById(id);
            }
        }
        throw new RuntimeException("用户更新失败");
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        log.info("查询所有用户");
        return userMapper.selectAllUsers();
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        log.info("删除用户，ID：{}", id);
        int result = userMapper.deleteById(id);
        return result > 0;
    }

    /**
     * 根据多条件查询用户
     */
    public User findByPhoneAndEmail(String phone, String email) {
        log.info("根据手机号和邮箱查询用户：{}, {}", phone, email);
        return userMapper.selectByPhoneAndEmail(phone, email);
    }
}