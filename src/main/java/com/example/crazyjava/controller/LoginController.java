package com.example.crazyjava.controller;

import com.example.crazyjava.common.Result;
import com.example.crazyjava.entity.LoginRequest;
import com.example.crazyjava.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="登录")
@RestController
@RequestMapping("/api/${api.version}")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    @Operation(summary = "登录并返回token")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = loginService.login(request.getUsername(), request.getPassword());
        return Result.success(token);
    }
}