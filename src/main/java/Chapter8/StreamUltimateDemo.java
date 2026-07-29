package Chapter8;

import java.util.*;
import java.util.stream.Collectors;

public class StreamUltimateDemo {
    public static void main(String[] args) {
        List<User> users = Arrays.asList(
                new User(1L, "Alice", 25, Arrays.asList("读书", "跑步")),
                new User(2L, "Bob", 30, Arrays.asList("游泳", "打篮球")),
                new User(3L, "Charlie", 25, Arrays.asList("看电影", "读书")),
                new User(4L, "David", 30, Arrays.asList("跑步", "打篮球"))
        );

        // 1. toMap：转成 Map<id, name>
        Map<Long, String> idToName = users.stream()
                .collect(Collectors.toMap(User::id, User::name));
        System.out.println("toMap: " + idToName);

        // 2. groupingBy：按年龄分组
        Map<Integer, List<User>> byAge = users.stream()
                .collect(Collectors.groupingBy(User::age));
        System.out.println("分组后年龄25的用户: " + byAge.get(25));

        // 3. groupingBy + counting：按年龄分组统计人数
        Map<Integer, Long> countByAge = users.stream()
                .collect(Collectors.groupingBy(User::age, Collectors.counting()));
        System.out.println("各年龄人数: " + countByAge);

        // 4. partitioningBy：分成成年/未成年
        Map<Boolean, List<User>> byAdult = users.stream()
                .collect(Collectors.partitioningBy(u -> u.age() >= 18));
        System.out.println("成年人: " + byAdult.get(true));

        // 5. summingInt：年龄总和
        //int totalAge = users.stream().collect(Collectors.summingInt(User::getAge));需要装箱再拆箱，性能不好，idea提示改成下面的写法
        int totalAge = users.stream().mapToInt(User::age).sum();
        System.out.println("年龄总和: " + totalAge);

        // 6. joining：名字拼接
        String names = users.stream().map(User::name).collect(Collectors.joining(", "));
        System.out.println("名字列表: " + names);

        // 7. flatMap：所有爱好平铺
        List<String> allHobbies1 = users.stream().flatMap(u -> u.hobbies().stream()).distinct().collect(Collectors.toList());
        allHobbies1.add("1");
        System.out.println("所有爱好: " + allHobbies1);
        //idea提示改成下面这种，但是注意.toList() 返回的是一个不可变列表
        List<String> allHobbies = users.stream().flatMap(u->u.hobbies().stream()).distinct().toList();
        //allHobbies.add("1");.toList() 返回的是一个不可变列表，如果你尝试 list.add(...)，会抛出 UnsupportedOperationException。
        System.out.println("所有爱好: " + allHobbies);
    }
}

// 实体类（带 hobbies）
record User(Long id, String name, Integer age, List<String> hobbies) {}
