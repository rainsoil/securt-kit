package com.chu7.securtkit.visitor;

import com.chu7.securtkit.constants.NumberConstant;
import com.chu7.securtkit.dto.FieldInfoDto;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 简化的字段解析访问者
 * 避免复杂的接口继承，专注于核心功能
 *
 * @author chu7
 * @date 2024/7/6 13:22
 */
@Slf4j
public class SimpleFieldParseVisitor {
    
    /**
     * 当前层级
     */
    private String layer;
    
    /**
     * 层级字段表映射
     */
    private Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap = new HashMap<>();
    
    /**
     * 层级SELECT字段表映射
     */
    private Map<String, Map<String, Set<FieldInfoDto>>> layerSelectTableFieldMap = new HashMap<>();

    private SimpleFieldParseVisitor(String layer, Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        this.layer = layer;
        if (layerFieldTableMap != null) {
            this.layerFieldTableMap = layerFieldTableMap;
        }
    }

    /**
     * 创建第一层实例
     */
    public static SimpleFieldParseVisitor newInstanceFirstLayer() {
        return new SimpleFieldParseVisitor(String.valueOf(NumberConstant.ONE), null);
    }

    /**
     * 创建第一层实例（带初始表映射）
     */
    public static SimpleFieldParseVisitor newInstanceFirstLayer(String layer, Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        return new SimpleFieldParseVisitor(String.valueOf(NumberConstant.ONE), layerFieldTableMap);
    }

    public String getLayer() {
        return layer;
    }

    public Map<String, Map<String, Set<FieldInfoDto>>> getLayerFieldTableMap() {
        return layerFieldTableMap;
    }

    public Map<String, Map<String, Set<FieldInfoDto>>> getLayerSelectTableFieldMap() {
        return layerSelectTableFieldMap;
    }

    /**
     * 访问SELECT语句
     */
    public void accept(Select select) {
        try {
            if (select.getSelectBody() instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
                visitPlainSelect(plainSelect);
            }
            // TODO: 处理其他类型的SELECT
        } catch (Exception e) {
            log.error("【securt-kit】解析SELECT失败", e);
        }
    }

    /**
     * 访问PlainSelect
     */
    private void visitPlainSelect(PlainSelect plainSelect) {
        try {
            // 解析FROM子句
            parseFromItem(plainSelect.getFromItem());
            
            // 解析JOIN子句
            List<Join> joins = plainSelect.getJoins();
            if (joins != null) {
                for (Join join : joins) {
                    parseFromItem(join.getRightItem());
                }
            }
            
            // 简化的SELECT字段处理
            parseSelectItemsSimple(plainSelect);
            
        } catch (Exception e) {
            log.error("【securt-kit】解析PlainSelect失败", e);
        }
    }

    /**
     * 解析FROM项
     */
    private void parseFromItem(FromItem fromItem) {
        if (fromItem == null) {
            return;
        }
        
        // 简化实现，主要处理表
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            String tableName = table.getName().toLowerCase();
            
            // 创建字段信息（简化版本）
            Set<FieldInfoDto> fieldInfos = new HashSet<>();
            // TODO: 从TableCache获取表的所有字段信息
            
            Map<String, Set<FieldInfoDto>> tableFieldMap = layerFieldTableMap.computeIfAbsent(layer, k -> new HashMap<>());
            tableFieldMap.put(tableName, fieldInfos);
        }
        // TODO: 处理子查询等其他FROM项类型
    }

    /**
     * 简化的SELECT项解析
     */
    private void parseSelectItemsSimple(PlainSelect plainSelect) {
        try {
            Set<FieldInfoDto> selectFields = new HashSet<>();
            
            // 简化处理：创建一个默认的字段信息
            FieldInfoDto fieldInfo = FieldInfoDto.builder()
                    .columnName("*")
                    .sourceColumn("*")
                    .sourceTableName("unknown")
                    .fromSourceTable(true)
                    .build();
            selectFields.add(fieldInfo);
            
            Map<String, Set<FieldInfoDto>> selectTableFieldMap = layerSelectTableFieldMap.computeIfAbsent(layer, k -> new HashMap<>());
            selectTableFieldMap.put("select_fields", selectFields);
        } catch (Exception e) {
            log.error("【securt-kit】解析SELECT项失败", e);
        }
    }
}