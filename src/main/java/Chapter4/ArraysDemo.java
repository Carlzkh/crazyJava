package Chapter4;

import java.util.Arrays;
import java.util.List;

public class ArraysDemo {
    public static void main(String[] args) {
        // 1. toString
        int[] arr = {5, 2, 8, 1, 9};
        System.out.println("原数组: " + Arrays.toString(arr));

        // 2. sort
        Arrays.sort(arr);
        System.out.println("排序后: " + Arrays.toString(arr));

        // 3. binarySearch
        int index = Arrays.binarySearch(arr, 8);
        System.out.println("8 的位置: " + index);

        // 4. copyOf
        int[] copy = Arrays.copyOf(arr, 3);
        System.out.println("前3个元素: " + Arrays.toString(copy));

        // 5. equals
        int[] arr2 = {1, 2, 5, 8, 9};
        System.out.println("arr 和 arr2 是否相等: " + Arrays.equals(arr, arr2));

        // 6. asList
        List<Integer> list = Arrays.asList(1, 2, 3);
        System.out.println("数组转 List: " + list);

        // 7. fill
        int[] filled = new int[5];
        Arrays.fill(filled, 100);
        System.out.println("填充后: " + Arrays.toString(filled));


        int[] arr4 = {1, 2, 5, 8, 9};
        int[] arr5 = {1, 5, 2, 8, 9};
        System.out.println("arr4 和 arr5 是否相等: " + Arrays.equals(arr4, arr5));

    }
}