package ticket.reserve.event.dto;

import lombok.Builder;
import ticket.reserve.event.domain.Event;

import java.time.LocalDateTime;

@Builder
public record EventDetailResponseDto(
        Long id,
        String eventTitle,          // 공연 제목
        String description,
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        int availableInventory,
        int totalSeats
) {
    public static EventDetailResponseDto from(Event event, Integer availableInventoryCount) {
        return EventDetailResponseDto.builder()
                .id(event.getId())
                .eventTitle(event.getEventTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .availableInventory(availableInventoryCount)
                .totalSeats(event.getTotalSeats())
                .build();
    }
}