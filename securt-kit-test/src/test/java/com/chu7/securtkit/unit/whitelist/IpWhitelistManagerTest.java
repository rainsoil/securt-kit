package com.chu7.securtkit.unit.whitelist;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.whitelist.IpWhitelistManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IP白名单管理器测试
 */
@DisplayName("IP白名单管理器测试")
class IpWhitelistManagerTest {

    private SafetyConfig.XssConfig.IpWhitelistConfig config;

    @BeforeEach
    void setUp() {
        config = new SafetyConfig.XssConfig.IpWhitelistConfig();
        config.setEnabled(true);
        config.getAllowedIps().addAll(Arrays.asList(
            "192.168.1.100",
            "10.0.0.0/8",
            "172.16.0.0/12"
        ));
    }

    @Test
    @DisplayName("测试IP白名单初始化")
    void testInitIpWhitelist() {
        // 清空缓存
        IpWhitelistManager.clearIpWhitelistCache();
        
        // 初始化IP白名单
        IpWhitelistManager.initIpWhitelist(config);
        
        // 验证IP白名单是否生效
        assertTrue(IpWhitelistManager.isIpInWhitelist("192.168.1.100"));
        assertTrue(IpWhitelistManager.isIpInWhitelist("10.1.1.1"));
        assertTrue(IpWhitelistManager.isIpInWhitelist("172.16.1.1"));
        
        // 验证不在白名单中的IP
        assertFalse(IpWhitelistManager.isIpInWhitelist("8.8.8.8"));
        assertFalse(IpWhitelistManager.isIpInWhitelist("1.1.1.1"));
    }

    @Test
    @DisplayName("测试CIDR网段匹配")
    void testCidrMatching() {
        // 清空缓存
        IpWhitelistManager.clearIpWhitelistCache();
        
        // 初始化IP白名单
        IpWhitelistManager.initIpWhitelist(config);
        
        // 测试CIDR网段匹配
        assertTrue(IpWhitelistManager.isIpInWhitelist("10.0.0.1"));
        assertTrue(IpWhitelistManager.isIpInWhitelist("10.255.255.255"));
        assertTrue(IpWhitelistManager.isIpInWhitelist("172.16.0.1"));
        assertTrue(IpWhitelistManager.isIpInWhitelist("172.31.255.255"));
        
        // 测试不在网段中的IP
        assertFalse(IpWhitelistManager.isIpInWhitelist("11.0.0.1"));
        assertFalse(IpWhitelistManager.isIpInWhitelist("172.15.255.255"));
    }

    @Test
    @DisplayName("测试IP白名单统计信息")
    void testGetIpWhitelistStats() {
        // 清空缓存
        IpWhitelistManager.clearIpWhitelistCache();
        
        // 初始化IP白名单
        IpWhitelistManager.initIpWhitelist(config);
        
        // 获取统计信息
        Map<String, Object> stats = IpWhitelistManager.getIpWhitelistStats();
        
        // 验证统计信息
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalIps"));
        assertTrue(stats.containsKey("ipCount"));
        assertTrue(stats.containsKey("cidrCount"));
        
        // 验证数量大于0
        assertTrue((Integer) stats.get("totalIps") > 0);
    }

    @Test
    @DisplayName("测试IP白名单重新加载")
    void testReloadIpWhitelist() {
        // 清空缓存
        IpWhitelistManager.clearIpWhitelistCache();
        
        // 初始化IP白名单
        IpWhitelistManager.initIpWhitelist(config);
        
        // 获取初始统计信息
        Map<String, Object> initialStats = IpWhitelistManager.getIpWhitelistStats();
        
        // 重新加载IP白名单
        IpWhitelistManager.reloadIpWhitelist(config);
        
        // 获取重新加载后的统计信息
        Map<String, Object> reloadedStats = IpWhitelistManager.getIpWhitelistStats();
        
        // 验证统计信息一致
        assertEquals(initialStats.get("totalIps"), reloadedStats.get("totalIps"));
    }

    @Test
    @DisplayName("测试空值和无效IP处理")
    void testNullAndInvalidIpHandling() {
        // 清空缓存
        IpWhitelistManager.clearIpWhitelistCache();
        
        // 初始化IP白名单
        IpWhitelistManager.initIpWhitelist(config);
        
        // 测试空值
        assertFalse(IpWhitelistManager.isIpInWhitelist(null));
        assertFalse(IpWhitelistManager.isIpInWhitelist(""));
        assertFalse(IpWhitelistManager.isIpInWhitelist("   "));
        
        // 测试无效IP
        assertFalse(IpWhitelistManager.isIpInWhitelist("invalid-ip"));
        assertFalse(IpWhitelistManager.isIpInWhitelist("256.256.256.256"));
        assertFalse(IpWhitelistManager.isIpInWhitelist("192.168.1"));
    }

    @Test
    @DisplayName("测试IP白名单禁用")
    void testIpWhitelistDisabled() {
        // 禁用IP白名单
        config.setEnabled(false);
        
        // 清空缓存
        IpWhitelistManager.clearIpWhitelistCache();
        
        // 初始化IP白名单
        IpWhitelistManager.initIpWhitelist(config);
        
        // 验证IP白名单未加载
        assertFalse(IpWhitelistManager.isIpInWhitelist("192.168.1.100"));
        assertFalse(IpWhitelistManager.isIpInWhitelist("10.1.1.1"));
    }
}
