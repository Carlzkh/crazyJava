import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class DateTimeTest {
    @Test
    public void tst11(){
        Date srcDateTime = DateUtil.parseDateTime("2026-05-26 23:00:00");
        Date dstDateTime = DateUtil.parseDateTime("2026-05-27 00:00:00");
        long between = DateUtil.between(srcDateTime, dstDateTime, DateUnit.DAY);
        between = DateUtil.betweenDay(srcDateTime, dstDateTime, true);
        System.out.println(between);
        System.out.println("between" != "");
    }
}

