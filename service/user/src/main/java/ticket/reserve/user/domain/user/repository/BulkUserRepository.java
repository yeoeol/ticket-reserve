package ticket.reserve.user.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.user.domain.user.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BulkUserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.jpa.properties.hibernate.default_batch_fetch_size}")
    private int batchSize;

    public int locationBulkUpdate(List<User> users, Map<Long, Point> pointMap) {
        String sql = "INSERT INTO users (user_id, username, password, email, location, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ST_SRID(POINT(?, ?), 4326), ?, ?) " +
                        "ON DUPLICATE KEY " +
                        "UPDATE location = VALUES(location), " +
                                "updated_at = VALUES(updated_at)";

        return jdbcTemplate.batchUpdate(sql,
                users,
                batchSize,
                (ps, user) -> {
                    Point point = pointMap.get(user.getId());
                    if (point == null) {
                        throw new CustomException(ErrorCode.POINT_NOT_FOUND);
                    }
                    ps.setLong(1, user.getId());
                    ps.setString(2, user.getUsername());
                    ps.setString(3, user.getPassword());
                    ps.setString(4, user.getEmail());
                    ps.setDouble(5, point.getX());
                    ps.setDouble(6, point.getY());
                    ps.setTimestamp(7, Timestamp.valueOf(user.getCreatedAt()));
                    ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                }
        ).length;
    }
}
