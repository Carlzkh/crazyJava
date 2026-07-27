package Chapter8;

import java.util.Arrays;
import java.util.List;

public class LambdaTest {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5);
        //for循环
        for(Integer num:numbers){
            System.out.println(num);
        }
        //lambda
        numbers.forEach(num-> System.out.println(num));
        //方法引用(::)
        numbers.forEach(System.out::println);
    }
}
