package ticket.reserve.notification.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.notification.application.NotificationService;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuskingCreatedEventHandler implements EventHandler<BuskingCreatedEventPayload> {

    private final NotificationService notificationService;

    @Override
    public void handle(Event<BuskingCreatedEventPayload> event) {
        BuskingCreatedEventPayload payload = event.getPayload();

        List<Long> receiverIds = findNearByUserIds();
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

    private List<Long> findNearByUserIds() {
        // TODO : 버스킹 장소 주변 사용자 ID 조회
        return List.of(803522828039717047L);
    }
}
