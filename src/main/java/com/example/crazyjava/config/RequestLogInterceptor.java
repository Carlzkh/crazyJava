package com.example.crazyjava.config;

import com.alibaba.fastjson.JSON;
import com.example.crazyjava.common.Result;
import com.example.crazyjava.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {
    private final LoginService loginService;

    @Value("${api.version}")
    private String apiVersion;

    public RequestLogInterceptor(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        log.info("拦截器：{} {}", request.getMethod(), request.getRequestURI());
        String prefix = "/api/" + apiVersion + "/users";
        if (request.getRequestURI().startsWith(prefix)) {
            String token = request.getHeader("Authorization");
            if (!loginService.isValid(token)) {
                response.setStatus(401);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(Result.error(401, "未登录")));
                return false;
            }
        }
        return true;
    }
    }


