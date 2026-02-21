package ticket.reserve.notification.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.notification.domain.notification.Notification;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BulkNotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.jdbc.batch-size}")
    private int batchSize;

    @Transactional
    public int bulkInsert(List<Notification> notifications) {
        String sql = "INSERT INTO notifications " +
                        "(notification_id, title, body, receiver_id, busking_id, status, retry_count, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return batchExecute(notifications, sql);
    }

    @Transactional
    public int bulkUpsert(List<Notification> notifications) {
        String sql = "INSERT INTO notifications " +
                        "(notification_id, title, body, receiver_id, busking_id, status, retry_count, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                            "status = VALUES(status), " +
                            "retry_count = VALUES(retry_count)";

        return batchExecute(notifications, sql);
    }

    private int batchExecute(List<Notification> notifications, String sql) {
        LocalDateTime now = LocalDateTime.now();

        return jdbcTemplate.batchUpdate(sql,
                notifications,
                batchSize,
                (ps, notification) -> {
                    ps.setLong(1, notification.getId());
                    ps.setString(2, notification.getTitle());
                    ps.setString(3, notification.getBody());
                    ps.setLong(4, notification.getReceiverId());
                    ps.setLong(5, notification.getBuskingId());
                    ps.setString(6, notification.getStatus().name());
                    ps.setInt(7, notification.getRetryCount());
                    ps.setTimestamp(8, notification.getCreatedAt() != null
                            ? Timestamp.valueOf(notification.getCreatedAt())
                            : Timestamp.valueOf(now));
                    ps.setTimestamp(9, Timestamp.valueOf(now));
                }
        ).length;
    }
}
