package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.FcmTokenRequestDto;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;
import ticket.reserve.notification.domain.fcmtoken.repository.FcmTokenRepository;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final IdGenerator idGenerator;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void save(FcmTokenRequestDto request) {
        FcmToken fcmToken = request.toEntity(idGenerator);
        fcmTokenRepository.save(fcmToken);
    }

    @Transactional(readOnly = true)
    public String getTokenByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FCM_TOKEN))
                .getFcmToken();
    }
}
