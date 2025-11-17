package ticket.reserve.user.application.dto.request;

public record UserLoginRequestDto(
        String username,
        String password
) {
}
