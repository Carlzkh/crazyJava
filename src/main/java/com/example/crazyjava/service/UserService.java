package com.example.crazyjava.service;


import com.example.crazyjava.common.exception.BusinessException;
import com.example.crazyjava.entity.User;
import com.example.crazyjava.repository.UserRepository;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("用户不存在在，id为{}", id);
            throw new BusinessException(404, "用户不存在，ID：" + id);
        }
        log.info("查到用户：{}", user);
        return user;
    }

    public User setUserById(long id, User updatedUser) {
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("用户不存在在，id为{}", id);
            throw new BusinessException(404, "用户不存在，ID：" + id);
        }
        // 2. 更新数据
        user.setName(updatedUser.getName());
        user.setAge(updatedUser.getAge());
        user.setGender(updatedUser.getGender());

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
        User user = userRepository.findById_(id);
        if (user == null) {
            log.warn("Service 层：用户 ID {} 不存在", id);
            throw new BusinessException(404, "用户不存在，ID：" + id);
        }
        User user1=userRepository.findById(id);
        if(user1==null){
            log.warn("Service 层：用户 ID {} 存在", id);
            throw new BusinessException(400, "用户已被删除，ID：" + id);
        }

        // 2. 执行删除
        int rows = userRepository.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(500, "删除失败，请重试");
        }
        log.info("Service 层：用户 ID {} 删除成功", id);
    }

    public User createUser(User user) {
        log.info("Service 层：收到新增用户请求，用户数据: {}", user);
        // 调用 Repository 保存用户
        userRepository.save(user);
        log.info("Service 层：用户保存成功，生成 ID: {}", user.getId());
        return user;
    }

    public PageInfo<User> getUserListByPage(int page, int size) {
        log.info("Service 层：分页查询，page={}, size={}", page, size);

        // 1. 开启分页（PageHelper 会自动拦截下一次 SQL 查询）
        PageHelper.startPage(page, size);

        // 2. 执行查询（仍然是查询所有，但 PageHelper 会自动加 LIMIT）
        List<User> userList = userRepository.findAll();

        // 3. 封装成 PageInfo（包含总记录数、总页数等分页信息）
        PageInfo<User> pageInfo = new PageInfo<>(userList);

        log.info("Service 层：查询到 {} 条数据，总记录数: {}", userList.size(), pageInfo.getTotal());
        return pageInfo;
    }

    public PageInfo<User> getUserListByPageAndName(int page, int size, String name) {
        log.info("Service条件分页查询，参数：page={},size={},name={}", page, size, name);
        PageHelper.startPage(page, size);
        List<User> userList;
        if (StringUtils.hasText(name)) {
            userList = userRepository.findByName(name);
        } else {
            userList = userRepository.findAll();
        }
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        log.info("Service 层：查询到 {} 条数据，总记录数: {}", userList.size(), pageInfo.getTotal());
        return pageInfo;
    }

    public List<User> searchByName(String name){
        return userRepository.findByName(name);
    }
    public List<User> searchByAge(int minAge,int maxAge){
        return userRepository.findByAge(minAge, maxAge);
    }
    // 综合搜索：名字 + 年龄范围
    public List<User> searchUsers(String name, Integer minAge, Integer maxAge,String gender) {
        log.info("Service 层：综合搜索，name={}, minAge={}, maxAge={}", name, minAge, maxAge);

        // 参数预处理（可选）
        // 如果 name 是空字符串，转为 null（让 Mapper 的 <if> 判断更干净）
        String cleanName = StringUtils.hasText(name) ? name.trim() : null;

        // 调用 Repository
        List<User> users = userRepository.searchUsers(cleanName, minAge, maxAge,gender);

        log.info("Service 层：搜索到 {} 条数据", users.size());
        return users;
    }
}
