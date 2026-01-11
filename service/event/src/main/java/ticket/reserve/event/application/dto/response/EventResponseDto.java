package ticket.reserve.event.application.dto.response;

import lombok.Builder;
import ticket.reserve.event.domain.event.Event;

import java.time.LocalDateTime;

@Builder
public record EventResponseDto(
        Long id,
        String eventTitle,          // 공연 제목
        String description,
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static EventResponseDto from(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .eventTitle(event.getEventTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
    }
}