package ticket.reserve.user.dto;

public record UserUpdateRequestDto(
        Long id,
        String username,
        String password,
        String email
) {
}
