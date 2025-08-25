package com.chu7.securtkit.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chu7.securtkit.test.entity.SystemConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统配置Mapper接口
 *
 * @author chu7
 * @date 2025/8/15
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfigEntity> {
    
    /**
     * 根据配置键查询配置
     */
    @Select("SELECT * FROM system_config WHERE config_key = #{configKey}")
    SystemConfigEntity selectByConfigKey(@Param("configKey") String configKey);
} 