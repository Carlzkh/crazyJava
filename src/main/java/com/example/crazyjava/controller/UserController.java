package com.example.crazyjava.controller;


import com.example.crazyjava.common.Result;
import com.example.crazyjava.entity.User;
import com.example.crazyjava.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;



@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @GetMapping
    public Result<List<User>> users(){
        log.info("===== 访问了 /users 接口，当前时间：" + new java.util.Date() + " =====");
        log.info("Controller 层：接收到 /users 请求");
        List<User> result = userService.getUserList();
        log.info("Controller 层：返回数据 {}", result);
        return Result.success(result);
    }


    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable long id){
        log.info("Controller 层：根据 ID 查询用户: {}", id);
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    public Result<User> setUserById(@PathVariable Long id, @Valid @RequestBody User user){
        log.info("Controller 层：根据 ID 查询用户: {}", id);
        User newUser = userService.setUserById(id,user);
        return Result.success(newUser);
    }


    // ========== 新增：删除用户 ==========
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("Controller 层：删除用户 ID: {}", id);
        userService.deleteUserById(id);
        return Result.success("用户删除成功", null);
    }


    @PostMapping
    public Result<User> createUser(@Valid @RequestBody User user){
        log.info("新增user的入参 {}",user);
        User newUser = userService.createUser(user);
        log.info("新增成功{}",newUser);
        return Result.success("创建用户成功",newUser);
    }

}
