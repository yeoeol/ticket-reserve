package ticket.reserve.core.outboxmessagerelay;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ticket.reserve.core.event.EventType;

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
    @Column(columnDefinition = "VARCHAR(50)")
    private EventType eventType;
    private Long partitionKey;

    @Column(length = 500)
    private String payload;
    private LocalDateTime createdAt;

    public static Outbox create(Long outboxId, EventType eventType, String payload, Long partitionKey) {
        Outbox outbox = new Outbox();
        outbox.outboxId = outboxId;
        outbox.eventType = eventType;
        outbox.partitionKey = partitionKey;
        outbox.payload = payload;
        outbox.createdAt = LocalDateTime.now();
        return outbox;
    }
}
