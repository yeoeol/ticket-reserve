package ticket.reserve.hotbusking.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeCalculatorUtils {

    public static Duration calculateDurationToStartTime(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(now, startTime);
    }
}
