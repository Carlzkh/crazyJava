package Chapter7;

public class StringBuilderTest {
    public static void main(String[] args) {


        StringBuilder sb = new StringBuilder();
        //追加字符串
        sb.append("java");
        System.out.println("append："+sb);// sb = "java"
        // 插入
        sb.insert(0, "hello ");
        System.out.println("insert:"+sb);// sb="hello java"
        // 替换
        sb.replace(5, 6, ",");
        System.out.println("replace:"+sb);// sb="hello,java"
        // 删除
        sb.delete(5, 6);
        System.out.println("delete:"+sb);//sb="hellojava"
        //反转
        sb.reverse();// sb="avajolleh"
        System.out.println(sb);
        System.out.println(sb.length());// 输出9
        System.out.println(sb.capacity());//输出16,StringBuilder的容量
        // 改变StringBuilder的长度，将只保留前面部分
        sb.setLength(5);
        System.out.println(sb);// sb="avajo"
    }
}
