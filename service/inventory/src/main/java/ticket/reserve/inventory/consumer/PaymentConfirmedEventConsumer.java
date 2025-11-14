package ticket.reserve.inventory.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.inventory.service.InventoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedEventConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = EventType.Topic.TICKET_RESERVE_PAYMENT)
    public void listen(String message) {
        log.info("[InventoryService-service : PaymentConfirmedEventConsumer.listen] 결제 완료 이벤트 수신: message = {}", message);
        try {
            PaymentConfirmedEventPayload payload = objectMapper.readValue(message, PaymentConfirmedEventPayload.class);
            inventoryService.confirmInventory(payload.getInventoryId());
            log.info("[InventoryService-service : PaymentConfirmedEventConsumer.listen] 예약 확정 처리 완료 - inventoryId = {}", payload.getInventoryId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[PaymentConfirmedEventConsumer.listen] JSON 파싱 중 오류 발생", e);
        } catch (Exception e) {
            throw new RuntimeException("[PaymentConfirmedEventConsumer.listen] 오류 발생", e);
        }
    }
}
