package com.example.crazyjava.service;

import com.example.crazyjava.exception.BusinessException;
import com.example.crazyjava.config.AppProperties;
import com.example.crazyjava.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginService {

    private final AppProperties appProperties;
    private final Set<String> tokens = ConcurrentHashMap.newKeySet();
    private final LoginAuditService loginAuditService;


    public LoginService(AppProperties appProperties, LoginAuditService loginAuditService) {
        this.appProperties = appProperties;
        this.loginAuditService = loginAuditService;
    }

    public String login(String username, String password) {
        if (!appProperties.getLoginUsername().equals(username)
                || !appProperties.getLoginPassword().equals(password)) {
            throw new BusinessException(ResultCodeEnum.LOGIN_FAILED);
        }
        String token = UUID.randomUUID().toString();
        tokens.add(token);
        loginAuditService.record(username);
        return token;
    }
    public boolean isValid(String token) {
        return token != null && tokens.contains(token);
    }
    @Scheduled(fixedRate = 15000)
    public void logTokenCount() {
        log.info("当前有效 token 数量: {}", tokens.size());
    }



}
