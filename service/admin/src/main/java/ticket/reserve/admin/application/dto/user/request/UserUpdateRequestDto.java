package ticket.reserve.admin.application.dto.user.request;

public record UserUpdateRequestDto(
        Long id,
        String username,
        String password,
        String email
) {
}
