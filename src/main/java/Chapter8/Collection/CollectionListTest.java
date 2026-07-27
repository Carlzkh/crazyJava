package Chapter8.Collection;
import java.util.*;

/**
 * 1. List 接口：有序、可重复、有索引
 */
public class CollectionListTest {
    public static void main(String[] args) {
        // 1.1 ArrayList
        // 底层：动态数组
        // 优势：随机访问（get/set）速度极快 O(1)
        // 劣势：中间插入/删除慢 O(n)，因为需要移动元素
        // 场景：查询多、改多、增删多在末尾
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Apple"); // 允许重复
        arrayList.set(1,"set");//修改参数1位置的元素为参数2
        System.out.println(arrayList.get(0));//获取参数位置的元素
        arrayList.add(1,"add");//在参数1位置新增参数2，后续元素后移一位
        //List<Integer> addArrayList = new ArrayList<Integer>(Arrays.asList(1,2,3));
        List<String> addArrayList = new ArrayList<String>(Arrays.asList("1","2","3"));
        arrayList.addAll(addArrayList);//添加一个数组的所有元素
        System.out.println("ArrayList (有序,可重复): " + arrayList);
        System.out.println("ArrayList 取第二个元素: " + arrayList.get(1) + "\n");

        // 1.2 LinkedList
        // 底层：双向链表
        // 优势：在头部/中间插入/删除极快 O(1)（只要拿到节点）
        // 劣势：随机访问慢 O(n)，需要从头遍历
        // 场景：频繁在头部/中间插入删除，如消息队列、LRU缓存
        List<String> linkedList = new LinkedList<>();
        linkedList.add("Dog");
        linkedList.add("Cat");
        linkedList.add(0, "Bird"); // 在头部插入，效率高
        System.out.println(linkedList.stream().toList());
        System.out.println("LinkedList (有序,可重复): " + linkedList);
        System.out.println("LinkedList 取第一个元素: " + linkedList.get(0) + "\n");

        // 1.3 Vector（过时，线程安全，但性能差）
        // 底层：动态数组（与ArrayList几乎相同）
        // 优势：线程安全（方法上都有 synchronized）
        // 劣势：性能差，现在使用 Collections.synchronizedList 代替
        // 场景：遗留系统，不建议在新项目中使用
        List<String> vector = new Vector<>();
        vector.add("A");
        vector.add("B");
        System.out.println("Vector (线程安全,过时): " + vector + "\n");

        // 1.4 Stack（过时，LIFO 栈）
        // 底层：继承 Vector
        // 特点：后进先出 (LIFO)
        // 优势：push/pop/peek 操作语义清晰
        // 劣势：继承了 Vector，不推荐，建议使用 ArrayDeque
        // 场景：方法调用栈、撤销操作、回溯算法
        Stack<String> stack = new Stack<>();
        stack.push("First");
        stack.push("Second");
        System.out.println("Stack (LIFO): " + stack);
        System.out.println("Stack pop: " + stack.pop()); // 弹出 Second
        System.out.println("Stack after pop: " + stack + "\n");
    }
}
