package ticket.reserve.notification.infrastructure.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.event.EventType;
import ticket.reserve.notification.application.eventhandler.EventHandlerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final EventHandlerService eventHandlerService;

    @KafkaListener(topics = {
            EventType.Topic.TICKET_RESERVE_BUSKING
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[NotificationEventConsumer.listen] 이벤트 수신: message = {}", message);

        Event<EventPayload> event = Event.fromJson(message);
        if (event != null) {
            eventHandlerService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
