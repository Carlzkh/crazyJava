package Chapter5;

import java.util.Arrays;

public class Overload {
    public void test(){
        System.out.println("无参数");
    }
    public void test(String ... messages){
        System.out.println("可变长参数："+Arrays.toString(messages));
        int i=0;
        for(String msg:messages){
            i++;
            System.out.println("第"+i+"个可变长参数："+ msg);}
    }

    public void test(String msg){
        System.out.println("有参数："+msg);
    }


    public static void main(String[] args) {
        Overload q = new Overload();
        q.test();
        q.test("呵呵");
        q.test(new String[]{"aa"});
        q.test("he1","he2");
    }
}
