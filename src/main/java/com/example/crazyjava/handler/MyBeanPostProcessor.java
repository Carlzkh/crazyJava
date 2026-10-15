package com.example.crazyjava.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            log.info(">>> [Before] {} 初始化前", beanName);
        }
        if ("userController".equals(beanName)) {
            log.info(">>> [userController][Before] {} 初始化前", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            log.info(">>> [After] {} 初始化后，实际类型: {}", beanName, bean.getClass().getName());
        }
        if ("userController".equals(beanName)) {
            log.info(">>> [userController][After] {} 初始化后，实际类型: {}", beanName, bean.getClass().getName());
        }
        return bean;
    }
}
