package Chapter7;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TestTime {
    public static void main(String[] args) {
        long start = System.nanoTime();
        LocalDate date = LocalDate.of(2026, 9, 5);
        System.out.println(date);
        LocalDate newDate = date.plusDays(10);  // 返回新对象，原对象不变
        System.out.println(newDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy", Locale.ENGLISH);
        String str1 = date.format(formatter);
        System.out.println(str1);

        LocalDate parsed = LocalDate.parse("2026-09-05");
        System.out.println(parsed);
        LocalDate custom = LocalDate.parse("2026/09/05", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        System.out.println(custom);


        // 英文输出
        DateTimeFormatter englishFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US);
        System.out.println(date.format(englishFormatter));  // 05-Sep-2026

        // 中文输出（默认）
        DateTimeFormatter chineseFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.CHINA);
        System.out.println(date.format(chineseFormatter));  // 05-9月-2026

        // 全称月份
        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.US);
        System.out.println(date.format(fullFormatter));     // 05-September-2026

        // 解析 "05-Sep-2026" 回 LocalDate
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US);
        LocalDate parsed1 = LocalDate.parse("05-Sep-2026", formatter1);
        System.out.println(parsed1);  // 2026-09-05

        // 90% 的场景，用这两个就够了
        LocalDateTime now = LocalDateTime.now();  // 业务时间
        System.out.println(now);
        Instant timestamp = Instant.now();        // 日志/存储时间戳
        System.out.println(timestamp);
        // 通常返回 UTC 时间戳（毫秒或秒）
        long timestamp1 = Instant.now().toEpochMilli();
        System.out.println(timestamp1);
        // 或返回 ISO 8601 格式字符串
        String isoTime = Instant.now().toString();  // 2026-07-09T06:30:25.123Z
        System.out.println(isoTime);

        // 格式化为用户所在时区的字符串
        ZonedDateTime now1 = ZonedDateTime.now();
        System.out.println(now1);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String display = now1.format(formatter2);
        System.out.println(display);

        // ... 执行代码 ...
        long duration = System.nanoTime() - start;  // 耗时（纳秒）
        System.out.println(duration);
        // 1. 获取当前时间的毫秒数（最常用）
        long millis = System.currentTimeMillis();

        // 2. 获取当前日期时间（最常用）
        LocalDateTime now2 = LocalDateTime.now();

        // 3. 获取当前 UTC 时间戳（推荐用于存储和传输）
        Instant now3 = Instant.now();

        // 4. 格式化为可读字符串
        String formatted = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 5. 获取当前日期所在月的第一天
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);

        // 6. 获取当前时间的整点（去掉分钟和秒）
        LocalTime hour = LocalTime.now().withMinute(0).withSecond(0).withNano(0);
    }
}
