package com.example.crazyjava.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EnvNameLogger {
    public EnvNameLogger(AppProperties appProperties) {
        log.info("当前环境名：{},每页条数：{}",appProperties.getEnvName(),appProperties.getPageSize());
    }
}
