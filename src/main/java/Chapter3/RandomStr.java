package Chapter3;

public class RandomStr {
    public static void main(String[] args) {
        StringBuilder result = new StringBuilder();
        for (int i=0;i<6;i++){
            int intValue = (int)(Math.random()*26+97);
            result.append((char) intValue); // String是不可变对象，使用+拼接会增加消耗，浪费性能
        }
        System.out.println(result);

    }
}
