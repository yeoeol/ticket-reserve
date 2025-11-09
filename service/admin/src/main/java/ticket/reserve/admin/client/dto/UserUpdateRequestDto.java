package ticket.reserve.admin.client.dto;

public record UserUpdateRequestDto(
        Long id,
        String username,
        String password,
        String email
) {
}
