package Chapter7;

public class TestSB {
    // 静态变量，多线程共享
    //StringBuilder线程不安全，输出不确定
    static StringBuilder sb = new StringBuilder();

    //StringBuffer线程安全，输出确定，永远是10000
    // static StringBuffer sb = new StringBuffer();

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads= new Thread[10];
        for(int i=0; i<10;i++){
            threads[i]=new Thread(()->{
                //用sb.append("a".repeat(1000))代替下面的for循环会大大降低并发报错的概率，不利于我们理解线程不安全；并且它不能解决问题，只能降低问题出现的概率
                for(int j=0;j<1000;j++){
                    sb.append("a");
                }
            });
            threads[i].start();
        }
        // 等待所有线程执行完毕
        for(Thread t:threads){
            t.join();
        }
        System.out.println("最终长度："+sb.length());
    }
}
