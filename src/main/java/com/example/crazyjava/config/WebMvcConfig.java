package com.example.crazyjava.config;

import com.example.crazyjava.Gender;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置。
 * 注册 String -> Gender 转换器，让 @RequestParam Gender gender
 * 兼容两种传参：数字 code（0 / 1）、枚举名（MALE / FEMALE，大小写不敏感）。
 * 传入非法值时由 Gender.fromCode / fromValue 抛出 IllegalArgumentException，
 * 最终被 GlobalExceptionHandler 转为 400 响应。
 *
 * 说明：@JsonCreator 只作用于 @RequestBody（JSON 反序列化），
 * 对 @RequestParam 无效，因此这里需要单独注册 Converter。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToGenderConverter());
    }

    /**
     * String -> Gender 转换器：
     * - 空白字符串返回 null（表示不按性别过滤）；
     * - 纯数字（如 0 / 1）走 Gender.fromCode，按 code 取枚举；
     * - 非数字走 Gender.fromValue，按枚举名大小写不敏感匹配 MALE / FEMALE。
     */
    private static class StringToGenderConverter implements Converter<String, Gender> {
        @Override
        public Gender convert(String source) {
            String value = source == null ? null : source.trim();
            if (!StringUtils.hasText(value)) {
                return null;
            }
            // 兼容数字 code：0 / 1（复用 Gender.fromCode）
            try {
                return Gender.fromCode(Integer.parseInt(value));
            } catch (NumberFormatException notNumeric) {
                // 非数字：按枚举名匹配（大小写不敏感，复用 Gender.fromValue）
                return Gender.fromValue(value);
            }
        }
    }
}
