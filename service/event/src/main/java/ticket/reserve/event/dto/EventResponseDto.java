package ticket.reserve.event.dto;

import lombok.Builder;
import ticket.reserve.event.entity.Event;

import java.time.LocalDateTime;

@Builder
public record EventResponseDto(
        Long id,
        String eventTitle,          // 공연 제목
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        int availableSeats          // 남은 좌석 수
) {
    public static EventResponseDto from(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .eventTitle(event.getEventTitle())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .availableSeats(event.getAvailableSeats())
                .build();
    }
}