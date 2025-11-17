package ticket.reserve.inventory.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.EventCreatedEventPayload;
import ticket.reserve.inventory.application.InventoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCreatedEventConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = EventType.Topic.TICKET_RESERVE_EVENT)
    public void listen(String message) {
        log.info("[EventCreatedEventConsumer.listen] 이벤트 생성 이벤트 수신: message = {}", message);
        try {
            EventCreatedEventPayload payload = objectMapper.readValue(message, EventCreatedEventPayload.class);
            inventoryService.createInventoryAsTotalSeats(payload.getEventId(), payload.getTotalSeats());
            log.info("[EventCreatedEventConsumer.listen] 좌석 생성 완료 - eventId = {}, totalSeats = {}", payload.getEventId(), payload.getTotalSeats());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[EventCreatedEventConsumer.listen] JSON 파싱 중 오류 발생", e);
        } catch (Exception e) {
            throw new RuntimeException("[EventCreatedEventConsumer.listen] 오류 발생", e);
        }
    }
}
