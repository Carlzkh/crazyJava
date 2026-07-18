package Chapter3;

public class NarrowConversion {
    public static void main(String[] args) {
        int a = 233;
        byte b = (byte)a;
        /*32位的int,0000000000000000011101001;
        变成8位的byte，前面的24位舍去：11101001；得到负数，需要求原码
        反码（值减1）：11101000；
        原码（首位不变，其余取反）：10010111，就是-23
        * */
        System.out.println(b);
        double c = 3.99;
        int d = (int)c;//强制类型转换直接截断小数部分，只要整数部分
        System.out.println(d);
        //float f = 5.6;这个会报错，5.6默认是double类型
    }
}
