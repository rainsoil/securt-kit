package com.chu7.securtkit.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chu7.securtkit.encrypt.annotation.EncryptField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 *
 * @author chu7
 * @date 2025/8/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("orders")
public class OrderEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    @EncryptField
    private String customerName;
    
    @EncryptField
    private String customerPhone;
    
    @EncryptField
    private String customerEmail;
    
    @EncryptField
    private String deliveryAddress;
    
    private BigDecimal amount;
    
    private String status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 