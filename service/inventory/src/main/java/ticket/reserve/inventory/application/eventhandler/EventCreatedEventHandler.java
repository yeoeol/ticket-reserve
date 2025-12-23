package ticket.reserve.inventory.application.eventhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.EventCreatedEventPayload;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCreatedEventHandler implements EventHandler<EventCreatedEventPayload> {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void handle(Event<EventCreatedEventPayload> event) {
        EventCreatedEventPayload payload = event.getPayload();
        createInventoryAsTotalSeats(payload.getEventId(), payload.getTotalSeats());
        log.info("[EventCreatedEventHandler.handle] 좌석 생성 완료 " +
                "- eventId = {}, totalSeats = {}", payload.getEventId(), payload.getTotalSeats());
    }

    @Override
    public boolean supports(Event<EventCreatedEventPayload> event) {
        return EventType.EVENT_CREATED == event.getType();
    }

    private void createInventoryAsTotalSeats(Long eventId, int totalSeats) {
        char prefix = 'A';
        int seatNumber = 1;

        for (int i = 1; i <= totalSeats; i++) {
            String inventoryName = prefix + String.valueOf(seatNumber);
            Inventory inventory = Inventory.builder()
                    .eventId(eventId)
                    .inventoryName(inventoryName)
                    .price(1000*i)
                    .build();
            inventoryRepository.save(inventory);
            seatNumber++;

            if (seatNumber > 10) {
                prefix++;
                seatNumber = 1;
            }
        }
    }
}
