package ticket.reserve.admin.application.dto.event.response;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
public record EventDetailResponseDto(
        Long id,
        String eventTitle,          // 공연 제목
        String description,
        String location,            // 장소
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startTime,
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endTime,
        int availableInventory,
        int totalInventoryCount
) {
}