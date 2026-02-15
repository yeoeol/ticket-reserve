package ticket.reserve.hotbusking.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeCalculatorUtils {

    public static Duration calculateDurationToStartTime(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(now, startTime);
        if (between.isNegative()) {
            return Duration.ZERO;
        }
        return between;
    }
}
