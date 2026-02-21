package ticket.reserve.core.event.payload;

import lombok.*;
import ticket.reserve.core.event.EventPayload;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubscriptionCreatedEventPayload implements EventPayload {
    private Long subscriptionId;
    private Long userId;
    private Long buskingId;
    private String title;
    private String location;
    private LocalDateTime startTime;
    private Long buskingSubscriptionCount;
}
