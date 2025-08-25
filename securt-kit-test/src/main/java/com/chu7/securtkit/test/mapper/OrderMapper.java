package com.chu7.securtkit.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chu7.securtkit.test.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单Mapper接口
 *
 * @author chu7
 * @date 2025/8/15
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
    
    /**
     * 根据客户手机号查询订单
     */
    @Select("SELECT * FROM orders WHERE customer_phone = #{customerPhone}")
    List<OrderEntity> selectByCustomerPhone(@Param("customerPhone") String customerPhone);
    
    /**
     * 根据客户邮箱查询订单
     */
    @Select("SELECT * FROM orders WHERE customer_email = #{customerEmail}")
    List<OrderEntity> selectByCustomerEmail(@Param("customerEmail") String customerEmail);
    
    /**
     * 根据客户姓名查询订单
     */
    @Select("SELECT * FROM orders WHERE customer_name = #{customerName}")
    List<OrderEntity> selectByCustomerName(@Param("customerName") String customerName);
    
    /**
     * 根据用户ID查询订单
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId}")
    List<OrderEntity> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据客户手机号和邮箱查询订单
     */
    @Select("SELECT * FROM orders WHERE customer_phone = #{customerPhone} AND customer_email = #{customerEmail}")
    List<OrderEntity> selectByCustomerPhoneAndEmail(@Param("customerPhone") String customerPhone, @Param("customerEmail") String customerEmail);
} 