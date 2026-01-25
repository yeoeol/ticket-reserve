package ticket.reserve.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.FcmTokenRequestDto;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;
import ticket.reserve.notification.domain.fcmtoken.repository.FcmTokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final IdGenerator idGenerator;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveOrUpdate(FcmTokenRequestDto request) {
        fcmTokenRepository.findByUserId(request.userId())
            .ifPresentOrElse(
                fcmToken -> fcmToken.updateFcmToken(request.fcmToken()),
                () -> {
                    try {
                        fcmTokenRepository.save(request.toEntity(idGenerator));
                    } catch (DataIntegrityViolationException e) {
                        fcmTokenRepository.findByUserId(request.userId())
                                .ifPresent(token -> token.updateFcmToken(request.fcmToken()));
                    }
                }
            );
    }

    @Transactional(readOnly = true)
    public String getTokenByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FCM_TOKEN))
                .getFcmToken();
    }
}
