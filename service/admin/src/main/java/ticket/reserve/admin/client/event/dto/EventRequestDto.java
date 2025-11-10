package ticket.reserve.admin.client.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record EventRequestDto(
        String eventTitle,          // 공연 제목
        String description,         // 상세 내용
        String location,            // 장소
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime startTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime endTime,
        int totalSeats             // 총 좌석 수
) {
}
