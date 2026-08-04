package com.example.crazyjava.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.example.crazyjava.Gender;
import com.example.crazyjava.json.GenderDeserializer;
import com.example.crazyjava.json.GenderSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Web MVC 配置。
 * 注册 String -> Gender 转换器，让 @RequestParam Gender gender
 * 兼容两种传参：数字 code（0 / 1）、枚举名（MALE / FEMALE，大小写不敏感）。
 * 传入非法值时由 Gender.fromCode / fromValue 抛出 IllegalArgumentException，
 * 最终被 GlobalExceptionHandler 转为 400 响应。
 *
 * 说明：JSON 读写改用 Fastjson（见 extendMessageConverters）；
 * @RequestParam 的 String->Gender 仍走本类的 Converter，与 Fastjson 无关。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToGenderConverter());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 全局注册 Gender 自定义反序列化器（兼容 0/1 与大小写不敏感的 MALE/FEMALE）
        ParserConfig.getGlobalInstance().putDeserializer(Gender.class, new GenderDeserializer());
        // 全局注册 Gender 自定义序列化器（输出稳定小写串 male/female，不再泄漏枚举常量名）
        SerializeConfig.getGlobalInstance().put(Gender.class, new GenderSerializer());
        // 用 Fastjson 替换默认的 JSON 消息转换器（放最前，优先处理 application/json）
        // 注意：FastJsonHttpMessageConverter 默认字符集取系统默认（Windows 上常为 GBK），
        // 不显式指定 UTF-8 会导致响应中文乱码。这里同时固定写出字符集与 Content-Type 的 charset。
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        // 1. 创建一个 FastJsonConfig 配置对象
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        // 2. 在配置对象中设置字符集为 UTF-8
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);

        // 3. （可选）在这里还可以设置日期格式、序列化特性等其他配置
        // fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);

        // 4. 将配置对象设置到消息转换器中
        converter.setFastJsonConfig(fastJsonConfig);
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        converters.add(0, converter);
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
            String value = source.trim();
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
