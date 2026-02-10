package ticket.reserve.notification.application;

import ticket.reserve.notification.application.dto.request.FcmTokenRequestDto;

import java.util.List;
import java.util.Map;

public interface FcmTokenService {
    // FCM TOKEN 저장 또는 업데이트
    void saveOrUpdate(FcmTokenRequestDto request);

    // FCM TOKEN 리스트 반환
    Map<Long, String> getTokenMapByUserIds(List<Long> userIds);
}

