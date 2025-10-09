package ticket.reserve.user.dto;

public record UserRegisterRequestDto(
        String username,
        String password,
        String email
) {
}
