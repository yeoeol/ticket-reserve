package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.port.out.RedisPort;
import ticket.reserve.busking.application.port.out.SubscriptionPort;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.buskingimage.enums.ImageType;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuskingService {

    private final IdGenerator idGenerator;
    private final BuskingCrudService buskingCrudService;
    private final RedisPort redisPort;
    private final InventoryPort inventoryPort;
    private final ImagePort imagePort;
    private final SubscriptionPort subscriptionPort;

    public BuskingResponseDto create(BuskingRequestDto request, MultipartFile file) {
        Busking busking = request.toEntity(idGenerator);

        // 이미지가 있다면 저장
        ImageResponseDto imageResponse = null;
        if (file != null && !file.isEmpty()) {
            imageResponse = imagePort.uploadImage(file);
            if (imageResponse != null) {
                busking.addEventImage(
                        idGenerator, imageResponse.getOriginalFileName(), imageResponse.getStoredPath(),
                        ImageType.THUMBNAIL, 1
                );
            }
            // TODO: imageResponse가 null일 때 재시도 로직 구현
        }

        // 버스킹 저장
        try {
            Busking savedBusking = buskingCrudService.save(busking);
            redisPort.addToNotificationSchedule(busking.getId(), busking.getStartTime());
            return BuskingResponseDto.from(savedBusking, savedBusking.getTotalInventoryCount());
        } catch (Exception e) {
            if (imageResponse != null) {
                imagePort.deleteImage(imageResponse.getImageId());
            }
            throw new CustomException(ErrorCode.IMAGE_DELETE_FAIL);
        }
    }

    public List<BuskingResponseDto> getAll() {
        return buskingCrudService.findAll().stream()
                .map(e -> BuskingResponseDto.from(e, inventoryPort.countInventory(e.getId())))
                .toList();
    }

    public BuskingResponseDto getOne(Long buskingId, Long userId) {
        Integer availableInventoryCount = inventoryPort.countInventory(buskingId);
        boolean isSubscribed = subscriptionPort.isSubscribe(buskingId, userId);

        return BuskingResponseDto.from(
                buskingCrudService.findById(buskingId),
                availableInventoryCount,
                isSubscribed
        );
    }

    @Transactional(readOnly = true)
    public List<BuskingResponseDto> findAllByBulk(List<Long> buskingIds, Long userId) {
        return buskingCrudService.findAllByBulk(buskingIds).stream()
                .map(busking -> {
                    Boolean isSubscribed = subscriptionPort.isSubscribe(busking.getId(), userId);
                    return BuskingResponseDto.from(busking, 0, isSubscribed);
                }).toList();
    }
}
