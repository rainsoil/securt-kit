package com.chu7.securtkit.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chu7.securtkit.test.entity.UserEntity;
import com.chu7.securtkit.test.mapper.UserMapper;
import com.chu7.securtkit.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户Service实现类
 *
 * @author chu7
 * @date 2025/8/15
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    
    @Override
    public UserEntity getByPhone(String phone) {
        log.info("根据手机号查询用户: {}", phone);
        return baseMapper.selectByPhone(phone);
    }
    
    @Override
    public UserEntity getByEmail(String email) {
        log.info("根据邮箱查询用户: {}", email);
        return baseMapper.selectByEmail(email);
    }
    
    @Override
    public UserEntity getByIdCard(String idCard) {
        log.info("根据身份证号查询用户: {}", idCard);
        return baseMapper.selectByIdCard(idCard);
    }
    
    @Override
    public List<UserEntity> getByAge(String age) {
        log.info("根据年龄查询用户: {}", age);
        return baseMapper.selectByAge(age);
    }
    
    @Override
    public UserEntity getByPhoneAndEmail(String phone, String email) {
        log.info("根据手机号和邮箱查询用户: phone={}, email={}", phone, email);
        return baseMapper.selectByPhoneAndEmail(phone, email);
    }
    
    @Override
    public List<UserEntity> getByPhoneLike(String phone) {
        log.info("根据手机号模糊查询用户: {}", phone);
        return baseMapper.selectByPhoneLike("%" + phone + "%");
    }
    
    @Override
    public boolean createUser(UserEntity user) {
        log.info("创建用户: {}", user);
        return save(user);
    }
    
    @Override
    public boolean updateUser(UserEntity user) {
        log.info("更新用户: {}", user);
        return updateById(user);
    }
    
    @Override
    public boolean deleteUser(Long id) {
        log.info("删除用户: {}", id);
        return removeById(id);
    }
} 