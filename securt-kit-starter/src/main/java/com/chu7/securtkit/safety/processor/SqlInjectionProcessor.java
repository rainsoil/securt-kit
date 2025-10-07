package com.chu7.securtkit.safety.processor;

import com.chu7.securtkit.safety.config.SafetyConfig;
import org.springframework.stereotype.Component;

/**
 * SQL 注入处理器
 * - action=block：发现注入特征抛出 SafetyBlockException。
 * - action=replace：使用关键词替换（replaceChar）。
 */
@Component
public class SqlInjectionProcessor extends AbstractRuleBackedProcessor {

    private final SafetyConfig config;

    public SqlInjectionProcessor(SafetyConfig config) { this.config = config; }

    @Override
    public boolean hasAttack(String text) {
        if (config.getSqlInjection() == null || !config.getSqlInjection().isEnabled()) return false;
        return super.hasAttack(text);
    }

    @Override
    public String sanitize(String text) {
        if (config.getSqlInjection() == null || !config.getSqlInjection().isEnabled()) return text;
        return super.sanitize(text);
    }

    @Override
    protected String getAction() { return config.getSqlInjection().getAction(); }
    @Override
    protected String getReplaceChar() { return config.getSqlInjection().getReplaceChar(); }
    @Override
    protected String getBuiltinFile() { return config.getSqlInjection().getBuiltinKeywordFile(); }
    @Override
    protected java.util.List<String> getExternalFiles() { return config.getSqlInjection().getExternalKeywordFiles(); }
}


