package ticket.reserve.subscription.application.port.out;

import java.time.Duration;
import java.time.LocalDateTime;

public interface BuskingInfoPort {

    void createOrUpdate(Long buskingId, String title, String location, LocalDateTime startTime, Duration ttl);
}
