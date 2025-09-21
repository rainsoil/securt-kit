package com.chu7.securtkit.test.controller;

import com.chu7.securtkit.test.entity.User;
import com.chu7.securtkit.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器 - 用于手动测试API
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        log.info("创建用户请求：{}", request);
        return userService.createUser(
            request.getUsername(),
            request.getPassword(),
            request.getPhone(),
            request.getEmail(),
            request.getIdCard(),
            request.getAge()
        );
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("查询用户：{}", id);
        return userService.findByPhone(""); // 这里需要根据实际需求修改
    }

    /**
     * 根据手机号查询用户
     */
    @GetMapping("/phone/{phone}")
    public User getUserByPhone(@PathVariable String phone) {
        log.info("根据手机号查询用户：{}", phone);
        return userService.findByPhone(phone);
    }

    /**
     * 根据邮箱查询用户
     */
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        log.info("根据邮箱查询用户：{}", email);
        return userService.findByEmail(email);
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    public List<User> getAllUsers() {
        log.info("查询所有用户");
        return userService.getAllUsers();
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        log.info("更新用户：{}, 请求：{}", id, request);
        return userService.updateUser(id, request.getPhone(), request.getEmail(), request.getIdCard());
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        log.info("删除用户：{}", id);
        return userService.deleteUser(id);
    }

    /**
     * 创建用户请求对象
     */
    public static class CreateUserRequest {
        private String username;
        private String password;
        private String phone;
        private String email;
        private String idCard;
        private Integer age;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getIdCard() { return idCard; }
        public void setIdCard(String idCard) { this.idCard = idCard; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        @Override
        public String toString() {
            return "CreateUserRequest{" +
                    "username='" + username + '\'' +
                    ", password='[PROTECTED]'" +
                    ", phone='" + phone + '\'' +
                    ", email='" + email + '\'' +
                    ", idCard='" + idCard + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    /**
     * 更新用户请求对象
     */
    public static class UpdateUserRequest {
        private String phone;
        private String email;
        private String idCard;

        // Getters and Setters
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getIdCard() { return idCard; }
        public void setIdCard(String idCard) { this.idCard = idCard; }

        @Override
        public String toString() {
            return "UpdateUserRequest{" +
                    "phone='" + phone + '\'' +
                    ", email='" + email + '\'' +
                    ", idCard='" + idCard + '\'' +
                    '}';
        }
    }
}