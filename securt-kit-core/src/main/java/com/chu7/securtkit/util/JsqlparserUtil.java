package com.chu7.securtkit.util;

import com.chu7.securtkit.annotation.FieldEncryptor;
import com.chu7.securtkit.cache.TableCache;
import com.chu7.securtkit.constants.FieldConstant;
import com.chu7.securtkit.dto.ColumnTableDto;
import com.chu7.securtkit.dto.FieldInfoDto;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import com.chu7.securtkit.visitor.PlaceholderExpressionVisitor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * JSQLParser 工具类
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
public class JsqlparserUtil {
    
    /**
     * 解析SQL语句
     */
    public static Statement parse(String sql) throws JSQLParserException {
        return CCJSqlParserUtil.parse(sql);
    }
    
    /**
     * 根据列表信息获取字段加密注解
     */
    public static FieldEncryptor parseFieldEncryptor(ColumnTableDto columnTableDto) {
        if (columnTableDto == null || StringUtils.isBlank(columnTableDto.getSourceTableName()) 
                || StringUtils.isBlank(columnTableDto.getSourceColumn())) {
            return null;
        }
        
        return TableCache.getFieldEncryptor(columnTableDto.getSourceTableName(), columnTableDto.getSourceColumn());
    }
    
    /**
     * 解析列信息
     */
    public static ColumnTableDto parseColumn(Column column, int layer, Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        //字段名
        String columName = column.getColumnName();
        //字段所属表 （只有select 别名.字段名 时这个才有值，其它的为null）
        Table table = column.getTable();

        //字段所属的表的别名(from 后面接的表的别名)
        AtomicReference<String> tableAliasName = new AtomicReference<>();
        //字段所属表的真实表的名字
        AtomicReference<String> sourceTableName = new AtomicReference<>();
        //字段所属真实字段名
        AtomicReference<String> sourceColumn = new AtomicReference<>();
        //字段所属表的真实名字 from 后面的表的名字 （tableAliasName的真实名字）
        AtomicBoolean fromSourceTable = new AtomicBoolean(false);


        //1.没有指定表名时，从当前层的表的所有字段里面找到这个名字的表( select 字段)
        if (table == null) {
            layerFieldTableMap.get(String.valueOf(layer)).entrySet().forEach(f -> {
                List<FieldInfoDto> matchFields = f.getValue().stream().filter(fi -> StringUtils.equalIgnoreFieldSymbol(fi.getColumnName(), columName)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(matchFields)) {
                    //当前层的所有字段里面叫这个的，正确sql语法中只会有一个，所以get(0)
                    FieldInfoDto matchField = matchFields.get(0);
                    sourceTableName.set(matchField.getSourceTableName());
                    sourceColumn.set(matchField.getSourceColumn());
                    fromSourceTable.set(matchField.isFromSourceTable());
                    tableAliasName.set(f.getKey());
                }
            });
        }

        //2.有指定表名时，从当前层的这张表的所有字段里面这个字段的信息 （select 别名.字段）
        if (table != null) {
            String columnTableName = table.getName().toLowerCase();
            List<FieldInfoDto> matchFields = Optional.ofNullable(CollectionUtils.getValueIgnoreFloat(layerFieldTableMap.get(String.valueOf(layer)), columnTableName)).orElse(new HashSet<>()).stream().filter(f -> StringUtils.equalIgnoreFieldSymbol(f.getColumnName(), columName)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(matchFields)) {
                //当前层的所有字段里面叫这个的，正确sql语法中只会有一个，所以get(0)
                FieldInfoDto matchField = matchFields.get(0);
                sourceTableName.set(matchField.getSourceTableName());
                sourceColumn.set(matchField.getSourceColumn());
                fromSourceTable.set(matchField.isFromSourceTable());
                tableAliasName.set(columnTableName);
            }
        }

        return ColumnTableDto.builder().tableAliasName(tableAliasName.get()).sourceTableName(sourceTableName.get()).sourceColumn(sourceColumn.get()).fromSourceTable(fromSourceTable.get()).build();
    }
    
    /**
     * 处理POJO模式的二元表达式
     */
    public static void visitPojoBinaryExpression(PlaceholderExpressionVisitor visitor, BinaryExpression binaryExpression) {
        Expression leftExpression = binaryExpression.getLeftExpression();
        Expression rightExpression = binaryExpression.getRightExpression();
        
        // 处理左右表达式
        if (leftExpression != null) {
            leftExpression.accept(visitor);
        }
        if (rightExpression != null) {
            rightExpression.accept(visitor);
        }
    }
    
    /**
     * 解析WHERE条件中的列表映射关系
     * 
     * @param layer 当前层级
     * @param layerFieldTableMap 层级字段表映射
     * @param column 列
     * @param expression 表达式
     * @param placeholderColumnTableMap 占位符列表映射
     */
    public static void parseWhereColumTable(int layer,
                                          Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap,
                                          Column column,
                                          Expression expression,
                                          Map<String, ColumnTableDto> placeholderColumnTableMap) {
        parseWhereColumTable(String.valueOf(layer), layerFieldTableMap, column, expression, placeholderColumnTableMap);
    }
    
    /**
     * 解析WHERE条件中的列表映射关系
     * 
     * @param layer 当前层级
     * @param layerFieldTableMap 层级字段表映射
     * @param column 列
     * @param expression 表达式
     * @param placeholderColumnTableMap 占位符列表映射
     */
    public static void parseWhereColumTable(String layer,
                                          Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap,
                                          Column column,
                                          Expression expression,
                                          Map<String, ColumnTableDto> placeholderColumnTableMap) {
        // 检查表达式是否包含我们的占位符
        if (expression != null && expression.toString().contains(FieldConstant.PLACEHOLDER)) {
            ColumnTableDto columnTableDto = parseColumn(column, Integer.parseInt(layer), layerFieldTableMap);
            if (columnTableDto != null) {
                placeholderColumnTableMap.put(expression.toString(), columnTableDto);
            }
        }
    }
    
    /**
     * 解析WHERE条件中的列表映射关系（重载方法）
     */
    public static void parseWhereColumTable(int layer,
                                          Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap,
                                          Expression leftExpression,
                                          Expression rightExpression,
                                          Map<String, ColumnTableDto> placeholderColumnTableMap) {
        // 处理左右表达式的映射关系
        // 左边是列，右边是我们的占位符
        if (leftExpression instanceof Column && rightExpression != null && rightExpression.toString().contains(FieldConstant.PLACEHOLDER)) {
            Column column = (Column) leftExpression;
            String placeholder = rightExpression.toString();
            ColumnTableDto columnTableDto = parseColumn(column, layer, layerFieldTableMap);
            if (columnTableDto != null) {
                placeholderColumnTableMap.put(placeholder, columnTableDto);
            }
        }
        
        // 左边是我们的占位符 右边是列
        if (rightExpression instanceof Column && leftExpression != null && leftExpression.toString().contains(FieldConstant.PLACEHOLDER)) {
            Column column = (Column) rightExpression;
            String placeholder = leftExpression.toString();
            ColumnTableDto columnTableDto = parseColumn(column, layer, layerFieldTableMap);
            if (columnTableDto != null) {
                placeholderColumnTableMap.put(placeholder, columnTableDto);
            }
        }
    }
    
    /**
     * 解析WHERE条件中的列表映射关系（重载方法 - 接受BinaryExpression参数）
     */
    public static void parseWhereColumTable(int layer,
                                          Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap,
                                          BinaryExpression expression,
                                          Map<String, ColumnTableDto> placeholderColumnTableMap) {
        Expression leftExpression = expression.getLeftExpression();
        Expression rightExpression = expression.getRightExpression();
        
        parseWhereColumTable(layer, layerFieldTableMap, leftExpression, rightExpression, placeholderColumnTableMap);
    }
    
    /**
     * 将 dto 存放到对应的layerTableMap 中
     *
     * @param layerTableMap key: layer  value( key: tableName value: dto)
     * @author liutangqi
     * @date 2024/3/18 14:17
     **/
    public static void putFieldInfo(Map<String, Map<String, Set<FieldInfoDto>>> layerTableMap, int layer, String tableName, FieldInfoDto dto) {
        Map<String, Set<FieldInfoDto>> layerFieldMap = Optional.ofNullable(layerTableMap.get(String.valueOf(layer))).orElse(new HashMap<>());
        Set<FieldInfoDto> fieldInfoDtos = Optional.ofNullable(CollectionUtils.getValueIgnoreFloat(layerFieldMap, tableName)).orElse(new HashSet<>());

        fieldInfoDtos.add(dto);
        layerFieldMap.put(tableName, fieldInfoDtos);
        layerTableMap.put(String.valueOf(layer), layerFieldMap);
    }

    /**
     * 将 dto 存放到对应的layerTableMap 中
     *
     * @param layerTableMap key: layer  value( key: tableName value: dtos)
     * @author liutangqi
     * @date 2024/3/18 14:17
     **/
    public static void putFieldInfo(Map<String, Map<String, Set<FieldInfoDto>>> layerTableMap, int layer, String tableName, Set<FieldInfoDto> dtos) {
        Map<String, Set<FieldInfoDto>> layerFieldMap = Optional.ofNullable(layerTableMap.get(String.valueOf(layer))).orElse(new HashMap<>());
        Set<FieldInfoDto> fieldInfoDtos = Optional.ofNullable(CollectionUtils.getValueIgnoreFloat(layerFieldMap, tableName)).orElse(new HashSet<>());

        fieldInfoDtos.addAll(dtos);
        layerFieldMap.put(tableName, fieldInfoDtos);
        layerTableMap.put(String.valueOf(layer), layerFieldMap);
    }
    
    
    /**
     * 查找列所属的表名
     */
    private static String findTableForColumn(String layer, 
                                           Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap, 
                                           String columnName) {
        Map<String, Set<FieldInfoDto>> tableFieldMap = layerFieldTableMap.get(layer);
        if (tableFieldMap == null) {
            return null;
        }
        
        for (Map.Entry<String, Set<FieldInfoDto>> entry : tableFieldMap.entrySet()) {
            String tableName = entry.getKey();
            Set<FieldInfoDto> fields = entry.getValue();
            
            for (FieldInfoDto field : fields) {
                if (columnName.equalsIgnoreCase(field.getColumnName()) || 
                    columnName.equalsIgnoreCase(field.getSourceColumn())) {
                    return tableName;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 判断列是否需要加密
     */
    public static boolean needEncrypt(String tableName, String columnName) {
        FieldEncryptor encryptor = TableCache.getFieldEncryptor(tableName, columnName);
        return encryptor != null;
    }

    /**
     * 判断当前column 是否是表字段还是常量
     * 主要用于语法转换时 ` " ' 符号的取舍
     * 注意：来自虚拟表的字段，哪怕这个字段在原虚拟表中属于常量，这个也是表字段
     */
    public static boolean isTableFiled(Column column, int layer, Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        //字段名
        String columName = column.getColumnName();
        //字段所属表 （只有select 别名.字段名 时这个才有值，其它的为null）
        Table table = column.getTable();

        //1.没有指定表名时，从当前层的表的所有字段里面找到这个名字的表( select 字段)
        if (table == null) {
            for (Map.Entry<String, Set<FieldInfoDto>> entry : layerFieldTableMap.getOrDefault(String.valueOf(layer), new HashMap<>()).entrySet()) {
                //任意的表有一个字段符合，则返回true，表示是个表字段
                if (entry.getValue().stream().anyMatch(m -> StringUtils.equalIgnoreFieldSymbol(m.getColumnName(), columName))) {
                    return true;
                }
            }
        }

        //2.有指定表名时，说明肯定来自某张表，哪怕是虚拟表
        return table != null;
    }

    /**
     * 判断当前column 是否需要加解密
     */
    public static boolean needEncrypt(Column column, int layer, Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        //1.匹配所属表信息
        ColumnTableDto columnTableDto = parseColumn(column, layer, layerFieldTableMap);
        if (columnTableDto == null) {
            return false;
        }

        //2.判断是否需要加密
        return needEncrypt(columnTableDto.getSourceTableName(), columnTableDto.getSourceColumn());
    }

    /**
     * 判断表达式是否需要加密
     */
    public static FieldEncryptor needEncryptFieldEncryptor(Expression expression, int layer, Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        if (expression instanceof Column) {
            Column column = (Column) expression;
            if (needEncrypt(column, layer, layerFieldTableMap)) {
                ColumnTableDto columnTableDto = parseColumn(column, layer, layerFieldTableMap);
                return parseFieldEncryptor(columnTableDto);
            }
        }
        return null;
    }

}