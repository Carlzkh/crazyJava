package com.example.crazyjava;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE(0, "男"),
    FEMALE(1, "女");

    private final int code;
    private final String desc;

    Gender(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    @JsonCreator
    public static Gender fromCode(int code) {
        for (Gender g : values()) {
            if (g.code == code) return g;
        }
        throw new IllegalArgumentException("性别只能传 0 或 1");
    }

    @JsonCreator
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
