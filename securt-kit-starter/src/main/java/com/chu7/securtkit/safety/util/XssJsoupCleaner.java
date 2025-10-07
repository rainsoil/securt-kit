package com.chu7.securtkit.safety.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 基于Jsoup的XSS清洗支持（可选依赖）。
 * 如果运行时环境存在 org.jsoup.*，则使用Jsoup白名单进行清洗；
 * 否则回退为原文返回（不修改）。
 */
public class XssJsoupCleaner {

    private static final Logger log = LoggerFactory.getLogger(XssJsoupCleaner.class);

    /**
     * 使用Jsoup清洗HTML内容（Relaxed白名单）。
     * 在未引入Jsoup依赖时，返回原内容。
     */
    public static String cleanHtml(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        try {
            Class<?> safelistClz = Class.forName("org.jsoup.safety.Safelist");
            Class<?> cleanerClz = Class.forName("org.jsoup.safety.Cleaner");
            Class<?> jsoupClz = Class.forName("org.jsoup.Jsoup");
            Class<?> documentClz = Class.forName("org.jsoup.nodes.Document");

            // Safelist.relaxed()
            Method relaxedMethod = safelistClz.getMethod("relaxed");
            Object safelist = relaxedMethod.invoke(null);

            // Jsoup.parse(html)
            Method parseMethod = jsoupClz.getMethod("parse", String.class);
            Object doc = parseMethod.invoke(null, html);

            // new Cleaner(safelist).clean(doc)
            Object cleaner = cleanerClz.getConstructor(safelistClz).newInstance(safelist);
            Method cleanMethod = cleanerClz.getMethod("clean", documentClz);
            Object cleanedDoc = cleanMethod.invoke(cleaner, doc);

            // cleanedDoc.body().html()
            Method bodyMethod = documentClz.getMethod("body");
            Object body = bodyMethod.invoke(cleanedDoc);
            Class<?> elementClz = Class.forName("org.jsoup.nodes.Element");
            Method htmlMethod = elementClz.getMethod("html");
            Object cleanedHtml = htmlMethod.invoke(body);

            return String.valueOf(cleanedHtml);
        } catch (ClassNotFoundException e) {
            // Jsoup不存在，回退
            log.debug("Jsoup 未找到，XSS清洗回退为原文: {}", e.getMessage());
            return html;
        } catch (Exception e) {
            // 清洗失败，回退
            log.warn("Jsoup 清洗失败，回退为原文: {}", e.getMessage());
            return html;
        }
    }
}


