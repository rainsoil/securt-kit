package com.chu7.securtkit.cache;

import com.chu7.securtkit.dto.ClasssCacheKey;
import com.chu7.securtkit.strategy.FieldEncryptorStrategy;
import com.chu7.securtkit.strategy.DefaultStrategyBase;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 加解密相关策略的缓存
 * 优先加载这个bean，避免有些@PostConstruct 处理逻辑中需要用到这个缓存，但是这个缓存还未初始化完成
 *
 * @author chu7
 * @date 2025/6/24 17:58
 */
@Slf4j
public class EncryptorInstanceCache {

    /**
     * 缓存当前项目中的加解密策略
     * key: 加解密策略的class
     * value: 具体的实例
     **/
    private static final Map<ClasssCacheKey, FieldEncryptorStrategy> INSTANCE_MAP = new HashMap<>();

    /**
     * 初始化spring容器中的加解密策略
     *
     * @author chu7
     * @date 2025/6/24 11:12
     * @param strategies 策略列表
     */
    public void init(List<FieldEncryptorStrategy<?>> strategies) {
        //1.实例化默认策略
        @SuppressWarnings("unchecked")
        List<FieldEncryptorStrategy> rawStrategies = (List<FieldEncryptorStrategy>) (List<?>) strategies;
        DefaultStrategyBase.EncryptorBeanStrategy beanStrategy = new DefaultStrategyBase.EncryptorBeanStrategy(rawStrategies);
        INSTANCE_MAP.put(ClasssCacheKey.buildKey(DefaultStrategyBase.EncryptorBeanStrategy.class), beanStrategy);

        //2.初始化当前spring容器内的实现策略
        for (FieldEncryptorStrategy<?> strategy : strategies) {
            @SuppressWarnings("unchecked")
            FieldEncryptorStrategy rawStrategy = (FieldEncryptorStrategy) strategy;
            INSTANCE_MAP.put(ClasssCacheKey.buildKey(strategy.getClass()), rawStrategy);
        }
    }

    /**
     * 获取当前加解密策略实例
     *
     * @author chu7
     * @date 2025/6/24 11:13
     * @param clazz 策略类
     * @return 策略实例
     */
    public static <T> FieldEncryptorStrategy<T> getInstance(Class<? extends FieldEncryptorStrategy> clazz) {
        //1.先从本地缓存中找
        FieldEncryptorStrategy<T> strategy = INSTANCE_MAP.get(ClasssCacheKey.buildKey(clazz));

        //2.本地缓存找不到，尝试通过无参构造方法进行实例化
        if (strategy == null) {
            try {
                strategy = clazz.newInstance();
                INSTANCE_MAP.put(ClasssCacheKey.buildKey(clazz), strategy);
            } catch (Exception e) {
                throw new RuntimeException(String.format("未找到指定类型的加密策略 %s", clazz), e);
            }
        }
        return strategy;
    }

    /**
     * 测试时 mock 算法实例
     * 不从spring容器中获取，通过无参构造方法反射实例化
     *
     * @author chu7
     * @date 2025/6/25 17:12
     * @param scanBasePackage 扫描包路径
     * @param defaultInstance 默认实例
     */
    public static void mockInstance(String scanBasePackage, FieldEncryptorStrategy defaultInstance) 
            throws InstantiationException, IllegalAccessException {
        Set<Class<? extends FieldEncryptorStrategy>> strategyClasses = new HashSet<>();
        //1.扫描指定路径的实体类（这里简化实现，实际应该用ClassScannerUtil）
        // TODO: 实现类扫描器
        
        //2.实例化这些加解密策略
        for (Class<? extends FieldEncryptorStrategy> strategyClass : strategyClasses) {
            //2.1 默认配置
            if (DefaultStrategyBase.EncryptorBeanStrategy.class.equals(strategyClass)) {
                INSTANCE_MAP.put(ClasssCacheKey.buildKey(strategyClass), defaultInstance);
            }
            //2.2实例化其它的策略
            else {
                FieldEncryptorStrategy strategy = strategyClass.newInstance();
                INSTANCE_MAP.put(ClasssCacheKey.buildKey(strategyClass), strategy);
            }
        }
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        INSTANCE_MAP.clear();
    }
}