package ticket.reserve.user.dto;

public record UserLoginRequestDto(
        String username,
        String password
) {
}
