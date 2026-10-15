package com.example.crazyjava.minispring;

import com.example.crazyjava.minispring.annotation.MyAutowired;
import com.example.crazyjava.minispring.annotation.MyComponent;
import com.example.crazyjava.minispring.annotation.MyService;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MiniApplicationContext {

    private final Map<Class<?>, Object> beanMap = new HashMap<>();

    public MiniApplicationContext(String basePackage) throws Exception {
        // 1. 扫描包，创建 Bean 实例
        scanAndCreateBeans(basePackage);
        // 2. 注入依赖
        injectDependencies();
    }

    /**
     * 扫描指定包下所有类，实例化带 @MyComponent 或 @MyService 的类
     */
    private void scanAndCreateBeans(String basePackage) throws Exception {
        String packagePath = basePackage.replace('.', '/');
        URL resource = Thread.currentThread().getContextClassLoader().getResource(packagePath);
        if (resource == null) {
            System.out.println("未找到包: " + basePackage);
            return;
        }

        File dir = new File(resource.getFile());
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith(".class")) {
                String className = basePackage + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(MyComponent.class)
                        || clazz.isAnnotationPresent(MyService.class)) {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beanMap.put(clazz, instance);
                    System.out.println("创建 Bean: " + clazz.getSimpleName());
                }
            }
        }
    }

    /**
     * 遍历所有 Bean，注入 @MyAutowired 标注的字段
     */
    private void injectDependencies() throws IllegalAccessException {
        for (Object bean : beanMap.values()) {
            Class<?> clazz = bean.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    Object dependency = beanMap.get(field.getType());
                    if (dependency == null) {
                        throw new RuntimeException("找不到依赖: " + field.getType().getName());
                    }
                    field.setAccessible(true);
                    field.set(bean, dependency);
                    System.out.println("注入: " + clazz.getSimpleName() + "." + field.getName() + " ← " + field.getType().getSimpleName());
                }
            }
        }
    }

    /**
     * 获取 Bean
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz);
    }
}