package Chapter6;

@SuppressWarnings("InstantiationOfUtilityClass")
// 单例类，一个类只能创建一个实例
class Singleton {
    private static volatile Singleton instance;

    private Singleton() {
    }

    public static  Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

public class SingletonTest {
    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println(s1 == s2);
    }
}
