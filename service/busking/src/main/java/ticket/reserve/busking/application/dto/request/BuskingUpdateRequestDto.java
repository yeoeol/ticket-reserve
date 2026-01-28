package ticket.reserve.busking.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record BuskingUpdateRequestDto(
        @NotBlank(message = "{busking.title.not_blank}")
        @Size(max = 100, message = "{busking.title.range}")
        String title,          // 공연 제목
        @NotBlank(message = "{busking.description.not_blank}")
        String description,         // 상세 내용
        @NotBlank(message = "{busking.location.not_blank}")
        String location,            // 장소
        @NotNull(message = "{busking.startTime.not_null}")
        LocalDateTime startTime,
        @NotNull(message = "{busking.endTime.not_null}")
        LocalDateTime endTime,
        @NotNull(message = "{busking.totalInventoryCount.not_null}")
        @PositiveOrZero(message = "{busking.totalInventoryCount.range}")
        Integer totalInventoryCount             // 총 좌석 수
) {
}
