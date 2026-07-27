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
    public User findById(long id){
        return userMapper.findById(id);
    }
    public int setById(User user){
        return userMapper.setById(user);
    }

    // ========== 新增：删除用户 ==========
    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }

    public void save(User user) {
        userMapper.insert(user);
    }
}
