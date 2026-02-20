package ticket.reserve.core.event.payload;

import lombok.*;
import ticket.reserve.core.event.EventPayload;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubscriptionNotificationSentEventPayload implements EventPayload {
    private Long buskingId;
    private String title;
    private String location;
    private Set<Long> userIds;
    private long remainingMinutes;
}
