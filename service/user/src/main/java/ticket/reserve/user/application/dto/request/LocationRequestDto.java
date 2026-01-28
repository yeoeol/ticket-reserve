package ticket.reserve.user.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LocationRequestDto(
        @NotNull(message = "{location.latitude.not_null}")
        @DecimalMin(value = "-90.0", message = "{location.latitude.range}")
        @DecimalMax(value = "90.0", message = "{location.latitude.range}")
        Double latitude,

        @NotNull(message = "{location.longitude.not_null}")
        @DecimalMin(value = "-180.0", message = "{location.longitude.range}")
        @DecimalMax(value = "180.0", message = "{location.longitude.range}")
        Double longitude
) {
}
