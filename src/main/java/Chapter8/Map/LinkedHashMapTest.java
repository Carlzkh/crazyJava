package Chapter8.Map;
import java.util.*;

public class LinkedHashMapTest {
    public static void main(String[] args) {
        // 4.2 LinkedHashMap
        // 底层：哈希表 + 双向链表
        // 优势：保持了插入顺序（或访问顺序，取决于构造参数）
        // 劣势：比 HashMap 稍占内存
        // 场景：需要按插入顺序遍历，或实现 LRU 缓存
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("One", 1);
        linkedHashMap.put("Two", 2);
        linkedHashMap.put("Three", 3);
        System.out.println("LinkedHashMap (按插入顺序): " + linkedHashMap);
        // 下面演示 LRU 缓存（访问顺序）
        Map<String, Integer> lruCache = new LinkedHashMap<>(16, 0.75f, true);
        lruCache.put("A", 1);
        lruCache.put("B", 2);
        lruCache.put("C", 3);
        lruCache.get("A"); // 访问 A，A 会变成最近访问的
        System.out.println("LRU Cache (按访问顺序): " + lruCache + "\n");
    }
}
