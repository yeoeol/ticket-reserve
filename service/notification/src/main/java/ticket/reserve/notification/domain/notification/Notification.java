package ticket.reserve.notification.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification extends BaseTimeEntity {

    @Id
    @Column(name = "notification_id")
    private Long id;

    private String title;
    private String message;

    private Long buskingId;

    private Long receiverId;

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(IdGenerator idGenerator, String title, String message, Long buskingId, Long receiverId) {
        this.id = idGenerator.nextId();
        this.title = title;
        this.message = message;
        this.buskingId = buskingId;
        this.receiverId = receiverId;
    }

    public static Notification create(IdGenerator idGenerator, String title, String message, Long buskingId, Long receiverId) {
        return Notification.builder()
                .idGenerator(idGenerator)
                .title(title)
                .message(message)
                .buskingId(buskingId)
                .receiverId(receiverId)
                .build();
    }
}
