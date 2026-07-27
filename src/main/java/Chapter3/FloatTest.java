package Chapter3;

public class FloatTest {
    public static void main(String[] args) {
        float af = 5.2345556f; //浮点数默认是double类型，想用float需使用f/F后缀
        System.out.println(af);
        double a = 0.0;
        double c = Double.NEGATIVE_INFINITY;
        float d = Float.NEGATIVE_INFINITY;
        System.out.println(c==d); // 负无穷大相等
        System.out.println(a/a); // 0.0除以0.0得到NaN
        System.out.println(a/a==Float.NaN); // NaN 不等于 NaN
        System.out.println(6.0/0.0==555.0/0.0); // 正无穷相等，浮点数除以0得到无穷大，整数除以0会报错

    }}
