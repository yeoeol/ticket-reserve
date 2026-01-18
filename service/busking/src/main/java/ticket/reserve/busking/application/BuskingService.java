package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.domain.event.Busking;
import ticket.reserve.busking.domain.event.repository.BuskingRepository;
import ticket.reserve.busking.domain.eventimage.enums.ImageType;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.tsid.IdGenerator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuskingService {

    private final BuskingRepository buskingRepository;
    private final InventoryPort inventoryPort;
    private final ImagePort imagePort;
    private final OutboxEventPublisher outboxEventPublisher;
    private final IdGenerator idGenerator;

    @Transactional
    public BuskingResponseDto create(BuskingRequestDto request, MultipartFile file) {
        Busking busking = request.toEntity(idGenerator);
        if (file != null && !file.isEmpty()) {
            ImageResponseDto imageResponse = imagePort.uploadImage(file);
            busking.addEventImage(
                    idGenerator, imageResponse.getOriginalFileName(), imageResponse.getStoredPath(),
                    ImageType.THUMBNAIL, 1
            );
        }

        Busking savedBusking = buskingRepository.save(busking);
        outboxEventPublisher.publish(
                EventType.EVENT_CREATED,
                BuskingCreatedEventPayload.builder()
                        .buskingId(busking.getId())
                        .title(busking.getTitle())
                        .description(busking.getDescription())
                        .location(busking.getLocation())
                        .startTime(busking.getStartTime())
                        .endTime(busking.getEndTime())
                        .totalInventoryCount(busking.getTotalInventoryCount())
                        .build(),
                savedBusking.getId()
        );

        return BuskingResponseDto.from(savedBusking, savedBusking.getTotalInventoryCount());
    }

    @Transactional(readOnly = true)
    public List<BuskingResponseDto> getAll() {
        return buskingRepository.findAll().stream()
                .map(e -> BuskingResponseDto.from(e, inventoryPort.countsInventory(e.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public BuskingResponseDto getOne(Long buskingId) {
        Integer availableInventoryCount = inventoryPort.countsInventory(buskingId);

        return buskingRepository.findById(buskingId)
                .map(e -> BuskingResponseDto.from(e, availableInventoryCount))
                .orElseThrow(() -> new CustomException(ErrorCode.BUSKING_NOT_FOUND));
    }

    @Transactional
    public void update(Long id, BuskingUpdateRequestDto request) {
        Busking event = buskingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BUSKING_NOT_FOUND));

        event.update(
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
