package com.example.crazyjava.common;


import com.example.crazyjava.enums.ResultCodeEnum;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    // 私有构造，强制使用静态方法创建
    private Result() {}

    // ===== 成功响应 =====
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // ===== 失败响应 =====
    public static <T> Result<T> error(String msg) {
        return error(ResultCodeEnum.SUCCESS.getCode(),msg);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    // ===== 失败（使用错误码枚举） =====
    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMsg(resultCodeEnum.getMsg());
        return result;
    }

    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum, String extraMsg) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMsg(resultCodeEnum.getMsg() + ": " + extraMsg);
        return result;
    }



}