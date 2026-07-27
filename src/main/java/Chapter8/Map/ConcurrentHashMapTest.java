package Chapter8.Map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {
    public static void main(String[] args) {
        // 4.5 ConcurrentHashMap（高并发首选）
        // 底层：分段锁 / CAS + synchronized (JDK 1.8)
        // 优势：高并发下性能极佳
        // 劣势：单线程下比 HashMap 慢一点点
        // 场景：多线程环境下操作 Map
        Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("K1", "V1");
        concurrentHashMap.put("K2", "V2");
        System.out.println("ConcurrentHashMap (高并发): " + concurrentHashMap);
    }
}
