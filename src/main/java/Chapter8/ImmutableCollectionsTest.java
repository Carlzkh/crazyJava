package Chapter8;

import java.util.List;
import java.util.Map;
import java.util.Set;

//不可变集合
public class ImmutableCollectionsTest {
    public static void main(String[] args) {
        // 不可变集合：不能增删改
        List<String> fixedList = List.of("A", "B", "C");
        Set<Integer> fixedSet = Set.of(1, 2, 3);
        Map<String, Integer> fixedMap = Map.of("A", 1, "B", 2);

        //fixedList.add("D"); // ❌ 运行时抛出 UnsupportedOperationException
    }
}
