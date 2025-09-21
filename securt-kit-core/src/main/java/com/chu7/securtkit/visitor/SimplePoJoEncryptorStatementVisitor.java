package com.chu7.securtkit.visitor;

import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.dto.ColumnTableDto;
import com.chu7.securtkit.dto.FieldEncryptorInfoDto;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.*;

/**
 * 简化的POJO加解密SQL语句访问者
 * 专注于核心功能，避免复杂的继承关系
 *
 * @author chu7
 * @date 2024/7/6 13:22
 */
@Slf4j
public class SimplePoJoEncryptorStatementVisitor {
    
    /**
     * 当前sql涉及到的字段以及字段的所属表结构信息
     */
    private List<FieldEncryptorInfoDto> fieldEncryptorInfos = new ArrayList<>();

    /**
     * 当前占位符对应的数据库表，字段信息
     * key: 占位符 SECURT_KIT_PLACEHOLDER_ + 0开始的自增序号
     * value: 这个字段所属的表字段
     */
    private Map<String, ColumnTableDto> placeholderColumnTableMap = new HashMap<>();

    public List<FieldEncryptorInfoDto> getFieldEncryptorInfos() {
        return fieldEncryptorInfos;
    }

    public Map<String, ColumnTableDto> getPlaceholderColumnTableMap() {
        return placeholderColumnTableMap;
    }

    /**
     * 处理SQL语句
     */
    public void process(net.sf.jsqlparser.statement.Statement statement) {
        try {
            if (statement instanceof Select) {
                processSelect((Select) statement);
            } else if (statement instanceof Insert) {
                processInsert((Insert) statement);
            } else {
                log.debug("【securt-kit】暂不支持的SQL类型: {}", statement.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("【securt-kit】处理SQL语句失败", e);
        }
    }

    /**
     * 处理INSERT语句
     */
    private void processInsert(Insert insert) {
        try {
            log.debug("【securt-kit】处理INSERT语句");
            
            // 获取表名
            Table table = insert.getTable();
            String tableName = table.getName().toLowerCase();
            log.debug("【securt-kit】INSERT表名: {}", tableName);
            
            // 获取列名列表
            List<Column> columns = insert.getColumns();
            if (columns == null || columns.isEmpty()) {
                log.debug("【securt-kit】INSERT语句没有指定列名");
                return;
            }
            
            log.debug("【securt-kit】INSERT列名列表: {}", columns);
            
            // 简化处理：直接遍历列名，为每个列创建占位符映射
            // 这样可以避免复杂的JSqlParser API使用
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                String columnName = column.getColumnName().toLowerCase();
                
                // 为每个列创建一个占位符
                String placeholder = "SECURT_KIT_PLACEHOLDER_" + i;
                ColumnTableDto columnTableDto = new ColumnTableDto();
                columnTableDto = ColumnTableDto.builder()
                        .sourceTableName(tableName)
                        .sourceColumn(columnName)
                        .fromSourceTable(true)
                        .build();
                
                placeholderColumnTableMap.put(placeholder, columnTableDto);
                log.debug("【securt-kit】建立占位符映射: {} -> {}", placeholder, columnTableDto);
            }
            
            log.debug("【securt-kit】INSERT语句处理完成，占位符映射: {}", placeholderColumnTableMap);
        } catch (Exception e) {
            log.error("【securt-kit】处理INSERT语句失败", e);
        }
    }

    /**
     * 处理SELECT语句
     */
    private void processSelect(Select select) {
        try {
            log.debug("【securt-kit】处理SELECT语句");
            
            // 简化实现：创建一个默认的字段加密信息
            FieldEncryptorInfoDto fieldInfo = FieldEncryptorInfoDto.builder()
                    .columnName("*")
                    .sourceColumn("*")
                    .sourceTableName("unknown")
                    .fieldEncryptor(null)
                    .build();
            this.fieldEncryptorInfos.add(fieldInfo);
            
            // TODO: 实现完整的SELECT处理逻辑
            // 1. 解析FROM子句获取表信息
            // 2. 解析SELECT字段
            // 3. 解析WHERE条件中的占位符
            
        } catch (Exception e) {
            log.error("【securt-kit】处理SELECT语句失败", e);
        }
    }
}