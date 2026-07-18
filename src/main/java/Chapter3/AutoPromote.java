package Chapter3;

public class AutoPromote {
    /**
     * 自动提升类型，byte、short、char类型都会自动提升为int类型;
     * byte/short/char 节省内存，int 提升保证运算安全。它们解决的是不同的问题。
     * 比如：byte a = 126; byte b = 23; byte c = a+b;如果不报错，可能会溢出
     */
    public static void main(String[] args) {
        short a = 5;
        //short b = a +2;此语句错误，因为a被自动提升为int类型了


        System.out.println("Hello"+'a'+7);
        System.out.println('a'+7+"Hello");//'a'被提升为int了，结果同PrimitiveAndString
        char letter = 'A';      // 存的是 Unicode 码点 65
        System.out.println(letter);
        int code = letter;       // 自动提升为 int → 65
        System.out.println(code);
        char next = (char)(letter + 1);  // 'B'
        System.out.println(next);
    }
}
