package Chapter3;

public class PrimitiveAndString {
    public static void main(String[] args){
        //String str1 = 5;错误代码
        String str2 = 3.5f + "";
        System.out.println(str2);
        System.out.println(3+4+"Hello"); // 顺序影响结果
        System.out.println("Hello"+3+4);

    }
}
