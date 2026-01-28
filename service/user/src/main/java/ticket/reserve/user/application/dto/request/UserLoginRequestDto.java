package ticket.reserve.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank(message = "{user.username.not_blank}")
        String username,
        @NotBlank(message = "{user.password.not_blank}")
        String password
) {
}
