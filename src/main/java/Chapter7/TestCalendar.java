package Chapter7;

import java.util.Calendar;

public class TestCalendar {
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        System.out.println(c.get(Calendar.YEAR));
        System.out.println(c.get(Calendar.MONTH));
        System.out.println(c.get(Calendar.DATE));

        c.set(2026, Calendar.AUGUST, 31);
        System.out.println(c.getTime());
        c.add(2,8);
        System.out.println("add会改变上级，年会变成2027："+c.getTime());
        c.set(2026, Calendar.AUGUST, 31);
        c.roll(2,8);
        System.out.println("roll不会改变上级，年是2026："+c.getTime());

        c.set(2026, Calendar.AUGUST, 31);
        System.out.println(c.getTime());
        c.add(2,6);
        System.out.println("add会改变下级，日会变成28："+c.getTime());
        c.set(2026, Calendar.AUGUST, 31);
        c.roll(2,6);
        System.out.println("roll会改变下级，日会变成28："+c.getTime().toString());
    }
}
