package com.chu7.securtkit.safety.processor;

import com.chu7.securtkit.safety.config.SafetyConfig;
import org.springframework.stereotype.Component;

/**
 * 敏感词处理器
 * - action=block：发现敏感词抛出 SafetyBlockException。
 * - action=replace：使用关键词替换（replaceChar）。
 */
@Component
public class SensitiveWordProcessor extends AbstractRuleBackedProcessor {

    private final SafetyConfig config;

    public SensitiveWordProcessor(SafetyConfig config) { this.config = config; }

    @Override
    public boolean hasAttack(String text) {
        if (config.getSensitiveWord() == null || !config.getSensitiveWord().isEnabled()) return false;
        return super.hasAttack(text);
    }

    @Override
    public String sanitize(String text) {
        if (config.getSensitiveWord() == null || !config.getSensitiveWord().isEnabled()) return text;
        return super.sanitize(text);
    }

    @Override
    protected String getAction() { return config.getSensitiveWord().getAction(); }
    @Override
    protected String getReplaceChar() { return config.getSensitiveWord().getReplaceChar(); }
    @Override
    protected String getBuiltinFile() { return config.getSensitiveWord().getBuiltinKeywordFile(); }
    @Override
    protected java.util.List<String> getExternalFiles() { return config.getSensitiveWord().getExternalKeywordFiles(); }
}


