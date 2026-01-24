package ticket.reserve.notification.application.dto.request;

import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;

public record FcmTokenRequestDto(
        Long userId,
        String fcmToken
) {
    public FcmToken toEntity(IdGenerator idGenerator) {
        return FcmToken.create(idGenerator, this.userId, this.fcmToken);
    }
}
