package Chapter8.Map;

import java.util.Hashtable;
import java.util.Map;

public class HashtableTest {
    public static void main(String[] args) {
        // 4.4 Hashtable（过时，线程安全）
        // 底层：哈希表（与 HashMap 类似）
        // 优势：线程安全（方法上有 synchronized）
        // 劣势：性能差，不允许 null key/value
        // 场景：遗留系统
        Map<String, String> hashtable = new Hashtable<>();
        hashtable.put("A", "1");
        System.out.println("Hashtable (线程安全,过时): " + hashtable + "\n");
    }
}
