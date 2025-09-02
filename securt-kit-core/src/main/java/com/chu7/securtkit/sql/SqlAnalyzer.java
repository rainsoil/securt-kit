package com.chu7.securtkit.sql;

/**
 * SQL解析器接口，定义SQL解析的标准方法
 *
 * @author security-kit
 */
public interface SqlAnalyzer {
    
    /**
     * 解析SQL语句
     *
     * @param sql 原始SQL语句
     * @return SQL分析结果
     */
    SqlAnalysisResult analyze(String sql);
    
    /**
     * 解析SQL语句（带参数）
     *
     * @param sql 原始SQL语句
     * @param parameters SQL参数
     * @return SQL分析结果
     */
    SqlAnalysisResult analyze(String sql, Object parameters);
    
    /**
     * 判断SQL是否需要处理
     *
     * @param sql SQL语句
     * @return 是否需要处理
     */
    boolean needProcess(String sql);
    
    /**
     * 获取解析器名称
     *
     * @return 解析器名称
     */
    String getAnalyzerName();
} 