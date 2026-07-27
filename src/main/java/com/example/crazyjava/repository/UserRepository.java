package com.example.crazyjava.repository;


import com.example.crazyjava.entity.User;
import com.example.crazyjava.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;
    public List<User> findAll(){
        return userMapper.findAll();
    }

    public void save(User user) {
        userMapper.insert(user);
    }
}
