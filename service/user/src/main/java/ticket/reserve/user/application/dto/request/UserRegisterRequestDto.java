package ticket.reserve.user.application.dto.request;

public record UserRegisterRequestDto(
        String username,
        String password,
        String email
) {
}
