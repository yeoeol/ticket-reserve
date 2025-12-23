package ticket.reserve.inventory.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedEventHandler implements EventHandler<PaymentConfirmedEventPayload> {

    private final InventoryRepository inventoryRepository;

    @Override
    public void handle(Event<PaymentConfirmedEventPayload> event) {
        PaymentConfirmedEventPayload payload = event.getPayload();
        confirmInventory(payload.getInventoryId());
        log.info("[PaymentConfirmedEventConsumer.listen] 예매 확정 처리 완료 - inventoryId = {}", payload.getInventoryId());
    }

    @Override
    public boolean supports(Event<PaymentConfirmedEventPayload> event) {
        return EventType.PAYMENT_CONFIRMED == event.getType();
    }

    private void confirmInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.confirm();
    }
}
