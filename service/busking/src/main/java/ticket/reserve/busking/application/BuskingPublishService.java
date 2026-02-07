package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;

@Service
@RequiredArgsConstructor
public class BuskingPublishService {

    private final BuskingRepository buskingRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public Busking publishBuskingCreatedEvent(Busking busking) {
        Busking savedBusking = buskingRepository.save(busking);
        outboxEventPublisher.publish(
                EventType.BUSKING_CREATED,
                BuskingCreatedEventPayload.builder()
                        .buskingId(savedBusking.getId())
                        .title(savedBusking.getTitle())
                        .description(savedBusking.getDescription())
                        .location(savedBusking.getLocation())
                        .startTime(savedBusking.getStartTime())
                        .endTime(savedBusking.getEndTime())
                        .totalInventoryCount(savedBusking.getTotalInventoryCount())
                        .latitude(savedBusking.getCoordinate().getY())
                        .longitude(savedBusking.getCoordinate().getX())
                        .build(),
                savedBusking.getId()
        );
        return savedBusking;
    }
}
