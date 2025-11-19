package ticket.reserve.inventory.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.application.InventoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiredEventConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = EventType.Topic.TICKET_RESERVE_RESERVATION)
    public void listen(String message) {
        log.info("[ReservationExpiredEventConsumer.listen] 예매 만료 이벤트 수신: message = {}", message);
        try {
            PaymentConfirmedEventPayload payload = objectMapper.readValue(message, PaymentConfirmedEventPayload.class);
            inventoryService.releaseInventory(payload.getInventoryId());
            log.info("[ReservationExpiredEventConsumer.listen] 예매 취소 처리 완료 - reservationId = {}, inventoryId = {}", payload.getReservationId(), payload.getInventoryId());
        } catch (Exception e) {
            log.error("[ReservationExpiredEventConsumer.listen]", e);
            throw new CustomException(ErrorCode.RESERVATION_EXPIRED_ERROR);
        }
    }
}
