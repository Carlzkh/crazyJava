package com.example.crazyjava;  // 包名换成你项目的实际包名

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CrazyJavaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CrazyJavaApplication.class, args);
        System.out.println("🚀 SpringBoot 改造成功！");
        // 打印容器中所有 Bean 的名称
        String[] beanNames = context.getBeanDefinitionNames();
        for (String name : beanNames) {
            Object bean = context.getBean(name);
            System.out.println(name + " → " + bean.getClass().getName());
        }
    }
}