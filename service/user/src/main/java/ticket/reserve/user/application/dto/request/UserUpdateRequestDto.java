package ticket.reserve.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequestDto(
        @NotNull(message = "{user.id.not_null}")
        Long id,
        @NotBlank(message = "{user.username.not_blank}")
        String username,
        @NotBlank(message = "{user.password.not_blank}")
        String password,
        @Email(message = "{user.email.valid}")
        String email
) {
}
