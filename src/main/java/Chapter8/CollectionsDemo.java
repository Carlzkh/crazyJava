package Chapter8;

import java.util.*;

public class CollectionsDemo {
    public static void main(String[] args) {
        // 1. sort + reverse + shuffle
        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9));
        Collections.sort(numbers);
        System.out.println("排序后: " + numbers);

        Collections.reverse(numbers);
        System.out.println("反转后: " + numbers);

        Collections.shuffle(numbers);
        System.out.println("打乱后: " + numbers);

        // 2. max/min
        System.out.println("最大值: " + Collections.max(numbers));
        System.out.println("最小值: " + Collections.min(numbers));

        // 3. frequency
        List<String> list = Arrays.asList("A", "B", "A", "C", "A");
        System.out.println("A 出现次数: " + Collections.frequency(list, "A"));

        // 4. addAll
        List<String> result = new ArrayList<>();
        Collections.addAll(result, "X", "Y", "Z");
        System.out.println("addAll 结果: " + result);

        // 5. emptyList（安全返回）
        List<String> empty = Collections.emptyList();
        System.out.println("空列表: " + empty);
        System.out.println("空列表是否为空: " + empty.isEmpty());

        // 6. singletonList
        List<String> single = Collections.singletonList("唯一");
        System.out.println("单元素列表: " + single);

        // 7. unmodifiableList
        List<String> original = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> readOnly = Collections.unmodifiableList(original);
        System.out.println("只读列表: " + readOnly);
        // readOnly.add("D"); // 取消注释会报错
    }
}
