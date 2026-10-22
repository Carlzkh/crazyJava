package com.example.crazyjava.service;

import com.example.crazyjava.exception.BusinessException;
import com.example.crazyjava.config.AppProperties;
import com.example.crazyjava.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginService {

    private final AppProperties appProperties;
    private final Set<String> tokens = ConcurrentHashMap.newKeySet();

    public LoginService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String login(String username, String password) {
        if (!appProperties.getLoginUsername().equals(username)
                || !appProperties.getLoginPassword().equals(password)) {
            throw new BusinessException(ResultCodeEnum.LOGIN_FAILED);
        }
        String token = UUID.randomUUID().toString();
        tokens.add(token);
        return token;
    }
    public boolean isValid(String token) {
        return token != null && tokens.contains(token);
    }
}
