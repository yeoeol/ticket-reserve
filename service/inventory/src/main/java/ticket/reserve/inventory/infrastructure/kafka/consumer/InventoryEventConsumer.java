package ticket.reserve.inventory.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.event.EventType;
import ticket.reserve.inventory.application.InventoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = {
            EventType.Topic.TICKET_RESERVE_EVENT,
            EventType.Topic.TICKET_RESERVE_PAYMENT,
            EventType.Topic.TICKET_RESERVE_RESERVATION
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[InventoryEventConsumer.listen] 이벤트 수신: message = {}", message);

        Event<EventPayload> event = Event.fromJson(message);
        if (event != null) {
            inventoryService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
