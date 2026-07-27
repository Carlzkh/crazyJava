package Chapter8.Map;
import java.util.*;
public class HashMapTest {
    public static void main(String[] args) {
        // 4.1 HashMap（最常用）
        // 底层：哈希表（数组 + 链表 + 红黑树）
        // 优势：增删改查 O(1)，性能极高
        // 劣势：无序
        // 场景：缓存、数据字典、快速查找
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Apple", 5);
        hashMap.put("Banana", 3);
        hashMap.put("Apple", 10); // 覆盖旧值
        System.out.println("HashMap (key-value): " + hashMap);
        System.out.println("HashMap 取 Apple 的值: " + hashMap.get("Apple") + "\n");
    }
}
