package Chapter4.exec;

import java.util.Arrays;

public class NineNineTable {
    public static void main(String[] args) {
        for (int i=1;i<=9;i++){
            for(int j =1;j<=i;j++){
                System.out.print(i+"*"+j+"="+i*j);
                if (j!=i){
                    System.out.print(",");
                }
            }
            System.out.println();
        }

        for (int i=0;i<4;i++){
            String [] a=new String[2*i+1];
            Arrays.fill(a,"*");
            for(String b :a){
                System.out.print(b);
            }
            System.out.println();
            /*
            //String print = Arrays.toString(a);
            //System.out.println(print.substring(1, print.length()-1));
            */

        }

        for (int i = 1; i <= 4; i++) {
            // 打印空格：每行空格数 = 总行数 - 当前行号
            for (int j = 1; j <= 4 - i; j++) {
                System.out.print(" ");
            }
            // 打印星号：每行星号数 = 2 * 当前行号 - 1
            for (int k = 1; k <= 2 * i - 1; k++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
