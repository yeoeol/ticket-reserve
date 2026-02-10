package ticket.reserve.notification.application;

import ticket.reserve.notification.application.dto.request.FcmTokenRequestDto;

import java.util.List;

public interface FcmTokenService {
    // FCM TOKEN 저장 또는 업데이트
    void saveOrUpdate(FcmTokenRequestDto request);

    // FCM TOKEN 리스트 반환
    List<String> getTokensByUserIds(List<Long> userIds);
}
