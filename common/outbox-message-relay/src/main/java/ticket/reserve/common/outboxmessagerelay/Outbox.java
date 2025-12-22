package ticket.reserve.common.outboxmessagerelay;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ticket.reserve.common.event.EventType;

import java.time.LocalDateTime;

@Table(name = "outbox")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

    @Id
    private Long outboxId;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private Long key;
    private String payload;
    private LocalDateTime createdAt;

    public static Outbox create(Long outboxId, EventType eventType, String payload, Long key) {
        Outbox outbox = new Outbox();
        outbox.outboxId = outboxId;
        outbox.eventType = eventType;
        outbox.key = key;
        outbox.payload = payload;
        outbox.createdAt = LocalDateTime.now();
        return outbox;
    }
}
