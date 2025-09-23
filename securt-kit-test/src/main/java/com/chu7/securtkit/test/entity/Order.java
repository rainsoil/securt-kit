package com.chu7.securtkit.test.entity;

import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.encryptor.pojo.AesPoJoFieldEncryptorStrategy;
import com.chu7.securtkit.encryptor.pojo.Base64PoJoFieldEncryptorStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表实体
 * 
 * @author chu7
 * @date 2025/9/23
 */
@Data
@TableName("user_order")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String orderNo;
    
    @FieldEncryptor(AesPoJoFieldEncryptorStrategy.class)
    private String customerName;
    
    @FieldEncryptor(Base64PoJoFieldEncryptorStrategy.class)
    private String customerPhone;
    
    private BigDecimal amount;
    
    private String status;
    
    private String productName;
    
    private Integer quantity;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
