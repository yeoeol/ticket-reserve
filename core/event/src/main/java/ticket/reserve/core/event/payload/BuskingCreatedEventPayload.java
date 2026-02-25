package ticket.reserve.core.event.payload;

import lombok.*;
import ticket.reserve.core.event.EventPayload;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BuskingCreatedEventPayload implements EventPayload {

    private Long buskingId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double latitude;
    private Double longitude;
}
