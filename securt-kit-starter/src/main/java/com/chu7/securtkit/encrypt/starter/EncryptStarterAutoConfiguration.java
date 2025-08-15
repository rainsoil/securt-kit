package com.chu7.securtkit.encrypt.starter;

import com.chu7.securtkit.encrypt.config.EncryptAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 加密启动器自动配置类
 * 导入核心模块的配置
 *
 * @author chu7
 * @date 2025/8/15
 */
@Configuration
@AutoConfigureAfter(EncryptAutoConfiguration.class)
@Import(EncryptAutoConfiguration.class)
public class EncryptStarterAutoConfiguration {
    
    // 这个类主要用于导入核心模块的配置
    // 具体的Bean注册都在EncryptAutoConfiguration中完成
}
