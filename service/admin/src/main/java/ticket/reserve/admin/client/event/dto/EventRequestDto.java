package ticket.reserve.admin.client.event.dto;

import java.time.LocalDateTime;

public record EventRequestDto(
        Long id,
        String eventTitle,
        String description,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalSeats
) {
        public EventRequestDto() {
                this(null, "", "", "", LocalDateTime.now(), LocalDateTime.now(), 0);
        }
}
