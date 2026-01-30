package ticket.reserve.notification.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.notification.domain.notification.Notification;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BulkNotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.jdbc.batch-size}")
    private int batchSize;

    @Transactional
    public int bulkInsert(List<Notification> notifications) {
        String sql = "INSERT INTO notifications (notification_id, title, message, busking_id, receiver_id) " +
                        "VALUES (?, ?, ?, ?, ?)";

        return jdbcTemplate.batchUpdate(sql,
                notifications,
                batchSize,
                (ps, notification) -> {
                    ps.setLong(1, notification.getId());
                    ps.setString(2, notification.getTitle());
                    ps.setString(3, notification.getMessage());
                    ps.setLong(4, notification.getBuskingId());
                    ps.setLong(5, notification.getReceiverId());
                }
        ).length;
    }
}
