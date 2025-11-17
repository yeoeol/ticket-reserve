package ticket.reserve.user.application.dto.request;

public record UserUpdateRequestDto(
        Long id,
        String username,
        String password,
        String email
) {
}
