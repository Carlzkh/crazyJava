package com.example.crazyjava.aop;

import com.alibaba.fastjson.JSON;
import com.example.crazyjava.entity.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 切点：匹配 controller 包下的所有方法
     */
    @Pointcut("execution(* com.example.crazyjava.controller..*.*(..))")
    public void controllerMethod() {}

    /**
     * 环绕通知：记录请求日志和响应日志
     */
    @Around("controllerMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // 打印请求日志
        log.info("==========================================");
        log.info("请求 URL   : {}", request.getRequestURL());
        log.info("请求方法   : {}", request.getMethod());
        log.info("请求 IP    : {}", request.getRemoteAddr());
        log.info("请求参数   : {}", JSON.toJSONString(request.getParameterMap()));
        log.info("请求 Body  : {}", getRequestParams(joinPoint));

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 打印响应日志
        long costTime = System.currentTimeMillis() - startTime;
        log.info("响应结果   : {}", JSON.toJSONString(result));
        log.info("执行耗时   : {} ms", costTime);
        log.info("=========================================");

        return result;
    }

    /**
     * 获取请求参数（非 GET 请求的 Body 参数）
     */
    private String getRequestParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        // 过滤掉 HttpServletRequest、HttpServletResponse 等框架参数
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                continue;
            }
            if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;
                return "文件名=" + file.getOriginalFilename() + ", 大小=" + file.getSize();
            }
            if (arg instanceof LoginRequest) {
                LoginRequest login = (LoginRequest) arg;
                return "username=" + login.getUsername() + ", password=***";
            }
            try {
                return JSON.toJSONString(arg);
            } catch (Exception e) {
                return String.valueOf(arg);
            }
        }
        return "";
    }
}