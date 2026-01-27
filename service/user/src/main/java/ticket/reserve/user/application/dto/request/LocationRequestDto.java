package ticket.reserve.user.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LocationRequestDto(
        @NotNull(message = "위도 값은 필수입니다. (-90.0 ~ 90.0)")
        @DecimalMin("-90.0") @DecimalMax("90.0")
        Double latitude,
        @NotNull(message = "경도 값은 필수입니다. (-180.0 ~ 180.0)")
        @DecimalMin("-180.0") @DecimalMax("180.0")
        Double longitude
) {
}
