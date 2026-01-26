package ticket.reserve.inventory.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;
import ticket.reserve.core.tsid.IdGenerator;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuskingCreatedEventHandler implements EventHandler<BuskingCreatedEventPayload> {

    private final InventoryRepository inventoryRepository;
    private final IdGenerator idGenerator;

    @Override
    public void handle(Event<BuskingCreatedEventPayload> event) {
        BuskingCreatedEventPayload payload = event.getPayload();
        createInventoryAsTotalInventoryCount(payload.getBuskingId(), payload.getTotalInventoryCount());
        log.info("[BuskingCreatedEventHandler.handle] 좌석 생성 완료 " +
                "- buskingId = {}, totalInventoryCount = {}", payload.getBuskingId(), payload.getTotalInventoryCount());
    }

    @Override
    public boolean supports(Event<BuskingCreatedEventPayload> event) {
        return EventType.BUSKING_CREATED == event.getType();
    }

    private void createInventoryAsTotalInventoryCount(Long buskingId, Integer totalInventoryCount) {
        char prefix = 'A';
        int seatNumber = 1;

        for (int i = 1; i <= totalInventoryCount; i++) {
            String inventoryName = prefix + String.valueOf(seatNumber);
            Inventory inventory = Inventory.create(idGenerator, buskingId, inventoryName, 1000*i);

            inventoryRepository.save(inventory);
            seatNumber++;

            if (seatNumber > 10) {
                prefix++;
                seatNumber = 1;
            }
        }
    }
}
