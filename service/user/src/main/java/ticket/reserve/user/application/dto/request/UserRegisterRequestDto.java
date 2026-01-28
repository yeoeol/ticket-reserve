package ticket.reserve.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequestDto(
        @NotBlank(message = "{user.username.not_blank}")
        String username,
        @NotBlank(message = "{user.password.not_blank}")
        String password,
        @NotBlank(message = "{user.email.not_blank}")
        @Email(message = "{user.email.valid}")
        String email
) {
}
