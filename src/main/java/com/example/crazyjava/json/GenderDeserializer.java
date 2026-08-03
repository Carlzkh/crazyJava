package com.example.crazyjava.json;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.example.crazyjava.Gender;

import java.lang.reflect.Type;

/**
 * Fastjson 反序列化器：把 JSON 中的 gender 转成 {@link Gender}。
 * 兼容两种写法：
 * - 数字 code：0 -> MALE，1 -> FEMALE（复用 Gender.fromCode）；
 * - 字符串：MALE/FEMALE，大小写不敏感（复用 Gender.fromValue）。
 *
 * 生效方式：Gender 上的 @JSONType(deserializeUsing=...) 指向本类，
 * 同时在 WebMvcConfig 里用 ParserConfig 全局注册一次（双保险）。
 */
public class GenderDeserializer implements ObjectDeserializer {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            // 数字 code：0 -> MALE, 1 -> FEMALE
            return (T) Gender.fromCode(((Number) value).intValue());
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        // 字符串：按枚举名大小写不敏感匹配
        return (T) Gender.fromValue(text);
    }

    @Override
    public int getFastMatchToken() {
        // 0 表示不参与快速匹配，始终走本反序列化器
        return 0;
    }
}
