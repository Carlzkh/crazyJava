package Chapter6;
record Person(String name) {

    public String toString() {
        return "这是一个人，名字是" + name;
    }
}
public class PrintObject {
    public static void main(String[] args) {
        Person p = new Person("孙悟空");
        System.out.println(p);
        System.out.println(p.toString());
    }
}
