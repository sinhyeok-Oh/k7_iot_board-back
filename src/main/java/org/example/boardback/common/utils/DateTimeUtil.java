package org.example.boardback.common.utils;


import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 실무용 날짜/시간 변환 유틸
 * - DB 저장: UTC(LocalDateTime, DATETIME(6))
 * - 외부 노출: KST 문자열 또는 ISO-8601(UTC Z)
 * - 입력: KST LocalDateTime 또는 ISO-8601
 */
public class DateTimeUtil {

    /** Zone definitions */
    public static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");
    public static final ZoneId ZONE_UTC = ZoneId.of("UTC");

    /** Formatters */
    private static final DateTimeFormatter KST_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter ISO_UTC_FORMAT
            = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    // ------------------------------------------------------------------------------------
    // 1. UTC(LocalDateTime) → KST 문자열
    // ------------------------------------------------------------------------------------
    public static String toKstString(LocalDateTime utcLocalDateTime) {
        if (utcLocalDateTime == null) return null;

        ZonedDateTime zdtKst = utcLocalDateTime
                .atZone(ZONE_UTC)
                .withZoneSameInstant(ZONE_KST);

        return zdtKst.format(KST_FORMAT);
    }

    // ------------------------------------------------------------------------------------
    // 2. UTC(LocalDateTime) → ISO-8601 UTC 문자열 ("2025-09-03T04:00:00Z")
    // ------------------------------------------------------------------------------------
    public static String toUtcIsoString(LocalDateTime utcLocalDateTime) {
        if (utcLocalDateTime == null) return null;

        OffsetDateTime odt = utcLocalDateTime.atOffset(ZoneOffset.UTC);
        return ISO_UTC_FORMAT.format(odt);
    }

    // ------------------------------------------------------------------------------------
    // 3. KST(LocalDateTime) → UTC(LocalDateTime)
    //    예: 2025-09-03 13:00:00(KST) → 2025-09-03 04:00:00(UTC)
    // ------------------------------------------------------------------------------------
    public static LocalDateTime kstToUtc(LocalDateTime kstDateTime) {
        if (kstDateTime == null) return null;

        return kstDateTime
                .atZone(ZONE_KST)
                .withZoneSameInstant(ZONE_UTC)
                .toLocalDateTime();
    }

    // ------------------------------------------------------------------------------------
    // 4. ISO-8601 문자열(KST 또는 UTC) → UTC(LocalDateTime)
    //    "2025-09-03T13:00:00+09:00" → UTC 2025-09-03T04:00:00
    // ------------------------------------------------------------------------------------
    public static LocalDateTime isoToUtc(String isoString) {
        if (isoString == null) return null;

        OffsetDateTime odt = OffsetDateTime.parse(isoString);
        return odt.withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    // ------------------------------------------------------------------------------------
    // 5. KST 문자열("yyyy-MM-dd HH:mm:ss") → UTC(LocalDateTime)
    // ------------------------------------------------------------------------------------
    public static LocalDateTime kstStringToUtc(String kstString) {
        if (kstString == null || kstString.isBlank()) return null;

        LocalDateTime kstLocalDateTime = LocalDateTime.parse(kstString, KST_FORMAT);

        return kstLocalDateTime
                .atZone(ZONE_KST)
                .withZoneSameInstant(ZONE_UTC)
                .toLocalDateTime();
    }

    // ------------------------------------------------------------------------------------
    // 6. 지금 시간 UTC/KST 가져오기
    // ------------------------------------------------------------------------------------
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZONE_UTC);
    }

    public static LocalDateTime nowKst() {
        return LocalDateTime.now(ZONE_KST);
    }
}