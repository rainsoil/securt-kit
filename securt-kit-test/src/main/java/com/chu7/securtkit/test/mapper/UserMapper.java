package com.chu7.securtkit.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chu7.securtkit.test.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM test_user WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM test_user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    /**
     * 根据身份证号查询用户
     */
    @Select("SELECT * FROM test_user WHERE id_card = #{idCard}")
    User selectByIdCard(@Param("idCard") String idCard);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM test_user ORDER BY id")
    List<User> selectAllUsers();

    /**
     * 插入用户
     */
    @Insert("INSERT INTO test_user(username, password, phone, email, id_card, age, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{phone}, #{email}, #{idCard}, #{age}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE test_user SET username = #{username}, password = #{password}, phone = #{phone}, " +
            "email = #{email}, id_card = #{idCard}, age = #{age}, update_time = #{updateTime} WHERE id = #{id}")
    int updateUser(User user);

    /**
     * 根据多个条件查询用户
     */
    @Select("SELECT * FROM test_user WHERE phone = #{phone} AND email = #{email}")
    User selectByPhoneAndEmail(@Param("phone") String phone, @Param("email") String email);
}