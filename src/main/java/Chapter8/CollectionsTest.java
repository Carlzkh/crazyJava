package Chapter8;

import java.util.*;

public class CollectionsTest {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9));

        // 1. 排序
        Collections.sort(numbers); // [1, 2, 5, 8, 9]
        System.out.println("sort排序："+numbers);

        // 2. 反转
        Collections.reverse(numbers); // [9, 8, 5, 2, 1]
        System.out.println("reverse反转："+numbers);

        // 3. 打乱（洗牌）
        Collections.shuffle(numbers);
        System.out.println("shuffle打乱："+numbers);

        // 4. 取最大/最小值
        Integer max = Collections.max(numbers); // 9
        Integer min = Collections.min(numbers); // 1
        System.out.println("max:"+max+"; min:"+min);

        // 5. 二分查找（前提：先排序）
        Collections.sort(numbers);
        int index = Collections.binarySearch(numbers, 1); // 返回位置
        System.out.println(index);

        // 6. 空集合（避免返回 null 导致 NPE）
        List<String> emptyList = Collections.emptyList(); // 不可变的空列表
        Set<String> emptySet = Collections.emptySet();
        Map<String, String> emptyMap = Collections.emptyMap();

        // 7. 线程安全包装（把普通集合变成线程安全的）
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());

        //8.便捷方法
        List<Integer> numbers2 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

        // 删除所有偶数
        numbers2.removeIf(n -> n % 2 == 0); // [1, 3, 5]

        // 把所有元素乘以 2
        numbers2.replaceAll(n -> n * 2); // [2, 6, 10]

    }
}
