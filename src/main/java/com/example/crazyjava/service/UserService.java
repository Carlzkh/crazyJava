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

    public User getUserById(long id){
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("用户不存在在，id为{}",id);
            throw new RuntimeException("用户不存在，ID："+id);
        }
        log.info("查到用户：{}",user);
        return user;
    }

    public User setUserById(long id,User updatedUser){
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("用户不存在在，id为{}",id);
            throw new RuntimeException("用户不存在，ID："+id);
        }
        // 2. 更新数据
        user.setName(updatedUser.getName());
        user.setAge(updatedUser.getAge());

        // 3. 执行更新
        int row = userRepository.setById(user);
        log.info("Service 层：更新后的用户: {}", row);

        // 4. 返回更新后的用户
        return user;
    }

    // ========== 新增：删除用户 ==========
    public void deleteUserById(Long id) {
        log.info("Service 层：删除用户 ID: {}", id);

        // 1. 检查用户是否存在
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("Service 层：用户 ID {} 不存在", id);
            throw new RuntimeException("用户不存在，ID: " + id);
        }

        // 2. 执行删除
        int rows = userRepository.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("删除失败，请重试");
        }
        log.info("Service 层：用户 ID {} 删除成功", id);
    }

    public User createUser(User user){
        log.info("Service 层：收到新增用户请求，用户数据: {}", user);
        // 调用 Repository 保存用户
        userRepository.save(user);
        log.info("Service 层：用户保存成功，生成 ID: {}", user.getId());
        return user;
    }
}
