package com.chu7.securtkit.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chu7.securtkit.encrypt.annotation.EncryptField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author chu7
 * @date 2025/8/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class UserEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    @EncryptField
    private String phone;
    
    @EncryptField
    private String email;
    
    @EncryptField
    private String idCard;
    
    @EncryptField
    private String age;
    
    private String address;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 