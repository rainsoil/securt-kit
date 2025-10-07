package com.chu7.securtkit.safety.service;

import cn.hutool.extra.servlet.ServletUtil;
import com.chu7.securtkit.safety.config.SafetyConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 白名单服务
 * 仅负责“模块级”白名单判断（XSS/SQL/敏感词），不再存在根级白名单。
 * URL 使用 Ant 样式匹配；IP 仅支持精确匹配（如需 CIDR 可后续增强）。
 */
@Component
public class WhitelistService {

    private static final AntPathMatcher matcher = new AntPathMatcher();
    private final SafetyConfig config;

    public WhitelistService(SafetyConfig config) {
        this.config = config;
    }

    /**
     * XSS 专属白名单（URL/IP）
     * 命中任一条件则跳过 XSS 处理器：
     * - URL 在 XSS URL 白名单
     * - 客户端 IP 在 XSS IP 白名单
     */
    public boolean isWhitelistedForXss(HttpServletRequest request) {
        if (config.getXss() == null || !config.getXss().isEnabled()) return false;
        String uri = request.getRequestURI();
        SafetyConfig.UrlWhitelistConfig urlCfg = config.getXss().getUrlWhitelist();
        if (urlCfg != null && urlCfg.isEnabled() && matches(uri, urlCfg.getAllowedUrls())) return true;
        SafetyConfig.IpWhitelistConfig ipCfg = config.getXss().getIpWhitelist();
        if (ipCfg != null && ipCfg.isEnabled()) {
            String ip = ServletUtil.getClientIP(request);
            if (inIpList(ip, ipCfg.getAllowedIps())) return true;
        }
        return false;
    }

    /**
     * SQL 注入专属白名单（URL/IP）
     * 命中任一条件则跳过 SQL 注入处理器。
     */
    public boolean isWhitelistedForSql(HttpServletRequest request) {
        if (config.getSqlInjection() == null || !config.getSqlInjection().isEnabled()) return false;
        String uri = request.getRequestURI();
        SafetyConfig.UrlWhitelistConfig urlCfg = config.getSqlInjection().getUrlWhitelist();
        if (urlCfg != null && urlCfg.isEnabled() && matches(uri, urlCfg.getAllowedUrls())) return true;
        SafetyConfig.IpWhitelistConfig ipCfg = config.getSqlInjection().getIpWhitelist();
        if (ipCfg != null && ipCfg.isEnabled()) {
            String ip = ServletUtil.getClientIP(request);
            if (inIpList(ip, ipCfg.getAllowedIps())) return true;
        }
        return false;
    }

    /**
     * 敏感词专属白名单（URL/IP）
     * 命中任一条件则跳过敏感词处理器。
     */
    public boolean isWhitelistedForSensitive(HttpServletRequest request) {
        if (config.getSensitiveWord() == null || !config.getSensitiveWord().isEnabled()) return false;
        String uri = request.getRequestURI();
        SafetyConfig.UrlWhitelistConfig urlCfg = config.getSensitiveWord().getUrlWhitelist();
        if (urlCfg != null && urlCfg.isEnabled() && matches(uri, urlCfg.getAllowedUrls())) return true;
        SafetyConfig.IpWhitelistConfig ipCfg = config.getSensitiveWord().getIpWhitelist();
        if (ipCfg != null && ipCfg.isEnabled()) {
            String ip = ServletUtil.getClientIP(request);
            if (inIpList(ip, ipCfg.getAllowedIps())) return true;
        }
        return false;
    }

    private boolean matches(String uri, List<String> patterns) {
        if (patterns == null) return false;
        for (String p : patterns) if (matcher.match(p, uri)) return true;
        return false;
    }

    private boolean inIpList(String ip, List<String> ips) {
        if (ips == null || ip == null) return false;
        for (String allowed : ips) {
            if (ip.equals(allowed)) return true;
        }
        return false;
    }
}


