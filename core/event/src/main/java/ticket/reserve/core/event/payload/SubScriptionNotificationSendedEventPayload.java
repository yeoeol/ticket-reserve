package ticket.reserve.core.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.event.EventPayload;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubScriptionNotificationSendedEventPayload implements EventPayload {
    private Long buskingId;
    private Long userId;
}
