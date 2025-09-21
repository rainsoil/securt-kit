package com.chu7.securtkit.visitor.fieldparse;

import com.chu7.securtkit.dto.BaseFieldParseTable;
import com.chu7.securtkit.dto.FieldInfoDto;
import com.chu7.securtkit.util.CollectionUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.expression.Alias;

import java.util.*;

/**
 * 解析select语句中的字段信息
 *
 * @author liutangqi
 * @date 2024/3/4 14:26
 */
public class FieldParseParseTableSelectVisitor extends BaseFieldParseTable implements SelectVisitor {

    private FieldParseParseTableSelectVisitor(int layer, 
                                             Map<String, Map<String, Set<FieldInfoDto>>> layerSelectTableFieldMap, 
                                             Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        super(layer, layerSelectTableFieldMap, layerFieldTableMap);
    }

    public static FieldParseParseTableSelectVisitor newInstanceFirstLayer() {
        return new FieldParseParseTableSelectVisitor(1, new HashMap<>(), new HashMap<>());
    }

    public static FieldParseParseTableSelectVisitor newInstanceFirstLayer(Map<String, Map<String, Set<FieldInfoDto>>> layerFieldTableMap) {
        return new FieldParseParseTableSelectVisitor(1, new HashMap<>(), layerFieldTableMap);
    }

    public static FieldParseParseTableSelectVisitor newInstanceNextLayer(BaseFieldParseTable baseFieldParseTable) {
        return new FieldParseParseTableSelectVisitor(
            baseFieldParseTable.getLayer() + 1,
            baseFieldParseTable.getLayerSelectTableFieldMap(),
            baseFieldParseTable.getLayerFieldTableMap()
        );
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        // from 的表
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem != null) {
            FieldParseParseTableFromItemVisitor fieldParseTableFromItemVisitor = FieldParseParseTableFromItemVisitor.newInstanceCurLayer(this);
            fromItem.accept(fieldParseTableFromItemVisitor);
        }

        //join 的表
        List<Join> joins = Optional.ofNullable(plainSelect.getJoins()).orElse(new ArrayList<>());
        for (Join join : joins) {
            FromItem rightItem = join.getRightItem();
            FieldParseParseTableFromItemVisitor joinFieldTableFromItemVisitor = FieldParseParseTableFromItemVisitor.newInstanceCurLayer(this);
            rightItem.accept(joinFieldTableFromItemVisitor);
        }

        //查询的全部字段
        List<SelectItem<?>> selectItems = plainSelect.getSelectItems();
        for (SelectItem selectItem : selectItems) {
            FieldParseParseSelectItemVisitor fieldParseSelectItemVisitor = FieldParseParseSelectItemVisitor.newInstanceCurLayer(this);
            selectItem.accept(fieldParseSelectItemVisitor);
        }
    }

    @Override
    public void visit(SetOperationList setOperationList) {
        // 处理union等集合操作
        if (CollectionUtils.isNotEmpty(setOperationList.getSelects())) {
            for (Select select : setOperationList.getSelects()) {
                select.accept(this);
            }
        }
    }

    @Override
    public void visit(ParenthesedSelect parenthesedSelect) {
        // 处理括号包围的select
        if (parenthesedSelect.getSelect() != null) {
            parenthesedSelect.getSelect().accept(this);
        }
    }

    @Override
    public void visit(WithItem withItem) {
        // 处理with子句
    }

    @Override
    public void visit(Values values) {
        // 处理values子句
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        // 处理横向子查询
    }

    @Override
    public void visit(TableStatement tableStatement) {
        // 处理表语句
    }

}
