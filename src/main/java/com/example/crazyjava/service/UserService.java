package com.example.crazyjava.service;


import com.example.crazyjava.entity.User;
import com.example.crazyjava.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public List<User> getUserList(){
        return userRepository.findAll();
    }

    public User createUser(User user){
        log.info("Service 层：收到新增用户请求，用户数据: {}", user);
        // 调用 Repository 保存用户
        userRepository.save(user);
        log.info("Service 层：用户保存成功，生成 ID: {}", user.getId());
        return user;
    }
}
