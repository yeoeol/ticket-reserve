package ticket.reserve.notification.application.port.out;

import java.util.List;

public interface RedisPort {
    List<Long> findNearbyActiveUsers(Double buskingLng, Double buskingLat, double radiusKm);
}
