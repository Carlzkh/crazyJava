package com.example.crazyjava.repository;


import com.example.crazyjava.Gender;
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

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public List<User> findByName(String name) {
        return userMapper.findByName(name);
    }

    public List<User> findByAge(int minAge, int maxAge) {
        return userMapper.findByAge(minAge, maxAge);
    }

    public List<User> searchUsers(String name, Integer minAge, Integer maxAge, Gender gender, String startDate, String endDate) {
        return userMapper.searchUsers(name, minAge, maxAge, gender, startDate, endDate);
    }

    public User findById(long id) {
        return userMapper.findById(id);
    }

    public User findById_(long id) {
        return userMapper.findById_(id);
    }

    public int setById(User user) {
        return userMapper.setById(user);
    }

    // ========== 新增：删除用户 ==========
    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }

    public int deleteByIds(List<Long> ids) {
        return userMapper.deleteByIds(ids);
    }


    public void save(User user) {
        userMapper.insert(user);
    }
}
