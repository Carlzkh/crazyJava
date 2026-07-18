package Chapter4;

public class BreakAndContinue {
    /**
     * break只能跳出当前循环，会继续上层循环，想要跳出上层循环需使用标签
     * continue只能跳出当前循环的剩余部分，会继续本层循环的后续循环次数，想要跳出上层循环需使用标签
     * */
    public static void main(String[] args) {
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println( "内层循环前" + j);
                if (j == 1) {
                    break outer;
                }     // 只跳出内层 j 循环
                System.out.println( "内层循环" + j);
            }
            System.out.println(i + "外层循环");
        }
// 输出：0,0  1,0  2,0   ← 外层 i 循环没受影响
    }
}
