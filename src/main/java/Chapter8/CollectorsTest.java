package Chapter8;

import java.util.*;
import java.util.stream.Collectors;

public class CollectorsTest {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
                new Person("Alice", 25),
                new Person("Bob", 30),
                new Person("Charlie", 25),
                new Person("David", 30)
        );

        // 1. 分组 (Map<年龄, List<人>>)
        Map<Integer, List<Person>> byAge = people.stream().collect(Collectors.groupingBy(Person::getAge));
        System.out.println(byAge);

        // 2. 分组 + 计数 (Map<年龄, 人数>)
        Map<Integer, Long> countByAge = people.stream().collect(Collectors.groupingBy(Person::getAge, Collectors.counting()));
        System.out.println(countByAge);

        // 3. 字符串拼接
        String names = people.stream().map(Person::getName).collect(Collectors.joining(", ")); // "Alice, Bob, Charlie, David"
        System.out.println(names);

        // 4. 求和
        int sumAge = people.stream().mapToInt(Person::getAge).sum();
        //int sumAge = people.stream().collect(Collectors.summingInt(Person::getAge));
        System.out.println(sumAge);

        // 5. 转成 Map (key=姓名, value=年龄)
        Map<String, Integer> nameToAge = people.stream()
                .collect(Collectors.toMap(
                        Person::getName,   // key
                        Person::getAge,    // value
                        (a, b) -> a        // 如果有重复key，保留第一个
                ));
        System.out.println(nameToAge);
    }
}

class Person {
    private final String name;
    private final int age;
    public Person(String name, int age) { this.name = name; this.age = age; }
    public String getName() { return name; }
    public int getAge() { return age; }
}
