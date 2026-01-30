package ticket.reserve.core.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.event.EventPayload;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionNotificationSendedEventPayload implements EventPayload {
    private Long buskingId;
    private Set<Long> userIds;
    private long remainingMinutes;
}
