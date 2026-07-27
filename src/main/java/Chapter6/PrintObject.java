package Chapter6;

record Person(String name) {
    public String toString() {
        return "这是一个人，名字是" + name;
    }
}

class Person3 {
    private final String name;
    public Person3(String name){
        this.name =name;
    }

    public String toString() {
        return "这是一个人，名字是" + name;
    }
}

public class PrintObject {
    public static void main(String[] args) {
        Person p = new Person("孙悟空");
        System.out.println(p.name());
        System.out.println(p.toString());

        Person3 p3 = new Person3("孙悟空");
        System.out.println(p3);//没有p3.name()方法
        System.out.println(p3.toString());
    }
}
