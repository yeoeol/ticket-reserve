package ticket.reserve.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequestDto(
        @NotBlank(message = "ID는 필수입니다.")
        String username,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,
        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
}
