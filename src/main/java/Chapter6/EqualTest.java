package Chapter6;

public class EqualTest {
    /**
     * 对于==，如果是数值类型，只要值相等就返回true；如果是引用类型，只有它们指向同一个对象才相等
     *
     * */
    public static void main(String[] args) {
        int it = 65;
        float f1 = 65.0f;
        char ch = 'A';
        System.out.println(it==f1);

        System.out.println(it==ch); // 数值类型，值相等全部返回true

        String str1 = "Hello";
        String str2 = "Hello";
        System.out.println(str1==str2);//引用类型指向同一个对象，返回true
        String str3 = new String("Hello");
        String str4 = new String("Hello");
        System.out.println(str3==str4);//引用类型指向不同的对象，返回false

        String s1 = "疯狂Java";
        String s2 = "疯狂";
        String s3 = "Java";
        String s4 = "疯狂"+"Java";
        String s5 = "疯"+"狂"+"Java";
        String s6 =s2+s3;//编译时不确定，不能引用常量池的字符串，因此是个新对象
        String s7=new String("疯狂Java");
        System.out.println("=="+(s1==s4));
        System.out.println("equals"+s1.equals(s4));
        System.out.println(s1==s5);
        System.out.println(s1==s6);
        System.out.println("s6 equals:"+s1.equals(s6));
        System.out.println(s1==s7);
        System.out.println("s7 equals:"+s1.equals(s7));
    }
}
