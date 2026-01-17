package ticket.reserve.user.application.dto.response;

import lombok.Builder;

@Builder
public record UserLoginResponseDto(
        UserResponseDto user,
        String accessToken
) {
    public static UserLoginResponseDto from(UserResponseDto user, String accessToken) {
        return UserLoginResponseDto.builder()
                .user(user)
                .accessToken(accessToken)
                .build();
    }
}
