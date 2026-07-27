package com.example.crazyjava.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class User {
    private Long id;

    @NotBlank(message="姓名不能为空")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 0,message = "年龄不能小于0")
    private Integer age;
}
