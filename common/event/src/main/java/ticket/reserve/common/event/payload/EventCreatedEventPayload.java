package ticket.reserve.common.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.common.event.EventPayload;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedEventPayload implements EventPayload {

    private Long eventId;
    private String eventTitle;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalSeats;
}
