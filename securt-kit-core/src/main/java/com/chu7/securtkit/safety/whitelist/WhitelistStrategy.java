package com.chu7.securtkit.safety.whitelist;

/**
 * 白名单策略接口
 * 定义白名单管理的基本行为
 * 
 * @author chu7
 * @date 2024/12/19
 */
public interface WhitelistStrategy {
    
    /**
     * 检查内容是否在白名单中
     * @param content 待检查的内容
     * @return 是否在白名单中
     */
    boolean isInWhitelist(String content);
    
    /**
     * 获取白名单类型
     * @return 白名单类型
     */
    String getWhitelistType();
    
    /**
     * 初始化白名单
     * @param config 白名单配置
     */
    void initWhitelist(Object config);
    
    /**
     * 重新加载白名单
     * @param config 白名单配置
     */
    void reloadWhitelist(Object config);
    
    /**
     * 清空白名单缓存
     */
    void clearWhitelistCache();
    
    /**
     * 获取白名单统计信息
     * @return 统计信息
     */
    java.util.Map<String, Integer> getWhitelistStats();
}
