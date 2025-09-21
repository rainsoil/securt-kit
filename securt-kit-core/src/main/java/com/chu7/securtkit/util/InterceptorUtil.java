package com.chu7.securtkit.util;

import com.chu7.securtkit.annotation.FieldInterceptorOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.plugin.Interceptor;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 拦截器工具类
 * 
 * @author chu7
 * @date 2025/5/26 11:28
 */
@Slf4j
public class InterceptorUtil {
    
    /**
     * 对拦截器进行排序
     */
    public static void sort(Configuration configuration) {
        List<Interceptor> interceptors = configuration.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            return;
        }
        
        // 创建新的拦截器列表进行排序
        List<Interceptor> sortedInterceptors = new ArrayList<>(interceptors);
        
        // 根据@FieldInterceptorOrder注解的值进行排序
        sortedInterceptors.sort(new Comparator<Interceptor>() {
            @Override
            public int compare(Interceptor o1, Interceptor o2) {
                int order1 = getInterceptorOrder(o1);
                int order2 = getInterceptorOrder(o2);
                return Integer.compare(order1, order2);
            }
        });
        
        // 由于原列表可能是不可修改的，我们通过反射重新设置拦截器列表
        try {
            // 使用反射获取拦截器列表字段并重新设置
            java.lang.reflect.Field interceptorsField = Configuration.class.getDeclaredField("interceptors");
            interceptorsField.setAccessible(true);
            interceptorsField.set(configuration, sortedInterceptors);
        } catch (Exception e) {
            // 如果反射失败，记录警告但不抛出异常
            log.warn("【securt-kit】无法通过反射重新设置拦截器列表，拦截器可能未按预期顺序执行: {}", e.getMessage());
        }
    }
    
    /**
     * 获取拦截器的执行顺序
     */
    private static int getInterceptorOrder(Interceptor interceptor) {
        FieldInterceptorOrder order = interceptor.getClass().getAnnotation(FieldInterceptorOrder.class);
        return order != null ? order.value() : Integer.MAX_VALUE;
    }
}