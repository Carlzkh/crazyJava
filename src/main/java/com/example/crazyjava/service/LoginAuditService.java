package com.example.crazyjava.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginAuditService {

    @Async
    public void record(String username) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("登录记录已写完，用户={}，线程={}", username, Thread.currentThread().getName());
    }
}