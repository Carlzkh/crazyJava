package Chapter8.Collection;
import java.util.*;

/**
 * 2. Set 接口：不允许重复元素 (通过 equals() 和 hashCode() 判断)
 */
public class CollectionSetTest {
    public static void main(String[] args) {
        // 2.1 HashSet
        // 底层：HashMap（数组+链表+红黑树）
        // 优势：添加、删除、查找 O(1)，性能最高
        // 劣势：元素完全无序（不保证任何顺序）
        // 场景：去重、快速查找，且不关心顺序
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Java");
        hashSet.add("Python");
        hashSet.add("Java"); // 重复元素，不会被添加
        System.out.println("HashSet (无序,不重复): " + hashSet + "\n");

        // 2.2 LinkedHashSet
        // 底层：LinkedHashMap（哈希表 + 双向链表）
        // 优势：保留了元素的插入顺序，且查找快 O(1)
        // 劣势：比 HashSet 多维护一个链表，稍微占用更多内存
        // 场景：需要去重，同时需要保持元素的插入顺序
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("One");
        linkedHashSet.add("Two");
        linkedHashSet.add("Three");
        linkedHashSet.add("One"); // 重复，不会被添加
        System.out.println("LinkedHashSet (按插入顺序,不重复): " + linkedHashSet + "\n");

        // 2.3 TreeSet
        // 底层：TreeMap（红黑树）
        // 优势：元素会自动排序（升序），支持范围查找
        // 劣势：添加、删除、查找 O(log n)，比 HashSet 慢
        // 场景：需要对元素进行排序，或需要取最大/最小值
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(5);
        treeSet.add(1);
        treeSet.add(3);
        treeSet.add(1); // 重复，不会被添加
        System.out.println("TreeSet (排序,不重复): " + treeSet);
        // 因为 TreeSet 实现了 NavigableSet，可以直接获取最小/最大值
        System.out.println("TreeSet 最小值: " + ((TreeSet<Integer>) treeSet).first());
        System.out.println("TreeSet 最大值: " + ((TreeSet<Integer>) treeSet).last() + "\n");
    }
}
