package Chapter8.Map;

import java.util.Map;
import java.util.TreeMap;

public class TreeMapTest {
    public static void main(String[] args) {
        // 4.3 TreeMap
        // 底层：红黑树
        // 优势：Key 自动排序（升序），支持范围查找
        // 劣势：增删改查 O(log n)
        // 场景：需要排序的 Key，或需要取最大/最小 Key
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Banana", 2);
        treeMap.put("Apple", 5);
        treeMap.put("Cherry", 3);
        System.out.println("TreeMap (按 Key 排序): " + treeMap);
        System.out.println("TreeMap 第一个 Key: " + ((TreeMap<String, Integer>) treeMap).firstKey() + "\n");
    }
}
