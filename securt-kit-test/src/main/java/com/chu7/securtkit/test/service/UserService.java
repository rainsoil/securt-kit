package com.chu7.securtkit.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chu7.securtkit.test.entity.UserEntity;

import java.util.List;

/**
 * 用户Service接口
 *
 * @author chu7
 * @date 2025/8/15
 */
public interface UserService extends IService<UserEntity> {
    
    /**
     * 根据手机号查询用户
     */
    UserEntity getByPhone(String phone);
    
    /**
     * 根据邮箱查询用户
     */
    UserEntity getByEmail(String email);
    
    /**
     * 根据身份证号查询用户
     */
    UserEntity getByIdCard(String idCard);
    
    /**
     * 根据年龄查询用户列表
     */
    List<UserEntity> getByAge(String age);
    
    /**
     * 根据手机号和邮箱查询用户
     */
    UserEntity getByPhoneAndEmail(String phone, String email);
    
    /**
     * 根据手机号模糊查询用户
     */
    List<UserEntity> getByPhoneLike(String phone);
    
    /**
     * 创建用户
     */
    boolean createUser(UserEntity user);
    
    /**
     * 更新用户
     */
    boolean updateUser(UserEntity user);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
} 