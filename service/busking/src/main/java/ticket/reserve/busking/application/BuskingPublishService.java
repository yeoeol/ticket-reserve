package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.core.event.payload.BuskingDeletedEventPayload;
import ticket.reserve.core.event.payload.BuskingUpdatedEventPayload;
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
                        .latitude(savedBusking.getCoordinate().getY())
                        .longitude(savedBusking.getCoordinate().getX())
                        .build(),
                savedBusking.getId()
        );
        return savedBusking;
    }

    @Transactional
    public void publishBuskingDeletedEvent(Busking busking) {
        buskingRepository.delete(busking);
        outboxEventPublisher.publish(
                EventType.BUSKING_DELETED,
                BuskingDeletedEventPayload.builder()
                        .buskingId(busking.getId())
                        .title(busking.getTitle())
                        .description(busking.getDescription())
                        .location(busking.getLocation())
                        .startTime(busking.getStartTime())
                        .endTime(busking.getEndTime())
                        .latitude(busking.getCoordinate().getY())
                        .longitude(busking.getCoordinate().getX())
                        .build(),
                busking.getId()
        );
    }

    @Transactional
    public void publishBuskingUpdatedEvent(Busking busking) {
        outboxEventPublisher.publish(
                EventType.BUSKING_UPDATED,
                BuskingUpdatedEventPayload.builder()
                        .buskingId(busking.getId())
                        .title(busking.getTitle())
                        .description(busking.getDescription())
                        .location(busking.getLocation())
                        .startTime(busking.getStartTime())
                        .endTime(busking.getEndTime())
                        .latitude(busking.getCoordinate().getY())
                        .longitude(busking.getCoordinate().getX())
                        .build(),
                busking.getId()
        );
    }
}
