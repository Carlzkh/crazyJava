package Chapter3;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class BaseDataType {
    /**
     * 数值型：byte、short、int、long，分别是1、2、4、8个字节，long类型的数字后需要加l\L标识
     * 字符型：char，也可以算整数型，2个字节
     * 浮点型：float、double，分别是4、8个字节
     * 布尔型：boolean
     * */
    public static void main(String[] args) {
        long bigValue = 999999999999999999L; //数字太大，不加L，会报错
        System.out.println(bigValue);
        BaseDataType b = new BaseDataType();
        b.tst11();
    }


    public void tst11(){
        Date srcDateTime = DateUtil.parseDateTime("2026-05-26 17:00:00");
        Date dstDateTime = DateUtil.parseDateTime("2026-05-27 01:00:00");
        long between = DateUtil.between(srcDateTime, dstDateTime, DateUnit.DAY);
        between = DateUtil.betweenDay(srcDateTime, dstDateTime, true);
        System.out.println(between);
    }

}
