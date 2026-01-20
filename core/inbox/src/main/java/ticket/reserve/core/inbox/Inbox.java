package ticket.reserve.core.inbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;

@Table(name = "inbox")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inbox {

    @Id
    private Long inboxId;

    @Column(unique = true)
    private Long eventId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private LocalDateTime consumedAt;

    public static Inbox create(IdGenerator idGenerator, Long eventId, EventType eventType) {
        Inbox inbox = new Inbox();
        inbox.inboxId = idGenerator.nextId();
        inbox.eventId = eventId;
        inbox.eventType = eventType;
        inbox.consumedAt = LocalDateTime.now();
        return inbox;
    }
}
