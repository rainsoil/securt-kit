package com.chu7.securtkit.safety.processor;

/**
 * 带规则缓存的处理器基类：组合“规则加载/匹配/替换”与 processor 契约。
 */
public abstract class AbstractRuleBackedProcessor extends AbstractRuleProvider implements SecurityProcessor {

    /** 策略：replace 或 block */
    protected abstract String getAction();
    /** 替换字符 */
    protected abstract String getReplaceChar();
    /** 内置规则文件 */
    protected abstract String getBuiltinFile();
    /** 外部规则文件列表 */
    protected abstract java.util.List<String> getExternalFiles();

    /** 初始化加载 */
    protected void ensureLoaded() {
        if (keywordCache.isEmpty()) {
            load(getBuiltinFile(), getExternalFiles());
        }
    }

    @Override
    public boolean hasAttack(String text) {
        ensureLoaded();
        return containsAny(text);
    }

    @Override
    public String sanitize(String text) {
        ensureLoaded();
        if ("block".equalsIgnoreCase(getAction())) {
            throw new com.chu7.securtkit.safety.SafetyBlockException("内容被拦截");
        }
        String rc = getReplaceChar();
        return replaceAll(text, rc == null ? "*" : rc);
    }

    /** 显式触发重载 */
    public void reload() {
        load(getBuiltinFile(), getExternalFiles());
    }
}


