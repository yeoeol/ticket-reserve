package ticket.reserve.notification.domain.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.BaseTimeEntity;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification extends BaseTimeEntity {

    @Id
    @Column(name = "notification_id")
    private Long id;

    private String title;
    private String body;
    private Long receiverId;
    private Long buskingId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private int retryCount;

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(IdGenerator idGenerator, String title, String body, Long receiverId, Long buskingId, NotificationStatus status, int retryCount) {
        this.id = idGenerator.nextId();
        this.title = title;
        this.body = body;
        this.receiverId = receiverId;
        this.buskingId = buskingId;
        this.status = status;
        this.retryCount = retryCount;
    }

    public static Notification create(IdGenerator idGenerator, String title, String body, Long receiverId, Long buskingId) {
        return Notification.builder()
                .idGenerator(idGenerator)
                .title(title)
                .body(body)
                .receiverId(receiverId)
                .buskingId(buskingId)
                .status(NotificationStatus.PENDING)
                .build();
    }

    public void markAsSuccess() {
        this.status = NotificationStatus.SUCCESS;
    }

    public void markAsFail() {
        if (this.retryCount >= 5) {
            this.status = NotificationStatus.PERMANENT_FAILURE;
        } else {
            this.status = NotificationStatus.FAIL;
        }
    }

    public void incrementRetryCount() {
        if (this.retryCount < 5) {
            this.retryCount++;
        } else {
            this.status = NotificationStatus.PERMANENT_FAILURE;
        }
    }
}
