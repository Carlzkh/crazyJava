package com.example.crazyjava;  // 包名换成你项目的实际包名

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrazyJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrazyJavaApplication.class, args);
        System.out.println("🚀 SpringBoot 改造成功！");
    }
}