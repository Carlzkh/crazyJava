package Chapter3;

public class AutoConversion {
    /**
     * 自动转换有两条路径：
     * char-->int-->long-->float-->double
     * byte-->short-->int-->long-->float-->double
     * */
    public static void main(String[] args) {
        int a = 6;
        float f =a;
        System.out.println(f);//int会自动转换成float

    }
}
