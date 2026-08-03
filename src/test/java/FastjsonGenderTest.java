import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.example.crazyjava.Gender;
import com.example.crazyjava.entity.User;
import com.example.crazyjava.json.GenderDeserializer;
import com.example.crazyjava.json.GenderSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 Fastjson 对 Gender 的反/序列化行为。
 * - 反序列化："male"(小写) -> MALE 是 Fastjson 默认做不到的，证明 GenderDeserializer 生效；
 * - 序列化：固定输出小写 "male"/"female"（而非枚举名 MALE/FEMALE），证明 GenderSerializer 生效；
 *   且序列化后再反序列化可还原（请求/响应对称、不泄漏枚举名）。
 */
public class FastjsonGenderTest {

    @BeforeAll
    static void register() {
        ParserConfig.getGlobalInstance().putDeserializer(Gender.class, new GenderDeserializer());
        SerializeConfig.getGlobalInstance().put(Gender.class, new GenderSerializer());
    }

    @Test
    void deserializeByCode() {
        // 用 fromCode 作为期望值，与枚举 code 的具体取值解耦
        assertEquals(Gender.fromCode(0), JSON.parseObject("{\"gender\":0}", User.class).getGender());
        assertEquals(Gender.fromCode(1), JSON.parseObject("{\"gender\":1}", User.class).getGender());
    }

    @Test
    void deserializeByNameCaseInsensitive() {
        // 小写 male：Fastjson 默认会失败，只有自定义反序列化器能命中 -> 判别用例
        assertEquals(Gender.MALE, JSON.parseObject("{\"gender\":\"male\"}", User.class).getGender());
        assertEquals(Gender.FEMALE, JSON.parseObject("{\"gender\":\"FEMALE\"}", User.class).getGender());
    }

    @Test
    void serializeByName() {
        User u = new User();
        u.setGender(Gender.FEMALE);
        assertTrue(JSON.toJSONString(u).contains("\"gender\":\"female\""));
        u.setGender(Gender.MALE);
        assertTrue(JSON.toJSONString(u).contains("\"gender\":\"male\""));
    }

    @Test
    void roundTrip() {
        // 序列化(POJO 字段)出小写串，再反序列化回来，应得到同一枚举（请求/响应对称、不泄漏枚举名）
        for (Gender g : Gender.values()) {
            User u = new User();
            u.setGender(g);
            String json = JSON.toJSONString(u);
            assertEquals(g, JSON.parseObject(json, User.class).getGender());
        }
    }
}
