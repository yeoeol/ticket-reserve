package ticket.reserve.user.application.port.out;

import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LocationPort {
    void addLocation(Long userId, Double latitude, Double longitude);

    Set<String> findUserIds();

    Map<Long, Point> getPointMapByUserIds(List<Long> userIds);
}
