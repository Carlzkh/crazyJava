package com.example.crazyjava.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping
    public String hello(){
        log.info("info级别的日志！");
        return "Hello from 改造后的 SpringBoot 项目！你的学习代码依然可以运行。！！";
    }
}
