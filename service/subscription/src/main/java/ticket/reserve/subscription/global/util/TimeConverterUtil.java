package ticket.reserve.subscription.global.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class TimeConverterUtil {

    public static long convertToMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
