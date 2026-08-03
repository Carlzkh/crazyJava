package com.example.crazyjava.json;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.example.crazyjava.Gender;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Fastjson 序列化器：把 {@link Gender} 写成稳定的小写字符串 "male" / "female"，
 * 与 {@link GenderDeserializer} 对称。
 * <p>
 * 默认行为会把枚举序列化成 {@code name()}（"MALE"/"FEMALE"），相当于把 Java 枚举常量名
 * 直接泄漏到 API 契约里——一旦重命名常量或更换 JSON 库，线上值就会变。
 * 这里改用与常量名解耦的小写固定串，保证线上契约稳定。
 * <p>
 * 生效方式：在 WebMvcConfig 里用 SerializeConfig 全局注册一次。
 */
public class GenderSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
            throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }
        Gender gender = (Gender) object;
        String wire;
        switch (gender) {
            case MALE:
                wire = "male";
                break;
            case FEMALE:
                wire = "female";
                break;
            default:
                throw new IOException("未知的 Gender 枚举值: " + gender);
        }
        serializer.write(wire);
    }
}
