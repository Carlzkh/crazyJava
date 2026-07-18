package Chapter7;

import java.util.Calendar;

public class TestLenient {
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH,13);
        System.out.println(c.getTime());
        c.setLenient(false);
        //c.set(Calendar.MONTH,13);
        System.out.println(c.getTime());
    }
}
