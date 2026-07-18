package Chapter7;

import java.util.Calendar;

public class TestLazy {
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.set(2026, Calendar.AUGUST,31);
        c.set(Calendar.MONTH,8);
        //System.out.println(c.getTime());
        c.set(Calendar.DATE,5);
        System.out.println(c.getTime());
    }
}
