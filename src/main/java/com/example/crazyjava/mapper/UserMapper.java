package com.example.crazyjava.mapper;


import com.example.crazyjava.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();

    User findById(long id);

    List<User> findByName(@Param("name") String name);

    int setById(User user);

    // ========== 新增：删除用户 ==========
    int deleteById(Long id);


    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}
