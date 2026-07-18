import java.math.BigDecimal;

public class Test {

    public static void main(String[] args) {
        // 99.45 在内存中实际存储的是无限循环的截断版本
        double d = 99.45;

        // 打印实际存储的值
        System.out.println(new BigDecimal(d));
        // 输出：99.449999999999988631316227838397025177001953125

        // 这个值 ≈ 99.45 - 1.1368683772161603E-14
        // 误差来自截断无限循环小数
    }

}
