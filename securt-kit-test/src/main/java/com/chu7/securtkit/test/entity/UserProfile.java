package com.chu7.securtkit.test.entity;

import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户详情表实体
 * 
 * @author chu7
 * @date 2025/9/23
 */
@Data
@TableName("user_profile")
public class UserProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String realName;
    
    @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
    private String idCard;
    
    private String gender;
    
    private String occupation;
    
    private String address;
    
    private String remark;
}
