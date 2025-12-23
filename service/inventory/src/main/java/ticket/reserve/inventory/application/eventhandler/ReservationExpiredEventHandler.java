package ticket.reserve.inventory.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.ReservationExpiredPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;


@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiredEventHandler implements EventHandler<ReservationExpiredPayload> {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void handle(Event<ReservationExpiredPayload> event) {
        ReservationExpiredPayload payload = event.getPayload();
        releaseInventory(payload.getInventoryId());
        log.info("[ReservationExpiredEventConsumer.listen] 예매 취소 처리 완료 " +
                "- eventId = {}, reservationId = {}, inventoryId = {}", payload.getEventId(), payload.getReservationId(), payload.getInventoryId());
    }

    @Override
    public boolean supports(Event<ReservationExpiredPayload> event) {
        return EventType.RESERVATION_EXPIRED == event.getType();
    }

    private void releaseInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.release();
    }
}
