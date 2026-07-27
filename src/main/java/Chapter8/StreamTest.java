package Chapter8;

import java.util.*;
import java.util.stream.Collectors;

public class StreamTest {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Tom", "Jerry", "Alice", "Bob", "Tom", "Charlie");

        // 1. 筛选：名字长度 > 3
        List<String> longNames = names.stream().filter(name -> name.length() > 3).toList();
        System.out.println("长度>3: " + longNames);
        // 输出: [Jerry, Alice, Charlie]

        // 2. 转换：全部转大写
        List<String> upperNames = names.stream().map(String::toUpperCase).toList();
        System.out.println("转大写: " + upperNames);
        // 输出: [TOM, JERRY, ALICE, BOB, TOM, CHARLIE]

        // 3. 去重 + 排序 + 取前3
        List<String> top3 = names.stream()
                .distinct()        // 去重
                .sorted()          // 排序
                .limit(3).toList();
        System.out.println("去重排序取前3: " + top3);
        // 输出: [Alice, Bob, Charlie]

        // 4. 统计：以 "A" 开头的有多少个
        long count = names.stream()
                .filter(name -> name.startsWith("A"))
                .count();
        System.out.println("以A开头的个数: " + count);
        // 输出: 1

        // 5. 判断：是否全部长度 > 2
        boolean allMatch = names.stream().allMatch(name -> name.length() > 2);
        System.out.println("全部长度>2: " + allMatch);
        // 输出: true
    }
}