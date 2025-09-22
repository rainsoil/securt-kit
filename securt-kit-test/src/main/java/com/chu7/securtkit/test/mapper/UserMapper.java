package com.chu7.securtkit.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chu7.securtkit.test.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    // ========== 复杂SQL测试方法 ==========

    /**
     * 按年龄分组统计用户数量（带CASE WHEN）
     */
    @Select("SELECT " +
            "CASE " +
            "  WHEN age < 25 THEN '青年' " +
            "  WHEN age BETWEEN 25 AND 35 THEN '中年' " +
            "  ELSE '老年' " +
            "END as age_group, " +
            "COUNT(*) as user_count " +
            "FROM test_user " +
            "GROUP BY " +
            "CASE " +
            "  WHEN age < 25 THEN '青年' " +
            "  WHEN age BETWEEN 25 AND 35 THEN '中年' " +
            "  ELSE '老年' " +
            "END")
    List<Map<String, Object>> selectUsersByAgeGroup();

    /**
     * 查询用户统计信息（带聚合函数）
     */
    @Select("SELECT " +
            "COUNT(*) as total_users, " +
            "AVG(age) as avg_age, " +
            "MAX(age) as max_age, " +
            "MIN(age) as min_age, " +
            "COUNT(DISTINCT phone) as unique_phones " +
            "FROM test_user")
    Map<String, Object> selectUserStatistics();

    /**
     * 按年龄分组统计（带GROUP BY）
     */
    @Select("SELECT age, COUNT(*) as count " +
            "FROM test_user " +
            "GROUP BY age " +
            "ORDER BY age")
    List<Map<String, Object>> selectAgeStatistics();

    /**
     * 按年龄排序查询用户（带ORDER BY）
     */
    @Select("SELECT u.id, u.username, u.phone, u.email, u.id_card, u.age, u.create_time, u.update_time " +
            "FROM test_user u " +
            "ORDER BY u.age ASC, u.create_time DESC")
    List<User> selectUsersOrderByAge();

    /**
     * 分页查询用户（带LIMIT）
     */
    @Select("SELECT u.id, u.username, u.phone, u.email, u.id_card, u.age, u.create_time, u.update_time " +
            "FROM test_user u " +
            "ORDER BY u.id " +
            "LIMIT #{offset}, #{limit}")
    List<User> selectUsersWithLimit(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 复杂JOIN查询（模拟多表关联）
     */
    @Select("SELECT " +
            "u.id as user_id, " +
            "u.username as user_name, " +
            "u.phone as user_phone, " +
            "u.email as user_email, " +
            "u.id_card as user_id_card, " +
            "u.age as user_age, " +
            "u.create_time as user_create_time, " +
            "u.update_time as user_update_time " +
            "FROM test_user u " +
            "WHERE u.age > #{minAge} " +
            "ORDER BY u.age DESC")
    List<Map<String, Object>> selectUsersWithAlias(@Param("minAge") int minAge);

    /**
     * 复杂子查询更新（带子查询的UPDATE）
     */
    @Update("UPDATE test_user u1 " +
            "SET u1.phone = #{newPhone}, " +
            "    u1.email = #{newEmail}, " +
            "    u1.id_card = #{newIdCard}, " +
            "    u1.update_time = NOW() " +
            "WHERE u1.id = #{userId} " +
            "AND EXISTS (SELECT 1 FROM test_user u2 WHERE u2.id = u1.id AND u2.username = #{username})")
    int updateUserWithSubquery(@Param("userId") Long userId, 
                              @Param("username") String username,
                              @Param("newPhone") String newPhone, 
                              @Param("newEmail") String newEmail, 
                              @Param("newIdCard") String newIdCard);

    /**
     * 复杂条件查询（带多个条件）
     */
    @Select("SELECT u.* " +
            "FROM test_user u " +
            "WHERE u.age BETWEEN #{minAge} AND #{maxAge} " +
            "AND u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "AND u.create_time >= #{startDate} " +
            "ORDER BY u.age, u.create_time DESC")
    List<User> selectUsersWithComplexConditions(@Param("minAge") int minAge,
                                               @Param("maxAge") int maxAge,
                                               @Param("keyword") String keyword,
                                               @Param("startDate") String startDate);

    /**
     * 复杂聚合查询（带HAVING子句）
     */
    @Select("SELECT " +
            "SUBSTRING(phone, 1, 3) as phone_prefix, " +
            "COUNT(*) as user_count, " +
            "AVG(age) as avg_age " +
            "FROM test_user " +
            "GROUP BY SUBSTRING(phone, 1, 3) " +
            "HAVING COUNT(*) > #{minCount} " +
            "ORDER BY user_count DESC")
    List<Map<String, Object>> selectPhonePrefixStatistics(@Param("minCount") int minCount);
}