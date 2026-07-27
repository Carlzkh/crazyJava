package com.example.crazyjava.mapper;


import com.example.crazyjava.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("select * from user where id = #{id}")
    User findById(long id);

    @Update("update user set name = #{name}, age= #{age} where id = #{id}")
    int setById(User user);

    // ========== 新增：删除用户 ==========
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    @Insert("INSERT INTO user(name, age) VALUES(#{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}
