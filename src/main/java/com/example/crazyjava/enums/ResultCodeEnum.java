package com.example.crazyjava.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一错误码枚举
 * 格式：模块前缀 + 错误序号
 * 20000 - 成功
 * 40000 - 参数错误
 * 50000 - 系统错误
 * 60000 - 业务错误
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    // ===== 成功 =====
    SUCCESS(20000, "操作成功"),

    // ===== 参数错误 40000-40999 =====
    PARAM_IS_INVALID(40000, "参数无效"),
    PARAM_IS_BLANK(40001, "参数为空"),
    PARAM_IS_NULL(40002, "参数为null"),
    PARAM_TYPE_ERROR(40003, "参数类型错误"),
    DATE_RANGE_INVALID(40004, "开始日期不能大于结束日期"),
    GENDER_INVALID(40005, "性别只能传 MALE 或 FEMALE"),
    IDS_EMPTY(40006, "用户ID列表不能为空"),

    // ===== 业务错误 60000-60999 =====
    USER_NOT_FOUND(60000, "用户不存在"),
    USER_DELETE_FAILED(60001, "删除用户失败"),
    USER_UPDATE_FAILED(60002, "更新用户失败"),
    USER_CREATE_FAILED(60003, "创建用户失败"),
    USER_AlREADY_DELETED(60004, "用户已经被删除"),

    // ===== 系统错误 50000-50999 =====
    SYSTEM_ERROR(50000, "系统内部错误，请稍后重试"),
    DB_ERROR(50001, "数据库异常"),
    ;

    private final Integer code;
    private final String msg;
}