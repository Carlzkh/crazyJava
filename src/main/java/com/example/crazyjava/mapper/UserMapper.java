package com.example.crazyjava.mapper;


import com.example.crazyjava.Gender;
import com.example.crazyjava.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();

    User findById(long id);
    User findById_(long id);

    List<User> findByName(@Param("name") String name);

    List<User> findByAge(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    List<User> searchUsers(@Param("name") String name,
                           @Param("minAge") Integer minAge,
                           @Param("maxAge") Integer maxAge,
                           @Param("gender") Gender gender,
                           @Param("startDate") String startDate,
                           @Param("endDate") String endDate);

    int setById(User user);

    // ========== 新增：删除用户 ==========
    int deleteById(Long id);
    int deleteByIds(@Param("ids") List<Long> ids);


    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}
