package com.example.crazyjava;

/**
 * 性别枚举：数据库存 code（0/1）；JSON 可传 code（0/1）或枚举名（MALE/FEMALE，大小写不敏感）。
 * 反序列化由 GenderDeserializer 处理（在 WebMvcConfig 中用 ParserConfig 全局注册）。
 */
public enum Gender {
    MALE(1, "男"),
    FEMALE(0, "女");

    private final int code;
    private final String desc;

    Gender(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    /** 按数字 code 取枚举（1 -> MALE, 0 -> FEMALE），非法值抛异常 */
    public static Gender fromCode(int code) {
        for (Gender g : values()) {
            if (g.code == code) return g;
        }
        throw new IllegalArgumentException("性别只能传 0 或 1");
    }

    /** 按枚举名取枚举（大小写不敏感），null 返回 null，非法值抛异常 */
    public static Gender fromValue(String value) {
        if (value == null) return null;
        for (Gender g : Gender.values()) {
            if (g.name().equalsIgnoreCase(value)) {
                return g;
            }
        }
        throw new IllegalArgumentException("性别只能传 MALE 或 FEMALE");
    }
}
