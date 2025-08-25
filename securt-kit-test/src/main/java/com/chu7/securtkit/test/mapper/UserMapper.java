package com.chu7.securtkit.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chu7.securtkit.test.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author chu7
 * @date 2025/8/15
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    
    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    UserEntity selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    UserEntity selectByEmail(@Param("email") String email);
    
    /**
     * 根据身份证号查询用户
     */
    @Select("SELECT * FROM user WHERE id_card = #{idCard}")
    UserEntity selectByIdCard(@Param("idCard") String idCard);
    
    /**
     * 根据年龄查询用户
     */
    @Select("SELECT * FROM user WHERE age = #{age}")
    List<UserEntity> selectByAge(@Param("age") String age);
    
    /**
     * 根据手机号和邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND email = #{email}")
    UserEntity selectByPhoneAndEmail(@Param("phone") String phone, @Param("email") String email);
    
    /**
     * 根据手机号模糊查询用户
     */
    @Select("SELECT * FROM user WHERE phone LIKE #{phone}")
    List<UserEntity> selectByPhoneLike(@Param("phone") String phone);
} 