package com.example.crazyjava.exception;

import com.example.crazyjava.enums.ResultCodeEnum;
import lombok.Getter;

/**
 * 业务异常类
 * 用于在 Service 层抛出明确的业务错误，由全局异常处理器统一处理
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;
    private final String msg;

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMsg());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
    }

    public BusinessException(ResultCodeEnum resultCodeEnum, String extraMsg) {
        super(resultCodeEnum.getMsg() + ": " + extraMsg);
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg() + ": " + extraMsg;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}