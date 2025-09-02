package com.chu7.securtkit.sql;

import java.util.List;

/**
 * 字段处理器接口，定义字段处理的标准方法
 *
 * @author security-kit
 */
public interface FieldProcessor {
    
    /**
     * 处理字段列表
     *
     * @param fieldContexts 需要处理的字段上下文列表
     * @return 处理结果
     */
    ProcessResult process(List<FieldContext> fieldContexts);
    
    /**
     * 处理单个字段
     *
     * @param fieldContext 字段上下文
     * @return 处理结果
     */
    ProcessResult process(FieldContext fieldContext);
    
    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    String getProcessorName();
    
    /**
     * 判断是否支持当前字段
     *
     * @param fieldContext 字段上下文
     * @return 是否支持
     */
    default boolean supports(FieldContext fieldContext) {
        return true;
    }
} 