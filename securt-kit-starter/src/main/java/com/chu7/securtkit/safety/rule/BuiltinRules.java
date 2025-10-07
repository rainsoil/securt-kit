package com.chu7.securtkit.safety.rule;

import java.util.*;

/**
 * 内置安全规则
 */
public class BuiltinRules {
    
    /**
     * 获取XSS防护内置规则
     */
    public static SecurityRuleSet getXssRules() {
        SecurityRuleSet ruleSet = new SecurityRuleSet();
        ruleSet.setName("XSS Protection Rules");
        ruleSet.setDescription("Built-in XSS protection rules");
        ruleSet.setVersion("1.0.0");
        ruleSet.setEnabled(true);
        
        List<SecurityRule> rules = new ArrayList<>();
        
        // 脚本标签检测规则
        SecurityRule scriptRule = new SecurityRule();
        scriptRule.setId("xss-script-tags");
        scriptRule.setName("Script Tags Detection");
        scriptRule.setDescription("Detect and filter script tags");
        scriptRule.setType("XSS");
        scriptRule.setEnabled(true);
        scriptRule.setPriority(10);
        scriptRule.setAction("FILTER");
        scriptRule.setPatterns(Arrays.asList(
            "<script[^>]*>.*?</script>",
            "<script[^>]*>",
            "<iframe[^>]*>.*?</iframe>",
            "<object[^>]*>.*?</object>",
            "<embed[^>]*>"
        ));
        rules.add(scriptRule);
        
        // 事件处理器检测规则
        SecurityRule eventRule = new SecurityRule();
        eventRule.setId("xss-event-handlers");
        eventRule.setName("Event Handlers Detection");
        eventRule.setDescription("Detect and filter event handlers");
        eventRule.setType("XSS");
        eventRule.setEnabled(true);
        eventRule.setPriority(20);
        eventRule.setAction("FILTER");
        eventRule.setPatterns(Arrays.asList(
            "on\\w+\\s*=",
            "javascript\\s*:",
            "vbscript\\s*:",
            "expression\\s*\\("
        ));
        rules.add(eventRule);
        
        // 危险属性检测规则
        SecurityRule attributeRule = new SecurityRule();
        attributeRule.setId("xss-dangerous-attributes");
        attributeRule.setName("Dangerous Attributes Detection");
        attributeRule.setDescription("Detect and filter dangerous attributes");
        attributeRule.setType("XSS");
        attributeRule.setEnabled(true);
        attributeRule.setPriority(30);
        attributeRule.setAction("FILTER");
        attributeRule.setPatterns(Arrays.asList(
            "src\\s*=\\s*[\"']?javascript:",
            "href\\s*=\\s*[\"']?javascript:",
            "style\\s*=\\s*[\"']?.*expression\\s*\\("
        ));
        rules.add(attributeRule);
        
        ruleSet.setRules(rules);
        return ruleSet;
    }
    
    /**
     * 获取SQL注入防护内置规则
     */
    public static SecurityRuleSet getSqlInjectionRules() {
        SecurityRuleSet ruleSet = new SecurityRuleSet();
        ruleSet.setName("SQL Injection Protection Rules");
        ruleSet.setDescription("Built-in SQL injection protection rules");
        ruleSet.setVersion("1.0.0");
        ruleSet.setEnabled(true);
        
        List<SecurityRule> rules = new ArrayList<>();
        
        // 危险关键词检测规则
        SecurityRule keywordRule = new SecurityRule();
        keywordRule.setId("sql-dangerous-keywords");
        keywordRule.setName("Dangerous Keywords Detection");
        keywordRule.setDescription("Detect dangerous SQL keywords");
        keywordRule.setType("SQL_INJECTION");
        keywordRule.setEnabled(true);
        keywordRule.setPriority(10);
        keywordRule.setAction("BLOCK");
        keywordRule.setPatterns(Arrays.asList(
            "\\bselect\\b",
            "\\binsert\\b",
            "\\bupdate\\b",
            "\\bdelete\\b",
            "\\bdrop\\b",
            "\\bcreate\\b",
            "\\balter\\b",
            "\\bexec\\b",
            "\\bexecute\\b",
            "\\bunion\\b"
        ));
        rules.add(keywordRule);
        
        // 联合查询检测规则
        SecurityRule unionRule = new SecurityRule();
        unionRule.setId("sql-union-queries");
        unionRule.setName("Union Queries Detection");
        unionRule.setDescription("Detect union-based SQL injection");
        unionRule.setType("SQL_INJECTION");
        unionRule.setEnabled(true);
        unionRule.setPriority(20);
        unionRule.setAction("BLOCK");
        unionRule.setPatterns(Arrays.asList(
            "union\\s+select",
            "union\\s+all\\s+select"
        ));
        rules.add(unionRule);
        
        // 注释检测规则
        SecurityRule commentRule = new SecurityRule();
        commentRule.setId("sql-comments");
        commentRule.setName("SQL Comments Detection");
        commentRule.setDescription("Detect SQL comments");
        commentRule.setType("SQL_INJECTION");
        commentRule.setEnabled(true);
        commentRule.setPriority(30);
        commentRule.setAction("FILTER");
        commentRule.setPatterns(Arrays.asList(
            "--.*",
            "/\\*.*?\\*/"
        ));
        rules.add(commentRule);
        
        // 布尔盲注检测规则
        SecurityRule booleanRule = new SecurityRule();
        booleanRule.setId("sql-boolean-blind");
        booleanRule.setName("Boolean Blind Injection Detection");
        booleanRule.setDescription("Detect boolean-based blind SQL injection");
        booleanRule.setType("SQL_INJECTION");
        booleanRule.setEnabled(true);
        booleanRule.setPriority(40);
        booleanRule.setAction("BLOCK");
        booleanRule.setPatterns(Arrays.asList(
            "and\\s+\\d+\\s*=\\s*\\d+",
            "or\\s+\\d+\\s*=\\s*\\d+",
            "and\\s+\\d+\\s*=\\s*\\d+\\s*--",
            "or\\s+\\d+\\s*=\\s*\\d+\\s*--"
        ));
        rules.add(booleanRule);
        
        ruleSet.setRules(rules);
        return ruleSet;
    }
    
    /**
     * 获取敏感词过滤内置规则
     */
    public static SecurityRuleSet getSensitiveWordRules() {
        SecurityRuleSet ruleSet = new SecurityRuleSet();
        ruleSet.setName("Sensitive Word Filtering Rules");
        ruleSet.setDescription("Built-in sensitive word filtering rules");
        ruleSet.setVersion("1.0.0");
        ruleSet.setEnabled(true);
        
        List<SecurityRule> rules = new ArrayList<>();
        
        // 政治敏感词规则
        SecurityRule politicalRule = new SecurityRule();
        politicalRule.setId("sensitive-political");
        politicalRule.setName("Political Sensitive Words");
        politicalRule.setDescription("Filter political sensitive words");
        politicalRule.setType("SENSITIVE_WORD");
        politicalRule.setEnabled(true);
        politicalRule.setPriority(10);
        politicalRule.setAction("REPLACE");
        politicalRule.setReplacement("***");
        politicalRule.setPatterns(Arrays.asList(
            "政治",
            "政府",
            "国家",
            "领导人",
            "主席",
            "总理"
        ));
        rules.add(politicalRule);
        
        // 暴力敏感词规则
        SecurityRule violenceRule = new SecurityRule();
        violenceRule.setId("sensitive-violence");
        violenceRule.setName("Violence Sensitive Words");
        violenceRule.setDescription("Filter violence sensitive words");
        violenceRule.setType("SENSITIVE_WORD");
        violenceRule.setEnabled(true);
        violenceRule.setPriority(20);
        violenceRule.setAction("REPLACE");
        violenceRule.setReplacement("***");
        violenceRule.setPatterns(Arrays.asList(
            "暴力",
            "血腥",
            "恐怖",
            "爆炸",
            "枪击",
            "刀砍"
        ));
        rules.add(violenceRule);
        
        // 色情敏感词规则
        SecurityRule pornRule = new SecurityRule();
        pornRule.setId("sensitive-pornography");
        pornRule.setName("Pornography Sensitive Words");
        pornRule.setDescription("Filter pornography sensitive words");
        pornRule.setType("SENSITIVE_WORD");
        pornRule.setEnabled(true);
        pornRule.setPriority(30);
        pornRule.setAction("REPLACE");
        pornRule.setReplacement("***");
        pornRule.setPatterns(Arrays.asList(
            "色情",
            "黄色",
            "成人",
            "裸体"
        ));
        rules.add(pornRule);
        
        ruleSet.setRules(rules);
        return ruleSet;
    }
    
    /**
     * 获取所有内置规则
     */
    public static List<SecurityRuleSet> getAllBuiltinRules() {
        return Arrays.asList(
            getXssRules(),
            getSqlInjectionRules(),
            getSensitiveWordRules()
        );
    }
}
