package com.chu7.securtkit.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.DefaultPoJoFieldEncryptorPattern;
import com.chu7.securtkit.encryptor.pojo.Md5PoJoFieldEncryptorStrategy;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体类 - 用于测试加解密功能
 * 
 * @author chu7
 * @date 2024/7/9 14:06
 */
@Data
@TableName("test_user")
public class User {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名 - 不加密
     */
    @TableField("username")
    private String username;

    /**
     * 密码 - 使用MD5哈希（单向加密）
     */
    @FieldEncryptor(Md5PoJoFieldEncryptorStrategy.class)
    @TableField("password")
    private String password;

    /**
     * 手机号 - 使用DES加密
     */
    @FieldEncryptor(DefaultPoJoFieldEncryptorPattern.class)
    @TableField("phone")
    private String phone;

    /**
     * 邮箱 - 使用AES加密
     */
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    @TableField("email")
    private String email;

    /**
     * 身份证号 - 使用Base64编码
     */
    @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
    @TableField("id_card")
    private String idCard;

    /**
     * 年龄 - 不加密
     */
    @TableField("age")
    private Integer age;

    /**
     * 创建时间 - 不加密
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间 - 不加密
     */
    @TableField("update_time")
    private Date updateTime;
}