package ticket.reserve.event.dto;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        String eventTitle,          // 공연 제목
        String description,         // 상세 내용
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalSeats             // 총 좌석 수
) {
}
