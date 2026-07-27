package Chapter8.Collection;
import java.util.*;

/**
 * 3. Queue / Deque 接口
 */
public class CollectionQueueTest {
    public static void main(String[] args) {
        // 3.1 PriorityQueue（优先级队列）
        // 底层：二叉堆（数组）
        // 优势：每次取出的都是优先级最高的元素（自然顺序或自定义比较器）
        // 劣势：不是 FIFO（先进先出），而是按优先级出列
        // 场景：任务调度、哈夫曼树、求 TopK
        Queue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(10);
        priorityQueue.add(5);
        priorityQueue.add(20);
        priorityQueue.add(1);
        System.out.println("PriorityQueue (按优先级出队): " + priorityQueue);
        System.out.println("PriorityQueue poll: " + priorityQueue.poll()); // 取出最小的 1
        System.out.println("PriorityQueue after poll: " + priorityQueue + "\n");

        // 3.2 ArrayDeque（高效双端队列）
        // 底层：循环数组
        // 优势：比 LinkedList 更快（内存连续，CPU缓存友好）
        // 劣势：不能存储 null
        // 场景：用来替代 Stack（LIFO）和 LinkedList（FIFO）
        Deque<String> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst("A"); // 头部插入
        arrayDeque.addLast("B");  // 尾部插入
        arrayDeque.addFirst("C");
        System.out.println("ArrayDeque (双端队列): " + arrayDeque);
        System.out.println("ArrayDeque 弹出头部: " + arrayDeque.removeFirst());
        System.out.println("ArrayDeque after removeFirst: " + arrayDeque + "\n");

        // 3.3 LinkedList（也是 Deque 的实现）
        // LinkedList 实现了 List 和 Deque，所以它既可以当 List，也可以当双端队列
        // 场景：需要频繁在两端插入删除，且偶尔需要随机访问
        Deque<String> linkedListAsDeque = new LinkedList<>();
        linkedListAsDeque.addFirst("X");
        linkedListAsDeque.addLast("Y");
        System.out.println("LinkedList as Deque: " + linkedListAsDeque);
        System.out.println("LinkedList 弹出尾部: " + linkedListAsDeque.removeLast() + "\n");
    }
}
