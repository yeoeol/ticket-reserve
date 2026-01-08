package ticket.reserve.admin.application.dto.event.request;

import java.time.LocalDateTime;

public record EventRequestDto(
        Long id,
        String eventTitle,
        String description,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalInventoryCount
) {
        public EventRequestDto() {
                this(null, "", "", "", LocalDateTime.now(), LocalDateTime.now(), 0);
        }
}
