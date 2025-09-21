package com.chu7.securtkit.strategy;

import com.chu7.securtkit.annotation.DefaultStrategy;

import java.util.List;

/**
 * 默认策略基类
 * 
 * @author chu7
 * @date 2025/6/30 17:42
 */
public abstract class DefaultStrategyBase {

    /**
     * 默认的加解密策略，使用spring容器中的bean实例
     */
    public static class EncryptorBeanStrategy implements FieldEncryptorStrategy<String> {
        
        private List<FieldEncryptorStrategy> strategies;
        private FieldEncryptorStrategy defaultStrategy;

        public EncryptorBeanStrategy() {
        }

        public EncryptorBeanStrategy(List<FieldEncryptorStrategy> strategies) {
            this.strategies = strategies;
            if (strategies != null && !strategies.isEmpty()) {
                // 查找标注了@DefaultStrategy的策略
                for (FieldEncryptorStrategy strategy : strategies) {
                    if (strategy.getClass().isAnnotationPresent(DefaultStrategy.class)) {
                        this.defaultStrategy = strategy;
                        break;
                    }
                }
                // 如果没找到，使用第一个
                if (this.defaultStrategy == null) {
                    this.defaultStrategy = strategies.get(0);
                }
            }
        }

        @Override
        public String encryption(String oldExpression) {
            if (defaultStrategy != null) {
                return (String) defaultStrategy.encryption(oldExpression);
            }
            return oldExpression;
        }

        @Override
        public String decryption(String oldExpression) {
            if (defaultStrategy != null) {
                return (String) defaultStrategy.decryption(oldExpression);
            }
            return oldExpression;
        }
    }
}