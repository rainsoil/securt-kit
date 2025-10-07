package com.chu7.securtkit.safety.processor;

import com.chu7.securtkit.safety.config.SafetyConfig;
import com.chu7.securtkit.safety.util.XssJsoupCleaner;
import org.springframework.stereotype.Component;

/**
 * XSS 处理器
 * - engine=jsoup 时：使用 Jsoup 白名单清洗（若未引入依赖将回退为原文）。
 * - engine=regex 时：使用关键词替换。
 * - action=block 时：发现攻击抛出 SafetyBlockException。
 */
@Component
public class XssProcessor extends AbstractRuleBackedProcessor {

    private final SafetyConfig config;

    public XssProcessor(SafetyConfig config) { this.config = config; }

    @Override
    public boolean hasAttack(String text) {
        if (config.getXss() == null || !config.getXss().isEnabled()) return false;
        String engine = config.getXss().getEngine();
        if (engine != null && engine.equalsIgnoreCase("jsoup")) {
            String cleaned = XssJsoupCleaner.cleanHtml(text);
            return cleaned != null && !cleaned.equals(text);
        }
        return super.hasAttack(text);
    }

    @Override
    public String sanitize(String text) {
        if (config.getXss() == null || !config.getXss().isEnabled()) return text;
        String action = config.getXss().getAction();
        String engine = config.getXss().getEngine();
        if (engine != null && engine.equalsIgnoreCase("jsoup")) {
            if (action != null && action.equalsIgnoreCase("block")) {
                throw new com.chu7.securtkit.safety.SafetyBlockException("XSS 攻击拦截");
            }
            return XssJsoupCleaner.cleanHtml(text);
        }
        return super.sanitize(text);
    }

    @Override
    protected String getAction() { return config.getXss().getAction(); }
    @Override
    protected String getReplaceChar() { return config.getXss().getReplaceChar(); }
    @Override
    protected String getBuiltinFile() { return config.getXss().getBuiltinKeywordFile(); }
    @Override
    protected java.util.List<String> getExternalFiles() { return config.getXss().getExternalKeywordFiles(); }
}


