package ticket.reserve.busking.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ticket.reserve.busking.domain.event.Event;
import ticket.reserve.tsid.IdGenerator;

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

        @NotNull(message = "좌석 수를 입력하세요.")
        @PositiveOrZero(message = "좌석은 0 이상이어야 합니다.")
        Integer totalInventoryCount
) {
    public Event toEntity(IdGenerator idGenerator) {
        return Event.create(
                idGenerator,
                this.eventTitle,
                this.description,
                this.location,
                this.startTime,
                this.endTime,
                this.totalInventoryCount
        );
    }
}
