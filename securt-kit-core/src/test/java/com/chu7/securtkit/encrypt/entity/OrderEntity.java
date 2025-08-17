package com.chu7.securtkit.encrypt.entity;

import com.chu7.securtkit.encrypt.annotation.EncryptField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单实体类（测试用）
 *
 * @author chu7
 * @date 2025/8/15
 */
@Data
public class OrderEntity {
    
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
    
    private String createTime;
    
    private String updateTime;
    
    public OrderEntity() {}
    
    public OrderEntity(Long id, String orderNo, Long userId, String customerName, 
                      String customerPhone, String customerEmail, String deliveryAddress, 
                      BigDecimal amount, String status) {
        this.id = id;
        this.orderNo = orderNo;
        this.userId = userId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.amount = amount;
        this.status = status;
    }
} 