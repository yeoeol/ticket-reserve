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

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private int retryCount;

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(IdGenerator idGenerator, String title, String body, Long receiverId, NotificationStatus status, int retryCount) {
        this.id = idGenerator.nextId();
        this.title = title;
        this.body = body;
        this.receiverId = receiverId;
        this.status = status;
        this.retryCount = retryCount;
    }

    public static Notification create(IdGenerator idGenerator, String title, String body, Long receiverId) {
        return Notification.builder()
                .idGenerator(idGenerator)
                .title(title)
                .body(body)
                .receiverId(receiverId)
                .status(NotificationStatus.PENDING)
                .build();
    }

    public void markAsSuccess() {
        this.status = NotificationStatus.SUCCESS;
    }

    public void markAsFail() {
        this.status = NotificationStatus.FAIL;
    }

}
