package ticket.reserve.inventory.client.dto;

import java.time.LocalDateTime;

public record EventResponseDto(
        Long id,
        String eventTitle,          // 공연 제목
        String description,
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        int availableSeats          // 남은 좌석 수
) {
}
