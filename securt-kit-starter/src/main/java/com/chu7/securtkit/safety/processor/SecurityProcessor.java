package com.chu7.securtkit.safety.processor;

import javax.servlet.http.HttpServletRequest;

/**
 * 极简安全处理器接口
 *
 * 职责：为某一类安全策略（XSS/SQL 注入/敏感词）提供检测与清洗能力。
 * 约定：
 * - supports：按请求粒度控制是否启用（如路径、方法、Content-Type）。
 * - hasAttack：仅检测是否包含攻击/违规内容。
 * - sanitize：按策略配置执行“替换”或“阻断”（由调用方捕获异常）。
 */
public interface SecurityProcessor {

    /**
     * 当前请求是否由该处理器处理（可检查是否启用、白名单等）。
     */
    default boolean supports(HttpServletRequest request) { return true; }

    /**
     * 文本是否包含攻击。
     */
    boolean hasAttack(String text);

    /**
     * 清理/过滤文本。
     */
    String sanitize(String text);
}


