package ticket.reserve.admin.client.user.dto;

public record UserUpdateRequestDto(
        Long id,
        String username,
        String password,
        String email
) {
}
