package Chapter7;

public class TestString {
    public static void main(String[] args) {
        String s = new String("fkit.org");
        System.out.println("s.charAt(1): "+s.charAt(1));

        String s1 = new String("abcdefghijklmn");
        String s2 = new String("abcdefghijjb");
        String s3 = new String("abcdefghi");
        System.out.println("s.compareTo(相同返回0): "+s1.compareTo(s1));
        System.out.println("s.compareTo(返回第一个不相同字符的asscii差值): "+s1.compareTo(s2));
        System.out.println("s.compareTo(子串，返回两个字符串的长度差): "+s1.compareTo(s3));

        String s4 = new String("www.fkit.org");
        String s5 = new String("it");
        System.out.println("s4.indexOf(r字符第一次出现的位置)"+s4.indexOf('r'));
        System.out.println("s4.indexOf(从第2位开始，r字符第一次出现的位置)"+s4.indexOf('r',2));
        System.out.println("s4.indexOf(s5字符串第一次出现的位置)"+s4.indexOf(s5));
        System.out.println("s4.indexOf()"+s4.indexOf('w',0));


    }

}
