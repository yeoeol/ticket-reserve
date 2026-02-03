package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuskingCrudService {
    
    private final BuskingRepository buskingRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public Busking save(Busking busking) {
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

    @Transactional(readOnly = true)
    public Busking findById(Long id) {
        return buskingRepository.findByIdWithImage(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BUSKING_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Busking> findAll() {
        return buskingRepository.findAll();
    }

    @Transactional
    public void update(Long id, BuskingUpdateRequestDto request) {
        Busking busking = findById(id);
        busking.update(
                request.title(), request.description(), request.location(),
                request.startTime(), request.endTime(), request.totalInventoryCount()
        );
    }

    @Transactional
    public void delete(Long id) {
        buskingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Long> getIds() {
        return buskingRepository.findIds();
    }
}
