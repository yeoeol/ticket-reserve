package ticket.reserve.event.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ticket.reserve.event.domain.Event;

import java.time.LocalDateTime;

public record EventRequestDto(
        @NotBlank(message = "공연 제목은 필수입니다.")
        @Size(max = 100, message = "공연 제목은 100자를 초과할 수 없습니다.")
        String eventTitle,
        @NotBlank(message = "공연 설명은 필수입니다.")
        String description,
        @NotBlank(message = "장소를 입력하세요.")
        String location,

        @NotNull(message = "시작 날짜를 입력하세요.")
        LocalDateTime startTime,
        @NotNull(message = "종료 날짜를 입력하세요.")
        LocalDateTime endTime,

        @Positive(message = "좌석 수는 1 이상이어야 합니다.")
        int totalSeats  // 총 좌석 수
) {
    public Event toEntity() {
        return Event.builder()
                .eventTitle(this.eventTitle)
                .description(this.description)
                .location(this.location)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .totalSeats(this.totalSeats)
                .build();
    }
}
