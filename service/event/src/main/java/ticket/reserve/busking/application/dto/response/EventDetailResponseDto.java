package ticket.reserve.busking.application.dto.response;

import lombok.Builder;
import ticket.reserve.busking.domain.event.Event;
import ticket.reserve.busking.domain.eventimage.EventImage;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventDetailResponseDto(
        Long id,
        String eventTitle,          // 공연 제목
        String description,
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        int availableInventory,
        Integer totalInventoryCount,
        List<String> imageUrls
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
                .totalInventoryCount(event.getTotalInventoryCount())
                .imageUrls(event.getEventImages().stream()
                        .map(EventImage::getStoredPath)
                        .toList()
                ).build();
    }
}