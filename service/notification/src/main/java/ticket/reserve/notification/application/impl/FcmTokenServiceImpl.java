package ticket.reserve.notification.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.FcmTokenService;
import ticket.reserve.notification.application.dto.request.FcmTokenRequestDto;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;
import ticket.reserve.notification.domain.fcmtoken.repository.FcmTokenRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

    private final IdGenerator idGenerator;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveOrUpdate(FcmTokenRequestDto request) {
        Optional<FcmToken> optionalFcmToken = fcmTokenRepository.findByUserIdForUpdate(request.userId());

        if (optionalFcmToken.isPresent()) {
            optionalFcmToken.get().updateFcmToken(request.fcmToken());
            return;
        }

        fcmTokenRepository.save(request.toEntity(idGenerator));
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getTokenMapByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<FcmToken> tokens = fcmTokenRepository.findByUserIdIn(userIds);
        return tokens.stream()
                .collect(Collectors.toMap(
                        FcmToken::getUserId,
                        FcmToken::getFcmToken
                ));
    }
}
