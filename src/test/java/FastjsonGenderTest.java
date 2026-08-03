import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.example.crazyjava.Gender;
import com.example.crazyjava.entity.User;
import com.example.crazyjava.json.GenderDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 Fastjson 对 Gender 的反/序列化行为。
 * 其中 "male"(小写) -> MALE 是 Fastjson 默认做不到的，能证明 GenderDeserializer 确实生效。
 */
public class FastjsonGenderTest {

    @BeforeAll
    static void register() {
        ParserConfig.getGlobalInstance().putDeserializer(Gender.class, new GenderDeserializer());
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
        String json = JSON.toJSONString(u);
        assertTrue(json.contains("\"gender\":\"FEMALE\""), json);
    }
}
