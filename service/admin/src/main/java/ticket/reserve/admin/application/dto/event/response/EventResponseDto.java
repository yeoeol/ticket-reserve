package ticket.reserve.admin.application.dto.event.response;

import lombok.Builder;

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
}