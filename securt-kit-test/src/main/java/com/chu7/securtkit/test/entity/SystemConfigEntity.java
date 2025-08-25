package com.chu7.securtkit.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置实体类（不加密）
 *
 * @author chu7
 * @date 2025/8/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_config")
public class SystemConfigEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String configKey;
    
    private String configValue;
    
    private String description;
    
    private LocalDateTime createTime;
} 