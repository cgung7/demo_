package org.example.demo_ssr_v1_1._core.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class MyDateUtil {

    // 정적 변수로 포맷터를 선언 - 성능향상
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String timestampFormat(Timestamp time) {
        if (time == null) {
            return null;
        }
        // Timestamp -> LocalDateTime 변환 후 포맷 적용
        return time.toLocalDateTime().format(FORMATTER);
    }
}
// orm jpa 표준 하이버네트 영속성 컨텍스