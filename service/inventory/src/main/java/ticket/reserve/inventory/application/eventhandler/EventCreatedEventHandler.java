package ticket.reserve.inventory.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;
import ticket.reserve.tsid.IdGenerator;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCreatedEventHandler implements EventHandler<BuskingCreatedEventPayload> {

    private final InventoryRepository inventoryRepository;
    private final IdGenerator idGenerator;

    @Override
    @Transactional
    public void handle(Event<BuskingCreatedEventPayload> event) {
        BuskingCreatedEventPayload payload = event.getPayload();
        createInventoryAsTotalInventoryCount(payload.getBuskingId(), payload.getTotalInventoryCount());
        log.info("[EventCreatedEventHandler.handle] 좌석 생성 완료 " +
                "- buskingId = {}, totalInventoryCount = {}", payload.getBuskingId(), payload.getTotalInventoryCount());
    }

    @Override
    public boolean supports(Event<BuskingCreatedEventPayload> event) {
        return EventType.EVENT_CREATED == event.getType();
    }

    private void createInventoryAsTotalInventoryCount(Long eventId, Integer totalInventoryCount) {
        char prefix = 'A';
        int seatNumber = 1;

        for (int i = 1; i <= totalInventoryCount; i++) {
            String inventoryName = prefix + String.valueOf(seatNumber);
            Inventory inventory = Inventory.create(idGenerator, eventId, inventoryName, 1000*i);

            inventoryRepository.save(inventory);
            seatNumber++;

            if (seatNumber > 10) {
                prefix++;
                seatNumber = 1;
            }
        }
    }
}
