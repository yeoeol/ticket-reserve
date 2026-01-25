package ticket.reserve.notification.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;

public record FcmTokenRequestDto(
        @NotNull(message = "사용자ID는 필수입니다.")
        Long userId,
        @NotBlank(message = "FCM_TOKEN 값은 필수입니다.")
        String fcmToken
) {
    public FcmToken toEntity(IdGenerator idGenerator) {
        return FcmToken.create(idGenerator, this.userId, this.fcmToken);
    }
}
