package ticket.reserve.inventory.application.dto.response;

import lombok.Builder;

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
}