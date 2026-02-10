package ticket.reserve.busking.application.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.BuskingPublishService;
import ticket.reserve.busking.application.BuskingQueryService;
import ticket.reserve.busking.application.BuskingService;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.busking.application.port.out.NotificationSchedulePort;
import ticket.reserve.busking.application.port.out.SubscriptionPort;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.busking.domain.buskingimage.enums.ImageType;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuskingServiceImpl implements BuskingService {

    private final IdGenerator idGenerator;
    private final BuskingPublishService buskingPublishService;
    private final BuskingQueryService buskingQueryService;
    private final BuskingRepository buskingRepository;
    private final NotificationSchedulePort notificationSchedulePort;
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
            Busking savedBusking = buskingPublishService.publishBuskingCreatedEvent(busking);
            notificationSchedulePort.addToNotificationSchedule(
                    savedBusking.getId(),
                    savedBusking.getStartTime(),
                    savedBusking.getEndTime()
            );
            return BuskingResponseDto.from(savedBusking);
        } catch (Exception e) {
            log.error("[BuskingService.create] 버스킹 생성 실패", e);
            if (imageResponse != null) {
                try {
                    imagePort.deleteImage(imageResponse.getImageId());
                } catch (Exception deleteEx) {
                    log.error("[BuskingService.create] 이미지 삭제 실패: imageId={}", imageResponse.getImageId(), deleteEx);
                }
            }
            throw new CustomException(ErrorCode.BUSKING_CREATED_ERROR);
        }
    }

    public List<BuskingResponseDto> getAll() {
        return buskingRepository.findAll().stream()
                .map(BuskingResponseDto::from)
                .toList();
    }

    public BuskingResponseDto getOne(Long buskingId, Long userId) {
        boolean isSubscribed = subscriptionPort.isSubscribe(buskingId, userId);

        return BuskingResponseDto.from(
                buskingQueryService.findById(buskingId),
                isSubscribed
        );
    }

    @Transactional
    public void update(Long id, BuskingUpdateRequestDto request) {
        Busking busking = buskingQueryService.findById(id);
        busking.update(
                request.title(), request.description(), request.location(),
                request.startTime(), request.endTime()
        );
    }

    @Transactional(readOnly = true)
    public List<BuskingResponseDto> findAllByBulk(List<Long> buskingIds) {
        return buskingRepository.findAllByIdIn(buskingIds).stream()
                .map(BuskingResponseDto::from)
                .toList();
    }
}
