package com.example.crazyjava.entity;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

import com.example.crazyjava.Gender;
import lombok.Data;

@Data
public class User {
    private Long id;

    @NotNull(message = "{user.name.notnull}")
    @Size(min = 1, max = 50, message = "姓名长度必须在1-50之间")
    private String name;

    @NotNull(message = "{user.age.notnull}")
    @Min(value = 0, message = "{user.age.min}")
    @Max(value = 150, message = "{user.age.max}")
    private Integer age;


    private Gender gender;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Address> addresses;
}
