package ticket.reserve.notification.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;

public record FcmTokenRequestDto(
        @NotNull(message = "{user.id.not_null}")
        Long userId,
        @NotBlank(message = "{fcmToken.not_blank}")
        String fcmToken
) {
    public FcmToken toEntity(IdGenerator idGenerator) {
        return FcmToken.create(idGenerator, this.userId, this.fcmToken);
    }
}
