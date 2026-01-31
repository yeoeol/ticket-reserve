package ticket.reserve.notification.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.port.out.RedisPort;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuskingCreatedEventHandler implements EventHandler<BuskingCreatedEventPayload> {

    private final NotificationService notificationService;
    private final RedisPort redisPort;

    private static final double radiusKm = 5; // 반경 5km

    @Override
    public void handle(Event<BuskingCreatedEventPayload> event) {
        BuskingCreatedEventPayload payload = event.getPayload();

        // 버스킹 위치 반경 radiumKm 내 사용자 조회
        List<Long> receiverIds = redisPort.findNearbyActiveUsers(
                payload.getLongitude(), payload.getLatitude(), radiusKm
        );

        String body = payload.getLocation() + " : 새로운 버스킹이 등록되었습니다!";
        notificationService.sendBulkNotification(
                payload.getTitle(),
                body,
                payload.getBuskingId(),
                receiverIds
        );

        log.info("[BuskingCreatedEventHandler.handle] 버스킹 생성 알림 발송 " +
                "- buskingId={}, 제목={} : 총 알림 {}건",
                payload.getBuskingId(), payload.getTitle(), receiverIds.size());
    }

    @Override
    public boolean supports(Event<BuskingCreatedEventPayload> event) {
        return EventType.BUSKING_CREATED == event.getType();
    }
}
