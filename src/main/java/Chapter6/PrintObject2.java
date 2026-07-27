package Chapter6;
class Person2{
    private String name;
    public Person2(String name){
        this.name = name;
    }
}
public class PrintObject2 {
    public static void main(String[] args) {
        Person2 p = new Person2("孙悟空");
        System.out.println(p);
    }
}
