package ticket.reserve.hotbusking.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BuskingResponseDto(
        Long id,
        String title,          // 공연 제목
        String description,
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<String> imageUrls,
        Double latitude,
        Double longitude,
        boolean isSubscribed
) {
}