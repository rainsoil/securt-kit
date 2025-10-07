package com.chu7.securtkit.visitor.fieldparse;

import com.chu7.securtkit.dto.BaseFieldParseTable;
import com.chu7.securtkit.dto.FieldInfoDto;
import com.chu7.securtkit.util.CollectionUtils;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.expression.Alias;

import java.util.*;

/**
 * 解析from 后面的表结构信息
 *
 * @author liutangqi
 * @date 2024/3/4 14:26
 */
public class FieldParseParseTableFromItemVisitor extends BaseFieldParseTable implements FromItemVisitor {

    private FieldParseParseTableFromItemVisitor(int layer, Map<String, Map<String, Set<FieldInfoDto>>> layerSelectTableFieldMap, Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        super(layer, layerSelectTableFieldMap, layerFieldTableMap);
    }

    public static FieldParseParseTableFromItemVisitor newInstanceFirstLayer() {
        return new FieldParseParseTableFromItemVisitor(1, new HashMap<>(), new HashMap<>());
    }

    public static FieldParseParseTableFromItemVisitor newInstanceCurLayer(BaseFieldParseTable baseFieldParseTable) {
        return new FieldParseParseTableFromItemVisitor(
                baseFieldParseTable.getLayer(),
                baseFieldParseTable.getLayerSelectTableFieldMap(),
                baseFieldParseTable.getLayerFieldTableMap()
        );
    }

    @Override
    public void visit(Table table) {
        //解析表结构信息
        String tableName = table.getName();
        String tableAlias = Optional.ofNullable(table.getAlias()).map(Alias::getName).orElse(tableName);
        
        // 从 TableCache 中获取表的字段信息
        Set<FieldInfoDto> fieldInfoDtos = new HashSet<>();
        if (com.chu7.securtkit.cache.TableCache.getFieldEncryptTable().contains(tableName.toLowerCase())) {
            // 获取表的所有字段信息
            Map<String, com.chu7.securtkit.annotation.FieldEncryptor> tableFieldEncryptInfo = 
                com.chu7.securtkit.cache.TableCache.getTableFieldEncryptInfo(tableName.toLowerCase());
            
            if (tableFieldEncryptInfo != null) {
                for (Map.Entry<String, com.chu7.securtkit.annotation.FieldEncryptor> entry : tableFieldEncryptInfo.entrySet()) {
                    String columnName = entry.getKey();
                    FieldInfoDto fieldInfo = new FieldInfoDto(
                        columnName,  // columnName
                        columnName,  // sourceColumn
                        tableName.toLowerCase(),  // sourceTableName
                        true  // fromSourceTable
                    );
                    fieldInfoDtos.add(fieldInfo);
                }
            }
        }
        
        // 将表字段信息存储到 layerFieldTableMap 中
        Map<String, Set<FieldInfoDto>> tableFieldMap = this.getLayerFieldTableMap().computeIfAbsent(
            String.valueOf(this.getLayer()), k -> new HashMap<>()
        );
        tableFieldMap.put(tableAlias.toLowerCase(), fieldInfoDtos);
    }

    @Override
    public void visit(ParenthesedSelect subSelect) {
        // 处理子查询
        FieldParseParseTableSelectVisitor selectVisitor = FieldParseParseTableSelectVisitor.newInstanceNextLayer(this);
        subSelect.getSelect().accept(selectVisitor);
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        // 处理横向子查询
    }

    @Override
    public void visit(TableFunction tableFunction) {
        // 处理表函数
    }

    @Override
    public void visit(ParenthesedFromItem aThis) {
        // 处理括号包围的from项
    }
}
