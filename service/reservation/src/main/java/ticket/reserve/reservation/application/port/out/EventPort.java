package ticket.reserve.reservation.application.port.out;

import java.util.List;

public interface EventPort {
    List<Long> getEventIds();
}
