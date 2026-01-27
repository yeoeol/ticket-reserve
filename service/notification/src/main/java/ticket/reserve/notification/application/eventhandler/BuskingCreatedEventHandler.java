package ticket.reserve.notification.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.RedisService;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuskingCreatedEventHandler implements EventHandler<BuskingCreatedEventPayload> {

    private final NotificationService notificationService;
    private final RedisService redisService;

    private static final double radiusKm = 5; // 반경 5km

    @Override
    public void handle(Event<BuskingCreatedEventPayload> event) {
        BuskingCreatedEventPayload payload = event.getPayload();

        List<Long> receiverIds = redisService.findNearbyActiveUsers(
                payload.getLongitude(), payload.getLatitude(), radiusKm
        );

        int failCount = 0;
        for (Long receiverId : receiverIds) {
            NotificationResponseDto response = notificationService.createAndSend(
                    NotificationRequestDto.notifyBuskingCreated(payload.getTitle(), payload.getLocation(), payload.getBuskingId(), receiverId)
            );
            if (!response.result().isSuccess()) failCount++;
        }
        log.info("[BuskingCreatedEventHandler.handle] 버스킹 생성 알림 발송 " +
                "- buskingId={}, 제목={} : 총 알림 {}건 : 성공 {}건, 실패 {}건",
                payload.getBuskingId(), payload.getTitle(), receiverIds.size(), receiverIds.size()-failCount, failCount);
    }

    @Override
    public boolean supports(Event<BuskingCreatedEventPayload> event) {
        return EventType.BUSKING_CREATED == event.getType();
    }
}
