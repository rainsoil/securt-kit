package com.chu7.securtkit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 列表信息 DTO
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnTableDto {
    
    /**
     * 字段所属的表的别名(from 后面接的表的别名)
     */
    private String tableAliasName;
    
    /**
     * 字段所属表的真实表的名字
     */
    private String sourceTableName;
    
    /**
     * 字段所属真实字段名
     */
    private String sourceColumn;
    
    /**
     * 该字段是否直接从真实的表中关联获取的
     * 栗子：select user_name   from tb_user    user_name 这个字段真实属于tb_user的，这个值就是ture
     * select a.userName from (select user_name   from tb_user )a    userName这个字段是来自于表a 的，表a不是真实的数据来源表，所以这个值是false
     **/
    @Builder.Default
    private boolean fromSourceTable = false;
}