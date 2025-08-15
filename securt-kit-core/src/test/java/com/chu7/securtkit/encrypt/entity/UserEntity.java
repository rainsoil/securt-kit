package com.chu7.securtkit.encrypt.entity;

import com.chu7.securtkit.encrypt.annotation.EncryptField;
import lombok.Data;

/**
 * 用户实体类（测试用）
 *
 * @author chu7
 * @date 2025/8/15
 */
@Data
public class UserEntity {
    
    private Long id;
    
    private String username;
    
    @EncryptField
    private String phone;
    
    @EncryptField
    private String email;
    
    @EncryptField(algorithm = "AES")
    private String idCard;
    
    private String address;
    
    public UserEntity() {}
    
    public UserEntity(Long id, String username, String phone, String email, String idCard, String address) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.idCard = idCard;
        this.address = address;
    }
}
