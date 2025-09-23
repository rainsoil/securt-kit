package com.chu7.securtkit.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chu7.securtkit.test.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表Mapper
 * 
 * @author chu7
 * @date 2025/9/23
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
