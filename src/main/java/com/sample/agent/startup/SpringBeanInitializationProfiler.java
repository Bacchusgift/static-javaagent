package com.sample.agent.startup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringBeanInitializationProfiler {
    private static Map<Object, Map<String, Long>> applicationContextBeginTimeMap = new ConcurrentHashMap<>();

    public static void beforeInitialize(Object applicationContext, String beanName) {
        applicationContextBeginTimeMap.compute(applicationContext, (ctx, beginTimeMap) -> {
            if (beginTimeMap == null) {
                beginTimeMap = new ConcurrentHashMap<>();
            }
            if (beginTimeMap.put(beanName, System.currentTimeMillis()) != null) {
                // log
            }
            return beginTimeMap;
        });
    }

    public static void afterInitialize(Object applicationContext, String beanName) {
        applicationContextBeginTimeMap.compute(applicationContext, (ctx, beginTimeMap) -> {
            if (beginTimeMap != null && beginTimeMap.get(beanName) != null) {
                long st = beginTimeMap.get(beanName);
                long time = System.currentTimeMillis() - st;
                beginTimeMap.put(beanName, time);
            }
            return beginTimeMap;
        });
    }
}
